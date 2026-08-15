package com.example.ime

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.InputConnection
import com.example.model.ClipboardOp
import com.example.model.CursorDirection
import com.example.model.KeyAction
import com.example.model.KeyboardSettings
import com.example.model.ModifierKey

class InputConnectionHandler(
    private val context: Context,
    private val getCurrentInputConnection: () -> InputConnection?
) {

    fun executeAction(
        action: KeyAction,
        settings: KeyboardSettings,
        activeModifiers: Set<ModifierKey> = emptySet(),
        onModifierConsumed: (ModifierKey) -> Unit = {}
    ) {
        val ic = getCurrentInputConnection() ?: return

        val isCtrlActive = activeModifiers.contains(ModifierKey.CTRL)
        val isShiftActive = activeModifiers.contains(ModifierKey.SHIFT) || activeModifiers.contains(ModifierKey.CAPS_LOCK)

        when (action) {
            is KeyAction.InsertText -> {
                var text = action.text

                // If CTRL is active, interpret key shortcuts
                if (isCtrlActive && text.length == 1) {
                    val char = text.lowercase()[0]
                    handleCtrlShortcut(char, ic)
                    onModifierConsumed(ModifierKey.CTRL)
                    return
                }

                if (isShiftActive && text.length == 1) {
                    text = text.uppercase()
                } else if (settings.autoCapitalize && text.length == 1 && text[0].isLetter()) {
                    val textBefore = ic.getTextBeforeCursor(80, 0)
                    if (com.example.model.AutoCapitalizationHelper.shouldAutoCapitalize(textBefore)) {
                        text = text.uppercase()
                    }
                }

                // 1. Smart step-over if typing closing bracket/quote and it's already directly ahead
                val isClosingBracket = text == ")" || text == "}" || text == "]" || text == ">"
                val isQuote = text == "\"" || text == "'" || text == "`"
                val textAfter = ic.getTextAfterCursor(1, 0)?.toString() ?: ""

                if (settings.smartBracketStepOver && (isClosingBracket || isQuote) && textAfter == text) {
                    moveCursor(CursorDirection.RIGHT, ic)
                    if (isShiftActive && !activeModifiers.contains(ModifierKey.CAPS_LOCK)) {
                        onModifierConsumed(ModifierKey.SHIFT)
                    }
                    return
                }

                // 2. Auto-closing brackets & quotes check
                val isOpeningBracket = text == "{" || text == "(" || text == "[" || text == "<"
                val shouldPairBracket = settings.autoCloseBrackets && isOpeningBracket
                val shouldPairQuote = settings.autoCloseQuotes && isQuote

                if (shouldPairBracket || shouldPairQuote) {
                    val openCloseMap = mapOf(
                        "{" to Pair("{}", 1),
                        "(" to Pair("()", 1),
                        "[" to Pair("[]", 1),
                        "<" to Pair("<>", 1),
                        "\"" to Pair("\"\"", 1),
                        "'" to Pair("''", 1),
                        "`" to Pair("``", 1)
                    )
                    val pair = openCloseMap[text]
                    if (pair != null) {
                        ic.commitText(pair.first, 1)
                        // Move cursor back inside the pair
                        moveCursor(CursorDirection.LEFT, ic)
                        if (isShiftActive && !activeModifiers.contains(ModifierKey.CAPS_LOCK)) {
                            onModifierConsumed(ModifierKey.SHIFT)
                        }
                        return
                    }
                }

                ic.commitText(text, 1)
                if (action.moveCursorBack > 0) {
                    for (i in 0 until action.moveCursorBack) {
                        moveCursor(CursorDirection.LEFT, ic)
                    }
                }

                if (isShiftActive && !activeModifiers.contains(ModifierKey.CAPS_LOCK)) {
                    onModifierConsumed(ModifierKey.SHIFT)
                }
            }

            is KeyAction.Tab -> {
                val tabString = if (settings.tabSpaces <= 0) "\t" else " ".repeat(settings.tabSpaces)
                ic.commitText(tabString, 1)
            }

            is KeyAction.Escape -> {
                sendKeyEvent(KeyEvent.KEYCODE_ESCAPE, ic)
            }

            is KeyAction.Backspace -> {
                if (isCtrlActive) {
                    deleteWordBeforeCursor(ic)
                    onModifierConsumed(ModifierKey.CTRL)
                } else {
                    val selectedText = ic.getSelectedText(0)
                    if (selectedText.isNullOrEmpty()) {
                        if (settings.smartBackspacePair) {
                            val charBefore = ic.getTextBeforeCursor(1, 0)?.toString() ?: ""
                            val charAfter = ic.getTextAfterCursor(1, 0)?.toString() ?: ""
                            val isBracketPair = (charBefore == "(" && charAfter == ")") ||
                                                (charBefore == "{" && charAfter == "}") ||
                                                (charBefore == "[" && charAfter == "]") ||
                                                (charBefore == "<" && charAfter == ">")
                            val isQuotePair = (charBefore == "\"" && charAfter == "\"") ||
                                              (charBefore == "'" && charAfter == "'") ||
                                              (charBefore == "`" && charAfter == "`")
                            if (isBracketPair || isQuotePair) {
                                ic.deleteSurroundingText(1, 1)
                                return
                            }
                        }
                        ic.deleteSurroundingText(1, 0)
                    } else {
                        ic.commitText("", 1)
                    }
                }
            }

            is KeyAction.Enter -> {
                if (settings.autoIndentOnEnter) {
                    val textBefore = ic.getTextBeforeCursor(250, 0)?.toString() ?: ""
                    val textAfter = ic.getTextAfterCursor(20, 0)?.toString() ?: ""
                    val result = com.example.model.AutoIndentHelper.calculateEnterInsertion(
                        textBeforeCursor = textBefore,
                        textAfterCursor = textAfter,
                        tabSpaces = settings.tabSpaces
                    )
                    ic.commitText(result.insertText, 1)
                    if (result.moveCursorBack > 0) {
                        for (i in 0 until result.moveCursorBack) {
                            moveCursor(CursorDirection.LEFT, ic)
                        }
                    }
                } else {
                    sendKeyEvent(KeyEvent.KEYCODE_ENTER, ic)
                }
            }

            is KeyAction.Space -> {
                ic.commitText(" ", 1)
            }

            is KeyAction.SendKeyCode -> {
                sendKeyEvent(action.keyCode, ic)
            }

            is KeyAction.MoveCursor -> {
                if (isCtrlActive) {
                    when (action.direction) {
                        CursorDirection.LEFT -> moveCursor(CursorDirection.LINE_START, ic)
                        CursorDirection.RIGHT -> moveCursor(CursorDirection.LINE_END, ic)
                        CursorDirection.UP -> moveCursor(CursorDirection.PAGE_UP, ic)
                        CursorDirection.DOWN -> moveCursor(CursorDirection.PAGE_DOWN, ic)
                        else -> moveCursor(action.direction, ic)
                    }
                    onModifierConsumed(ModifierKey.CTRL)
                } else {
                    moveCursor(action.direction, ic)
                }
            }

            is KeyAction.Clipboard -> {
                handleClipboardOp(action.op, ic)
            }

            else -> {}
        }
    }

    private fun handleCtrlShortcut(char: Char, ic: InputConnection) {
        when (char) {
            'a' -> handleClipboardOp(ClipboardOp.SELECT_ALL, ic)
            'c' -> handleClipboardOp(ClipboardOp.COPY, ic)
            'v' -> handleClipboardOp(ClipboardOp.PASTE, ic)
            'x' -> handleClipboardOp(ClipboardOp.CUT, ic)
            'z' -> handleClipboardOp(ClipboardOp.UNDO, ic)
            'y' -> handleClipboardOp(ClipboardOp.REDO, ic)
            'd' -> handleClipboardOp(ClipboardOp.DUPLICATE_LINE, ic)
            'k' -> handleClipboardOp(ClipboardOp.DELETE_LINE, ic)
            'w' -> deleteWordBeforeCursor(ic)
            else -> {
                val keyEventDown = KeyEvent(
                    0, 0, KeyEvent.ACTION_DOWN,
                    charToKeyCode(char), 0, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
                )
                val keyEventUp = KeyEvent(
                    0, 0, KeyEvent.ACTION_UP,
                    charToKeyCode(char), 0, KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
                )
                ic.sendKeyEvent(keyEventDown)
                ic.sendKeyEvent(keyEventUp)
            }
        }
    }

    fun handleClipboardOp(op: ClipboardOp, ic: InputConnection) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        when (op) {
            ClipboardOp.SELECT_ALL -> {
                ic.performContextMenuAction(android.R.id.selectAll)
            }
            ClipboardOp.CUT -> {
                val selected = ic.getSelectedText(0)
                if (!selected.isNullOrEmpty()) {
                    clipboard?.setPrimaryClip(ClipData.newPlainText("code", selected))
                    ic.commitText("", 1)
                } else {
                    ic.performContextMenuAction(android.R.id.cut)
                }
            }
            ClipboardOp.COPY -> {
                val selected = ic.getSelectedText(0)
                if (!selected.isNullOrEmpty()) {
                    clipboard?.setPrimaryClip(ClipData.newPlainText("code", selected))
                } else {
                    ic.performContextMenuAction(android.R.id.copy)
                }
            }
            ClipboardOp.PASTE -> {
                val clip = clipboard?.primaryClip
                if (clip != null && clip.itemCount > 0) {
                    val text = clip.getItemAt(0).coerceToText(context).toString()
                    if (text.isNotEmpty()) {
                        ic.commitText(text, 1)
                        return
                    }
                }
                ic.performContextMenuAction(android.R.id.paste)
            }
            ClipboardOp.UNDO -> {
                ic.performContextMenuAction(android.R.id.undo)
            }
            ClipboardOp.REDO -> {
                ic.performContextMenuAction(android.R.id.redo)
            }
            ClipboardOp.DELETE_LINE -> {
                val textBefore = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
                val textAfter = ic.getTextAfterCursor(200, 0)?.toString() ?: ""
                val lastNewline = textBefore.lastIndexOf('\n')
                val nextNewline = textAfter.indexOf('\n')
                val beforeCount = if (lastNewline >= 0) textBefore.length - 1 - lastNewline else textBefore.length
                val afterCount = if (nextNewline >= 0) nextNewline + 1 else textAfter.length
                ic.deleteSurroundingText(beforeCount, afterCount)
            }
            ClipboardOp.DUPLICATE_LINE -> {
                val textBefore = ic.getTextBeforeCursor(200, 0)?.toString() ?: ""
                val lastNewline = textBefore.lastIndexOf('\n')
                val currentLine = if (lastNewline >= 0) textBefore.substring(lastNewline + 1) else textBefore
                ic.commitText("\n" + currentLine, 1)
            }
        }
    }

    fun moveCursor(direction: CursorDirection, ic: InputConnection) {
        when (direction) {
            CursorDirection.LEFT -> sendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT, ic)
            CursorDirection.RIGHT -> sendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT, ic)
            CursorDirection.UP -> sendKeyEvent(KeyEvent.KEYCODE_DPAD_UP, ic)
            CursorDirection.DOWN -> sendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN, ic)
            CursorDirection.LINE_START -> sendKeyEvent(KeyEvent.KEYCODE_MOVE_HOME, ic)
            CursorDirection.LINE_END -> sendKeyEvent(KeyEvent.KEYCODE_MOVE_END, ic)
            CursorDirection.PAGE_UP -> sendKeyEvent(KeyEvent.KEYCODE_PAGE_UP, ic)
            CursorDirection.PAGE_DOWN -> sendKeyEvent(KeyEvent.KEYCODE_PAGE_DOWN, ic)
        }
    }

    private fun deleteWordBeforeCursor(ic: InputConnection) {
        val textBefore = ic.getTextBeforeCursor(50, 0)?.toString() ?: ""
        if (textBefore.isEmpty()) return
        var deleteCount = 0
        var i = textBefore.length - 1

        // Skip trailing spaces
        while (i >= 0 && textBefore[i] == ' ') {
            deleteCount++
            i--
        }

        // Delete word characters
        if (i >= 0) {
            val isAlphaNum = textBefore[i].isLetterOrDigit() || textBefore[i] == '_'
            while (i >= 0) {
                val currentAlphaNum = textBefore[i].isLetterOrDigit() || textBefore[i] == '_'
                if (currentAlphaNum == isAlphaNum && textBefore[i] != ' ') {
                    deleteCount++
                    i--
                } else {
                    break
                }
            }
        }
        ic.deleteSurroundingText(deleteCount.coerceAtLeast(1), 0)
    }

    private fun sendKeyEvent(keyCode: Int, ic: InputConnection) {
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, keyCode))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, keyCode))
    }

    private fun charToKeyCode(char: Char): Int {
        return when (char.lowercaseChar()) {
            'a' -> KeyEvent.KEYCODE_A
            'b' -> KeyEvent.KEYCODE_B
            'c' -> KeyEvent.KEYCODE_C
            'd' -> KeyEvent.KEYCODE_D
            'e' -> KeyEvent.KEYCODE_E
            'f' -> KeyEvent.KEYCODE_F
            'g' -> KeyEvent.KEYCODE_G
            'h' -> KeyEvent.KEYCODE_H
            'i' -> KeyEvent.KEYCODE_I
            'j' -> KeyEvent.KEYCODE_J
            'k' -> KeyEvent.KEYCODE_K
            'l' -> KeyEvent.KEYCODE_L
            'm' -> KeyEvent.KEYCODE_M
            'n' -> KeyEvent.KEYCODE_N
            'o' -> KeyEvent.KEYCODE_O
            'p' -> KeyEvent.KEYCODE_P
            'q' -> KeyEvent.KEYCODE_Q
            'r' -> KeyEvent.KEYCODE_R
            's' -> KeyEvent.KEYCODE_S
            't' -> KeyEvent.KEYCODE_T
            'u' -> KeyEvent.KEYCODE_U
            'v' -> KeyEvent.KEYCODE_V
            'w' -> KeyEvent.KEYCODE_W
            'x' -> KeyEvent.KEYCODE_X
            'y' -> KeyEvent.KEYCODE_Y
            'z' -> KeyEvent.KEYCODE_Z
            else -> KeyEvent.KEYCODE_UNKNOWN
        }
    }
}
