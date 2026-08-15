package com.example.model

object KeyboardLayouts {

    data class CoderBarItem(
        val label: String,
        val shiftedLabel: String,
        val insertText: String,
        val cursorOffset: Int = 0
    )

    val coderBarShortcuts = listOf(
        CoderBarItem("{", "}", "{}", 1),
        CoderBarItem("(", ")", "()", 1),
        CoderBarItem("[", "]", "[]", 1),
        CoderBarItem("<", ">", "<>", 1),
        CoderBarItem(";", ";", ";", 0),
        CoderBarItem(":", ":", ":", 0),
        CoderBarItem("=", "=", "=", 0),
        CoderBarItem("=>", "=>", " => ", 0),
        CoderBarItem("->", "->", " -> ", 0),
        CoderBarItem("==", "===", " === ", 0),
        CoderBarItem("!=", "!==", " !== ", 0),
        CoderBarItem("&&", "&&", " && ", 0),
        CoderBarItem("||", "||", " || ", 0),
        CoderBarItem("\"", "\"", "\"\"", 1),
        CoderBarItem("'", "'", "''", 1),
        CoderBarItem("`", "`", "``", 1),
        CoderBarItem("$", "$", "$", 0),
        CoderBarItem("#", "#", "#", 0),
        CoderBarItem(".", ".", ".", 0),
        CoderBarItem("?", "?", "?", 0),
        CoderBarItem("!", "!", "!", 0),
        CoderBarItem("/", "//", "// ", 0),
        CoderBarItem("/*", "*/", "/*  */", 3),
        CoderBarItem("\\", "\\", "\\", 0),
        CoderBarItem("|", "|", "|", 0),
        CoderBarItem("~", "~", "~", 0),
        CoderBarItem("&", "&", "&", 0),
        CoderBarItem("%", "%", "%", 0),
        CoderBarItem("+", "+", "+", 0),
        CoderBarItem("-", "-", "-", 0),
        CoderBarItem("*", "*", "*", 0)
    )

    // ==========================================
    // PAGE 1: STREAMLINED MAIN QWERTY
    // 5 Rows: Ergonomic, spacious, and compact
    // ==========================================

    // Row 1: Esc, #, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, ⬍ (Page Switcher)
    val page1Row1 = listOf(
        KeyModel(primaryLabel = "Esc", action = KeyAction.Escape, weight = 1.25f, isModifier = true),
        KeyModel(primaryLabel = "#", action = KeyAction.InsertText("#"), weight = 1.0f),
        KeyModel(primaryLabel = "1", action = KeyAction.InsertText("1"), weight = 1.0f),
        KeyModel(primaryLabel = "2", action = KeyAction.InsertText("2"), weight = 1.0f),
        KeyModel(primaryLabel = "3", action = KeyAction.InsertText("3"), weight = 1.0f),
        KeyModel(primaryLabel = "4", action = KeyAction.InsertText("4"), weight = 1.0f),
        KeyModel(primaryLabel = "5", action = KeyAction.InsertText("5"), weight = 1.0f),
        KeyModel(primaryLabel = "6", action = KeyAction.InsertText("6"), weight = 1.0f),
        KeyModel(primaryLabel = "7", action = KeyAction.InsertText("7"), weight = 1.0f),
        KeyModel(primaryLabel = "8", action = KeyAction.InsertText("8"), weight = 1.0f),
        KeyModel(primaryLabel = "9", action = KeyAction.InsertText("9"), weight = 1.0f),
        KeyModel(primaryLabel = "0", action = KeyAction.InsertText("0"), weight = 1.0f),
        KeyModel(primaryLabel = "⬍", action = KeyAction.SwitchPage(KeyboardPage.PAGE_2), weight = 1.1f, iconType = KeyIconType.ARROW_UP_DOWN)
    )

    // Row 2: q, w, e, r, t, y, u, i, o, p
    val page1Row2 = listOf(
        KeyModel(primaryLabel = "q", secondaryLabel = "1", action = KeyAction.InsertText("q")),
        KeyModel(primaryLabel = "w", secondaryLabel = "2", action = KeyAction.InsertText("w")),
        KeyModel(primaryLabel = "e", secondaryLabel = "3", action = KeyAction.InsertText("e")),
        KeyModel(primaryLabel = "r", secondaryLabel = "4", action = KeyAction.InsertText("r")),
        KeyModel(primaryLabel = "t", secondaryLabel = "5", action = KeyAction.InsertText("t")),
        KeyModel(primaryLabel = "y", secondaryLabel = "6", action = KeyAction.InsertText("y")),
        KeyModel(primaryLabel = "u", secondaryLabel = "7", action = KeyAction.InsertText("u")),
        KeyModel(primaryLabel = "i", secondaryLabel = "8", action = KeyAction.InsertText("i")),
        KeyModel(primaryLabel = "o", secondaryLabel = "9", action = KeyAction.InsertText("o")),
        KeyModel(primaryLabel = "p", secondaryLabel = "0", action = KeyAction.InsertText("p"))
    )

    // Row 3: a, s, d, f, g, h, j, k, l
    val page1Row3 = listOf(
        KeyModel(primaryLabel = "a", secondaryLabel = "@", action = KeyAction.InsertText("a")),
        KeyModel(primaryLabel = "s", secondaryLabel = "#", action = KeyAction.InsertText("s")),
        KeyModel(primaryLabel = "d", secondaryLabel = "$", action = KeyAction.InsertText("d")),
        KeyModel(primaryLabel = "f", secondaryLabel = "_", action = KeyAction.InsertText("f")),
        KeyModel(primaryLabel = "g", secondaryLabel = "&", action = KeyAction.InsertText("g")),
        KeyModel(primaryLabel = "h", secondaryLabel = "-", action = KeyAction.InsertText("h")),
        KeyModel(primaryLabel = "j", secondaryLabel = "+", action = KeyAction.InsertText("j")),
        KeyModel(primaryLabel = "k", secondaryLabel = "(", action = KeyAction.InsertText("k")),
        KeyModel(primaryLabel = "l", secondaryLabel = ")", action = KeyAction.InsertText("l"))
    )

    // Row 4: Shift, z, x, c, v, b, n, m, Backspace
    val page1Row4 = listOf(
        KeyModel(primaryLabel = "⇧", action = KeyAction.ToggleModifier(ModifierKey.SHIFT), weight = 1.4f, isModifier = true, iconType = KeyIconType.SHIFT),
        KeyModel(primaryLabel = "z", secondaryLabel = "*", action = KeyAction.InsertText("z")),
        KeyModel(primaryLabel = "x", secondaryLabel = "<", action = KeyAction.InsertText("x")),
        KeyModel(primaryLabel = "c", secondaryLabel = ">", action = KeyAction.InsertText("c")),
        KeyModel(primaryLabel = "v", secondaryLabel = "/", action = KeyAction.InsertText("v")),
        KeyModel(primaryLabel = "b", secondaryLabel = "\\", action = KeyAction.InsertText("b")),
        KeyModel(primaryLabel = "n", secondaryLabel = "=", action = KeyAction.InsertText("n")),
        KeyModel(primaryLabel = "m", secondaryLabel = "%", action = KeyAction.InsertText("m")),
        KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.4f, isDanger = true, iconType = KeyIconType.BACKSPACE)
    )

    // Row 5: Ctrl, Tab, :, ,, ␣ (Space), ., ;, ↵ (Enter)
    val page1Row5 = listOf(
        KeyModel(primaryLabel = "Ctrl", action = KeyAction.ToggleModifier(ModifierKey.CTRL), weight = 1.35f, isModifier = true),
        KeyModel(primaryLabel = "Tab", action = KeyAction.Tab, weight = 1.35f, isModifier = true),
        KeyModel(primaryLabel = ":", action = KeyAction.InsertText(":"), weight = 1.0f),
        KeyModel(primaryLabel = ",", action = KeyAction.InsertText(","), weight = 1.0f),
        KeyModel(primaryLabel = "␣", action = KeyAction.Space, weight = 3.8f, iconType = KeyIconType.SPACE),
        KeyModel(primaryLabel = ".", action = KeyAction.InsertText("."), weight = 1.0f),
        KeyModel(primaryLabel = ";", action = KeyAction.InsertText(";"), weight = 1.0f),
        KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.4f, isAccent = true, iconType = KeyIconType.ENTER)
    )

    // Row 5 Compact (When dedicated number row is disabled in Settings):
    // Integrates Esc and Page Switcher (⬍) cleanly into the bottom bar
    val page1Row5Compact = listOf(
        KeyModel(primaryLabel = "Esc", action = KeyAction.Escape, weight = 1.15f, isModifier = true),
        KeyModel(primaryLabel = "⬍", action = KeyAction.SwitchPage(KeyboardPage.PAGE_2), weight = 1.0f, iconType = KeyIconType.ARROW_UP_DOWN),
        KeyModel(primaryLabel = "Ctrl", action = KeyAction.ToggleModifier(ModifierKey.CTRL), weight = 1.15f, isModifier = true),
        KeyModel(primaryLabel = "Tab", action = KeyAction.Tab, weight = 1.15f, isModifier = true),
        KeyModel(primaryLabel = "␣", action = KeyAction.Space, weight = 3.4f, iconType = KeyIconType.SPACE),
        KeyModel(primaryLabel = ",", action = KeyAction.InsertText(","), weight = 0.9f),
        KeyModel(primaryLabel = ".", action = KeyAction.InsertText("."), weight = 0.9f),
        KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.35f, isAccent = true, iconType = KeyIconType.ENTER)
    )


    // ==========================================
    // PAGE 2: SYMBOLS, BRACKETS & PROGRAMMING COMPULSORIES
    // (Contains the symbols moved from Page 1 + essential navigation & syntax)
    // ==========================================

    // Row 1: Esc, #, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, ⬍ (Page Switcher back to Page 1)
    val page2Row1 = listOf(
        KeyModel(primaryLabel = "Esc", action = KeyAction.Escape, weight = 1.25f, isModifier = true),
        KeyModel(primaryLabel = "#", action = KeyAction.InsertText("#"), weight = 1.0f),
        KeyModel(primaryLabel = "1", action = KeyAction.InsertText("1"), weight = 1.0f),
        KeyModel(primaryLabel = "2", action = KeyAction.InsertText("2"), weight = 1.0f),
        KeyModel(primaryLabel = "3", action = KeyAction.InsertText("3"), weight = 1.0f),
        KeyModel(primaryLabel = "4", action = KeyAction.InsertText("4"), weight = 1.0f),
        KeyModel(primaryLabel = "5", action = KeyAction.InsertText("5"), weight = 1.0f),
        KeyModel(primaryLabel = "6", action = KeyAction.InsertText("6"), weight = 1.0f),
        KeyModel(primaryLabel = "7", action = KeyAction.InsertText("7"), weight = 1.0f),
        KeyModel(primaryLabel = "8", action = KeyAction.InsertText("8"), weight = 1.0f),
        KeyModel(primaryLabel = "9", action = KeyAction.InsertText("9"), weight = 1.0f),
        KeyModel(primaryLabel = "0", action = KeyAction.InsertText("0"), weight = 1.0f),
        KeyModel(primaryLabel = "⬍", action = KeyAction.SwitchPage(KeyboardPage.PAGE_1), weight = 1.1f, iconType = KeyIconType.ARROW_UP_DOWN)
    )

    // Row 2 (Moved from Page 1 Row 2): ", ', %, `, ?, ^, _, ~, $, @, -, *, +, =
    val page2Row2 = listOf(
        KeyModel(primaryLabel = "\"", action = KeyAction.InsertText("\"")),
        KeyModel(primaryLabel = "'", action = KeyAction.InsertText("'")),
        KeyModel(primaryLabel = "%", action = KeyAction.InsertText("%")),
        KeyModel(primaryLabel = "`", action = KeyAction.InsertText("`")),
        KeyModel(primaryLabel = "?", action = KeyAction.InsertText("?")),
        KeyModel(primaryLabel = "^", action = KeyAction.InsertText("^")),
        KeyModel(primaryLabel = "_", action = KeyAction.InsertText("_")),
        KeyModel(primaryLabel = "~", action = KeyAction.InsertText("~")),
        KeyModel(primaryLabel = "$", action = KeyAction.InsertText("$")),
        KeyModel(primaryLabel = "@", action = KeyAction.InsertText("@")),
        KeyModel(primaryLabel = "-", action = KeyAction.InsertText("-")),
        KeyModel(primaryLabel = "*", action = KeyAction.InsertText("*")),
        KeyModel(primaryLabel = "+", action = KeyAction.InsertText("+")),
        KeyModel(primaryLabel = "=", action = KeyAction.InsertText("="))
    )

    // Row 3 (Moved from Page 1 Row 3): {, (, [, <, /, !, &, |, \, >, ], ), }
    val page2Row3 = listOf(
        KeyModel(primaryLabel = "{", action = KeyAction.InsertText("{")),
        KeyModel(primaryLabel = "(", action = KeyAction.InsertText("(")),
        KeyModel(primaryLabel = "[", action = KeyAction.InsertText("[")),
        KeyModel(primaryLabel = "<", action = KeyAction.InsertText("<")),
        KeyModel(primaryLabel = "/", action = KeyAction.InsertText("/")),
        KeyModel(primaryLabel = "!", action = KeyAction.InsertText("!")),
        KeyModel(primaryLabel = "&", action = KeyAction.InsertText("&")),
        KeyModel(primaryLabel = "|", action = KeyAction.InsertText("|")),
        KeyModel(primaryLabel = "\\", action = KeyAction.InsertText("\\")),
        KeyModel(primaryLabel = ">", action = KeyAction.InsertText(">")),
        KeyModel(primaryLabel = "]", action = KeyAction.InsertText("]")),
        KeyModel(primaryLabel = ")", action = KeyAction.InsertText(")")),
        KeyModel(primaryLabel = "}", action = KeyAction.InsertText("}"))
    )

    // Row 4: Compulsary Coding Operators & Arrow Navigation
    val page2Row4 = listOf(
        KeyModel(primaryLabel = "=>", action = KeyAction.InsertText(" => ")),
        KeyModel(primaryLabel = "->", action = KeyAction.InsertText(" -> ")),
        KeyModel(primaryLabel = "==", action = KeyAction.InsertText(" == ")),
        KeyModel(primaryLabel = "!=", action = KeyAction.InsertText(" != ")),
        KeyModel(primaryLabel = "&&", action = KeyAction.InsertText(" && ")),
        KeyModel(primaryLabel = "||", action = KeyAction.InsertText(" || ")),
        KeyModel(primaryLabel = "◀", action = KeyAction.MoveCursor(CursorDirection.LEFT), weight = 1.1f, iconType = KeyIconType.ARROW_LEFT),
        KeyModel(primaryLabel = "▲", action = KeyAction.MoveCursor(CursorDirection.UP), weight = 1.1f, iconType = KeyIconType.ARROW_UP),
        KeyModel(primaryLabel = "▼", action = KeyAction.MoveCursor(CursorDirection.DOWN), weight = 1.1f, iconType = KeyIconType.ARROW_DOWN),
        KeyModel(primaryLabel = "▶", action = KeyAction.MoveCursor(CursorDirection.RIGHT), weight = 1.1f, iconType = KeyIconType.ARROW_RIGHT)
    )

    // Row 5: Editing & Clipboard Actions
    val page2Row5 = listOf(
        KeyModel(primaryLabel = "Select All", action = KeyAction.Clipboard(ClipboardOp.SELECT_ALL), weight = 1.3f, isModifier = true),
        KeyModel(primaryLabel = "Copy", action = KeyAction.Clipboard(ClipboardOp.COPY), weight = 1.0f),
        KeyModel(primaryLabel = "Cut", action = KeyAction.Clipboard(ClipboardOp.CUT), weight = 1.0f),
        KeyModel(primaryLabel = "Paste", action = KeyAction.Clipboard(ClipboardOp.PASTE), weight = 1.0f),
        KeyModel(primaryLabel = "Undo", action = KeyAction.Clipboard(ClipboardOp.UNDO), weight = 1.0f),
        KeyModel(primaryLabel = "Redo", action = KeyAction.Clipboard(ClipboardOp.REDO), weight = 1.0f),
        KeyModel(primaryLabel = "⌫", action = KeyAction.Backspace, weight = 1.3f, isDanger = true, iconType = KeyIconType.BACKSPACE)
    )

    // Row 6: Bottom Bar
    val page2Row6 = listOf(
        KeyModel(primaryLabel = "Ctrl", action = KeyAction.ToggleModifier(ModifierKey.CTRL), weight = 1.35f, isModifier = true),
        KeyModel(primaryLabel = "Tab", action = KeyAction.Tab, weight = 1.35f, isModifier = true),
        KeyModel(primaryLabel = ":", action = KeyAction.InsertText(":"), weight = 1.0f),
        KeyModel(primaryLabel = ",", action = KeyAction.InsertText(","), weight = 1.0f),
        KeyModel(primaryLabel = "␣", action = KeyAction.Space, weight = 3.8f, iconType = KeyIconType.SPACE),
        KeyModel(primaryLabel = ".", action = KeyAction.InsertText("."), weight = 1.0f),
        KeyModel(primaryLabel = ";", action = KeyAction.InsertText(";"), weight = 1.0f),
        KeyModel(primaryLabel = "↵", action = KeyAction.Enter, weight = 1.4f, isAccent = true, iconType = KeyIconType.ENTER)
    )
}
