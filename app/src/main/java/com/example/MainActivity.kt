package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.model.ClipboardOp
import com.example.model.CursorDirection
import com.example.model.KeyAction
import com.example.model.KeyboardPreferences
import com.example.model.KeyboardSettings
import com.example.model.KeyboardTheme
import com.example.ui.CodeKeyboardView
import com.example.ui.KeyboardSettingsSheet
import com.example.ui.ThemeSelectorSheet

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferences = KeyboardPreferences.getInstance(this)

        setContent {
            DreamBoardApp(
                preferences = preferences,
                onOpenInputSettings = {
                    try {
                        startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                onSwitchKeyboard = {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.showInputMethodPicker()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamBoardApp(
    preferences: KeyboardPreferences,
    onOpenInputSettings: () -> Unit,
    onSwitchKeyboard: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val settings by preferences.settings.collectAsState()
    val theme = KeyboardTheme.getThemeById(settings.themeId)

    var showThemeSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    val themeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Device IME Status Detection
    var isImeEnabled by remember { mutableStateOf(false) }
    var isImeDefault by remember { mutableStateOf(false) }

    fun refreshImeStatus() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        if (imm != null) {
            val enabledList = imm.enabledInputMethodList
            isImeEnabled = enabledList.any { it.packageName == context.packageName }
        }
        val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        isImeDefault = defaultIme != null && defaultIme.contains(context.packageName)
    }

    // Refresh when user returns to app from system settings
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshImeStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        refreshImeStatus()
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Active App Tab: 0 = Device Activation & System Test, 1 = In-App Code Playground
    var selectedTab by remember { mutableStateOf(0) }

    // System Test Field state
    var systemTestText by remember {
        mutableStateOf(
            TextFieldValue(
                text = "Tap here to summon DreamBoard system keyboard! Works in Termux, Chrome, Notes, and every Android app."
            )
        )
    }

    // In-App Playground state & history
    var selectedLanguage by remember { mutableStateOf("JavaScript") }
    var playgroundCode by remember {
        mutableStateOf(
            TextFieldValue(
                text = """// DreamBoard Interactive Playground
// Test shortcuts: Ctrl+V (Paste), Ctrl+C (Copy), Ctrl+A (All), Ctrl+Z (Undo)

function calculateMetrics(items) {
    const total = items.reduce((acc, x) => acc + x.val, 0);
    console.log(`Computed total: ${'$'}{total}`);
    return { count: items.length, total: total };
}

calculateMetrics([{ id: 1, val: 42 }, { id: 2, val: 58 }]);
""",
                selection = TextRange(0)
            )
        )
    }

    val undoStack = remember { mutableStateListOf<TextFieldValue>() }
    val redoStack = remember { mutableStateListOf<TextFieldValue>() }
    var consoleOutput by remember { mutableStateOf("Shortcuts: Ctrl+V (Paste), Ctrl+C (Copy), Ctrl+Z (Undo), Ctrl+D (Dup)") }

    fun updatePlaygroundCode(newValue: TextFieldValue, saveHistory: Boolean = true) {
        if (saveHistory && newValue.text != playgroundCode.text) {
            undoStack.add(playgroundCode)
            redoStack.clear()
            if (undoStack.size > 50) {
                undoStack.removeAt(0)
            }
        }
        playgroundCode = newValue
    }

    fun handlePlaygroundAction(action: KeyAction) {
        val currentText = playgroundCode.text
        val selStart = playgroundCode.selection.start
        val selEnd = playgroundCode.selection.end
        val minSel = minOf(selStart, selEnd)
        val maxSel = maxOf(selStart, selEnd)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager

        when (action) {
            is KeyAction.InsertText -> {
                var insertStr = action.text
                if (settings.autoCapitalize && insertStr.length == 1 && insertStr[0].isLetter()) {
                    val textBefore = currentText.substring(0, minSel)
                    if (com.example.model.AutoCapitalizationHelper.shouldAutoCapitalize(textBefore)) {
                        insertStr = insertStr.uppercase()
                    }
                }

                // 1. Smart step over
                val isClosingBracket = insertStr == ")" || insertStr == "}" || insertStr == "]" || insertStr == ">"
                val isQuote = insertStr == "\"" || insertStr == "'" || insertStr == "`"
                if (settings.smartBracketStepOver && (isClosingBracket || isQuote)) {
                    if (minSel == maxSel && minSel < currentText.length && currentText[minSel].toString() == insertStr) {
                        updatePlaygroundCode(playgroundCode.copy(selection = TextRange(minSel + 1)))
                        consoleOutput = "Step over: $insertStr"
                        return
                    }
                }

                // 2. Granular Bracket and Quote Pairing
                val isOpeningBracket = insertStr == "{" || insertStr == "(" || insertStr == "[" || insertStr == "<"
                val shouldPairBracket = settings.autoCloseBrackets && isOpeningBracket
                val shouldPairQuote = settings.autoCloseQuotes && isQuote

                if (shouldPairBracket || shouldPairQuote) {
                    val pairMap = mapOf("{" to "{}", "(" to "()", "[" to "[]", "<" to "<>", "\"" to "\"\"", "'" to "''", "`" to "``")
                    val pair = pairMap[insertStr]
                    if (pair != null) {
                        val newText = currentText.substring(0, minSel) + pair + currentText.substring(maxSel)
                        updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel + 1)))
                        consoleOutput = "Auto-closed: $pair"
                        return
                    }
                }

                val newText = currentText.substring(0, minSel) + insertStr + currentText.substring(maxSel)
                val cursorOffset = if (action.moveCursorBack > 0) {
                    (minSel + insertStr.length - action.moveCursorBack).coerceIn(0, newText.length)
                } else {
                    minSel + insertStr.length
                }
                updatePlaygroundCode(TextFieldValue(newText, TextRange(cursorOffset)))
                consoleOutput = "Typed: '$insertStr'"
            }

            is KeyAction.Tab -> {
                val tabStr = if (settings.tabSpaces <= 0) "\t" else " ".repeat(settings.tabSpaces)
                val newText = currentText.substring(0, minSel) + tabStr + currentText.substring(maxSel)
                updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel + tabStr.length)))
                consoleOutput = "TAB (${tabStr.length} space)"
            }

            is KeyAction.Backspace -> {
                if (minSel != maxSel) {
                    val newText = currentText.substring(0, minSel) + currentText.substring(maxSel)
                    updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel)))
                } else if (minSel > 0) {
                    if (settings.smartBackspacePair && minSel < currentText.length) {
                        val charBefore = currentText[minSel - 1].toString()
                        val charAfter = currentText[minSel].toString()
                        val isBracketPair = (charBefore == "(" && charAfter == ")") ||
                                            (charBefore == "{" && charAfter == "}") ||
                                            (charBefore == "[" && charAfter == "]") ||
                                            (charBefore == "<" && charAfter == ">")
                        val isQuotePair = (charBefore == "\"" && charAfter == "\"") ||
                                          (charBefore == "'" && charAfter == "'") ||
                                          (charBefore == "`" && charAfter == "`")
                        if (isBracketPair || isQuotePair) {
                            val newText = currentText.substring(0, minSel - 1) + currentText.substring(minSel + 1)
                            updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel - 1)))
                            consoleOutput = "Deleted delimiter pair"
                            return
                        }
                    }
                    val newText = currentText.substring(0, minSel - 1) + currentText.substring(minSel)
                    updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel - 1)))
                }
                consoleOutput = "Backspace"
            }

            is KeyAction.Enter -> {
                if (settings.autoIndentOnEnter) {
                    val textBefore = currentText.substring(0, minSel)
                    val textAfter = currentText.substring(maxSel)
                    val result = com.example.model.AutoIndentHelper.calculateEnterInsertion(
                        textBeforeCursor = textBefore,
                        textAfterCursor = textAfter,
                        tabSpaces = settings.tabSpaces
                    )
                    val newText = textBefore + result.insertText + textAfter
                    val newCursor = (minSel + result.insertText.length - result.moveCursorBack).coerceIn(0, newText.length)
                    updatePlaygroundCode(TextFieldValue(newText, TextRange(newCursor)))
                    consoleOutput = "Auto-Indented Newline"
                } else {
                    val newText = currentText.substring(0, minSel) + "\n" + currentText.substring(maxSel)
                    updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel + 1)))
                    consoleOutput = "Enter"
                }
            }

            is KeyAction.Space -> {
                val newText = currentText.substring(0, minSel) + " " + currentText.substring(maxSel)
                updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel + 1)))
            }

            is KeyAction.Escape -> {
                consoleOutput = "ESC: Selection cleared."
                playgroundCode = playgroundCode.copy(selection = TextRange(playgroundCode.selection.start))
            }

            is KeyAction.MoveCursor -> {
                when (action.direction) {
                    CursorDirection.LEFT -> {
                        val newPos = (playgroundCode.selection.start - 1).coerceAtLeast(0)
                        playgroundCode = playgroundCode.copy(selection = TextRange(newPos))
                    }
                    CursorDirection.RIGHT -> {
                        val newPos = (playgroundCode.selection.start + 1).coerceAtMost(currentText.length)
                        playgroundCode = playgroundCode.copy(selection = TextRange(newPos))
                    }
                    CursorDirection.LINE_START -> {
                        val lastNewline = currentText.lastIndexOf('\n', (playgroundCode.selection.start - 1).coerceAtLeast(0))
                        val lineStart = if (lastNewline >= 0) lastNewline + 1 else 0
                        playgroundCode = playgroundCode.copy(selection = TextRange(lineStart))
                    }
                    CursorDirection.LINE_END -> {
                        val nextNewline = currentText.indexOf('\n', playgroundCode.selection.start)
                        val lineEnd = if (nextNewline >= 0) nextNewline else currentText.length
                        playgroundCode = playgroundCode.copy(selection = TextRange(lineEnd))
                    }
                    CursorDirection.UP -> {
                        val lastNewline = currentText.lastIndexOf('\n', (playgroundCode.selection.start - 1).coerceAtLeast(0))
                        if (lastNewline >= 0) {
                            val lineStart = currentText.lastIndexOf('\n', lastNewline - 1)
                            val prevLineStart = if (lineStart >= 0) lineStart + 1 else 0
                            playgroundCode = playgroundCode.copy(selection = TextRange(prevLineStart))
                        }
                    }
                    CursorDirection.DOWN -> {
                        val nextNewline = currentText.indexOf('\n', playgroundCode.selection.start)
                        if (nextNewline >= 0 && nextNewline + 1 < currentText.length) {
                            playgroundCode = playgroundCode.copy(selection = TextRange(nextNewline + 1))
                        }
                    }
                    CursorDirection.PAGE_UP -> playgroundCode = playgroundCode.copy(selection = TextRange(0))
                    CursorDirection.PAGE_DOWN -> playgroundCode = playgroundCode.copy(selection = TextRange(currentText.length))
                }
            }

            is KeyAction.Clipboard -> {
                when (action.op) {
                    ClipboardOp.SELECT_ALL -> {
                        playgroundCode = playgroundCode.copy(selection = TextRange(0, currentText.length))
                        consoleOutput = "Selected all (${currentText.length} chars)"
                    }
                    ClipboardOp.CUT -> {
                        if (minSel != maxSel) {
                            val cutText = currentText.substring(minSel, maxSel)
                            clipboard?.setPrimaryClip(ClipData.newPlainText("code", cutText))
                            val newText = currentText.substring(0, minSel) + currentText.substring(maxSel)
                            updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel)))
                            consoleOutput = "Cut to clipboard."
                        } else {
                            val lastNewline = currentText.lastIndexOf('\n', (minSel - 1).coerceAtLeast(0))
                            val nextNewline = currentText.indexOf('\n', minSel)
                            val start = if (lastNewline >= 0) lastNewline + 1 else 0
                            val end = if (nextNewline >= 0) nextNewline + 1 else currentText.length
                            val cutText = currentText.substring(start, end)
                            clipboard?.setPrimaryClip(ClipData.newPlainText("code", cutText))
                            val newText = currentText.substring(0, start) + currentText.substring(end)
                            updatePlaygroundCode(TextFieldValue(newText, TextRange(start.coerceAtMost(newText.length))))
                            consoleOutput = "Cut line."
                        }
                    }
                    ClipboardOp.COPY -> {
                        if (minSel != maxSel) {
                            val copied = currentText.substring(minSel, maxSel)
                            clipboard?.setPrimaryClip(ClipData.newPlainText("code", copied))
                            consoleOutput = "Copied selection to clipboard."
                        } else {
                            val lastNewline = currentText.lastIndexOf('\n', (minSel - 1).coerceAtLeast(0))
                            val nextNewline = currentText.indexOf('\n', minSel)
                            val start = if (lastNewline >= 0) lastNewline + 1 else 0
                            val end = if (nextNewline >= 0) nextNewline else currentText.length
                            val line = currentText.substring(start, end)
                            if (line.isNotEmpty()) {
                                clipboard?.setPrimaryClip(ClipData.newPlainText("code", line))
                                consoleOutput = "Copied line to clipboard."
                            }
                        }
                    }
                    ClipboardOp.PASTE -> {
                        val clip = clipboard?.primaryClip
                        if (clip != null && clip.itemCount > 0) {
                            val pasteText = clip.getItemAt(0).coerceToText(context).toString()
                            if (pasteText.isNotEmpty()) {
                                val newText = currentText.substring(0, minSel) + pasteText + currentText.substring(maxSel)
                                updatePlaygroundCode(TextFieldValue(newText, TextRange(minSel + pasteText.length)))
                                consoleOutput = "Pasted (${pasteText.length} chars)"
                            }
                        }
                    }
                    ClipboardOp.UNDO -> {
                        if (undoStack.isNotEmpty()) {
                            val prev = undoStack.removeAt(undoStack.lastIndex)
                            redoStack.add(playgroundCode)
                            playgroundCode = prev
                            consoleOutput = "Undo action"
                        }
                    }
                    ClipboardOp.REDO -> {
                        if (redoStack.isNotEmpty()) {
                            val next = redoStack.removeAt(redoStack.lastIndex)
                            undoStack.add(playgroundCode)
                            playgroundCode = next
                            consoleOutput = "Redo action"
                        }
                    }
                    ClipboardOp.DELETE_LINE -> {
                        val lastNewline = currentText.lastIndexOf('\n', (minSel - 1).coerceAtLeast(0))
                        val nextNewline = currentText.indexOf('\n', minSel)
                        val start = if (lastNewline >= 0) lastNewline else 0
                        val end = if (nextNewline >= 0) nextNewline + 1 else currentText.length
                        val newText = currentText.substring(0, start) + currentText.substring(end)
                        updatePlaygroundCode(TextFieldValue(newText, TextRange(start.coerceAtMost(newText.length))))
                        consoleOutput = "Deleted line."
                    }
                    ClipboardOp.DUPLICATE_LINE -> {
                        val lastNewline = currentText.lastIndexOf('\n', (minSel - 1).coerceAtLeast(0))
                        val nextNewline = currentText.indexOf('\n', minSel)
                        val start = if (lastNewline >= 0) lastNewline + 1 else 0
                        val end = if (nextNewline >= 0) nextNewline else currentText.length
                        val currentLine = currentText.substring(start, end)
                        val newText = currentText.substring(0, end) + "\n" + currentLine + currentText.substring(end)
                        updatePlaygroundCode(TextFieldValue(newText, TextRange(end + 1 + currentLine.length)))
                        consoleOutput = "Duplicated line."
                    }
                }
            }
            else -> {}
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = theme.keyboardBackground
    ) {
        Scaffold(
            containerColor = theme.keyboardBackground,
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(theme.coderBarBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Compact Title & Switcher
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0B0C10)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.dreamboard_icon),
                                contentDescription = "DreamBoard Logo",
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "DreamBoard",
                            color = theme.primaryText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Inline compact tabs
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(theme.keyBackground)
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedTab == 0) theme.tabActiveIndicator else Color.Transparent)
                                    .clickable { selectedTab = 0 }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Device Setup",
                                    color = if (selectedTab == 0) Color(0xFF18181B) else theme.secondaryText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (selectedTab == 1) theme.tabActiveIndicator else Color.Transparent)
                                    .clickable { selectedTab = 1 }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Playground",
                                    color = if (selectedTab == 1) Color(0xFF18181B) else theme.secondaryText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { showThemeSheet = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_theme_picker")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Themes",
                                tint = theme.secondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        IconButton(
                            onClick = { showSettingsSheet = true },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("btn_app_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = theme.secondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            if (selectedTab == 0) {
                // TAB 0: Device Keyboard Activation & OS System-Wide Testing
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Platform Brand Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF07080D)),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E2235))
                    ) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.dreamboard_platform_logo),
                            contentDescription = "DreamBoard - Code Freely. Type Intelligently.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Fit
                        )
                    }

                    // System Activation Status Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isImeDefault) Color(0xFF14241B) else theme.keyBackground
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isImeDefault) Color(0xFF22C55E) else theme.keyBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(if (isImeDefault) Color(0xFF22C55E) else Color(0xFFF59E0B))
                                    )
                                    Text(
                                        text = if (isImeDefault) "DreamBoard is Your Device Keyboard" else "Device Keyboard Activation",
                                        color = theme.primaryText,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = { refreshImeStatus() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh Status",
                                        tint = theme.secondaryText,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isImeDefault) {
                                    "DreamBoard is active across Android OS. You can now type in Termux, Chrome, WhatsApp, VS Code, Notes, and any app on this device."
                                } else {
                                    "Complete the 2 simple steps below to enable DreamBoard as your device-wide keyboard."
                                },
                                color = theme.secondaryText,
                                fontSize = 12.5.sp,
                                lineHeight = 17.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Step 1 Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isImeEnabled) Color(0xFF1B3826) else theme.modifierKeyBg)
                                    .clickable {
                                        onOpenInputSettings()
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Step 1: Enable DreamBoard in System",
                                        color = if (isImeEnabled) Color(0xFF86EFAC) else theme.primaryText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isImeEnabled) "Enabled in Android Settings" else "Tap to toggle ON in 'Manage Keyboards'",
                                        color = theme.secondaryText,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isImeEnabled) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Enabled",
                                        tint = Color(0xFF22C55E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(theme.accentKeyBg)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "ENABLE",
                                            color = theme.accentKeyText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Step 2 Button
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isImeDefault) Color(0xFF1B3826) else theme.modifierKeyBg)
                                    .clickable {
                                        onSwitchKeyboard()
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Step 2: Select DreamBoard as Active Input",
                                        color = if (isImeDefault) Color(0xFF86EFAC) else theme.primaryText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isImeDefault) "DreamBoard is the active default keyboard" else "Tap to select 'DreamBoard' in the keyboard picker",
                                        color = theme.secondaryText,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isImeDefault) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = Color(0xFF22C55E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(theme.glowColor)
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "SWITCH",
                                            color = Color(0xFF18181B),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // System Device Keyboard Live Test Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = theme.keyBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, theme.keyBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = theme.glowColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "System Keyboard Live Test",
                                    color = theme.primaryText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the field below. Your device will pop up the system keyboard (DreamBoard) from the bottom of your screen just like in any other app.",
                                color = theme.secondaryText,
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = systemTestText,
                                onValueChange = { systemTestText = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .testTag("device_test_input_field"),
                                textStyle = TextStyle(
                                    color = theme.primaryText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = theme.glowColor,
                                    unfocusedBorderColor = theme.keyBorder,
                                    focusedContainerColor = theme.keyboardBackground,
                                    unfocusedContainerColor = theme.keyboardBackground
                                ),
                                placeholder = {
                                    Text("Tap here to open device keyboard...", color = theme.secondaryText, fontSize = 12.sp)
                                }
                            )
                        }
                    }

                    // Feature Guide Cards
                    Text(
                        text = "Device Features & Shortcuts",
                        color = theme.primaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FeatureMiniCard(
                            title = "2-Page Layout",
                            desc = "Page 1 has QWERTY + Numbers/Operators. Page 2 has DPAD arrows, Brackets, Regex, and code snippets.",
                            theme = theme,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureMiniCard(
                            title = "IDE Shortcuts",
                            desc = "Ctrl+V (Paste), Ctrl+C (Copy), Ctrl+A (All), Ctrl+Z (Undo), Ctrl+D (Duplicate Line).",
                            theme = theme,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FeatureMiniCard(
                            title = "Switch Anywhere",
                            desc = "Tap the 🌐 Globe key on the keyboard to switch back to your normal keyboard anytime.",
                            theme = theme,
                            modifier = Modifier.weight(1f)
                        )
                        FeatureMiniCard(
                            title = "Coding Friendly",
                            desc = "Auto-closing {}, (), [], quotes, TAB key, ESC, and arrow navigation across all Android apps.",
                            theme = theme,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // TAB 1: In-App Code Editor Playground
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(8.dp)
                    ) {
                        // Language picker & Actions bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("JavaScript", "Python", "Kotlin", "Rust", "HTML", "Shell").forEach { lang ->
                                    val isSelected = selectedLanguage == lang
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (isSelected) theme.tabActiveIndicator else theme.keyBackground)
                                            .clickable {
                                                selectedLanguage = lang
                                                playgroundCode = TextFieldValue(getSampleCodeFor(lang), TextRange(0))
                                                consoleOutput = "Loaded $lang snippet."
                                            }
                                            .padding(horizontal = 8.dp, vertical = 3.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = lang,
                                            color = if (isSelected) Color(0xFF121212) else theme.primaryText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(theme.enterKeyBg)
                                    .clickable {
                                        consoleOutput = "Executed (${playgroundCode.text.length} chars)."
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("btn_run_code"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "RUN ▶",
                                        color = Color(0xFF18181B),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(theme.keyBackground)
                                    .clickable {
                                        playgroundCode = TextFieldValue("", TextRange(0))
                                        consoleOutput = "Cleared buffer."
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                    .testTag("btn_clear_code"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "CLEAR",
                                        color = theme.secondaryText,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Multi-line code editor container
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .border(1.dp, theme.keyBorder, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = theme.keyboardBackground),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            BasicTextField(
                                value = playgroundCode,
                                onValueChange = { playgroundCode = it },
                                textStyle = TextStyle(
                                    color = theme.primaryText,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                ),
                                cursorBrush = SolidColor(theme.glowColor),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .testTag("code_editor_field")
                            )
                        }
                    }

                    // Playground interactive preview keyboard
                    CodeKeyboardView(
                        theme = theme,
                        settings = settings,
                        onAction = { handlePlaygroundAction(it) },
                        onOpenSettings = { showSettingsSheet = true }
                    )
                }
            }
        }
    }

    if (showThemeSheet) {
        ThemeSelectorSheet(
            currentThemeId = settings.themeId,
            sheetState = themeSheetState,
            onSelectTheme = { preferences.updateTheme(it) },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showSettingsSheet) {
        KeyboardSettingsSheet(
            settings = settings,
            preferences = preferences,
            sheetState = settingsSheetState,
            onDismiss = { showSettingsSheet = false }
        )
    }
}

@Composable
private fun FeatureMiniCard(
    title: String,
    desc: String,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = theme.keyBackground),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, theme.keyBorder),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = title,
                color = theme.glowColor,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = desc,
                color = theme.secondaryText,
                fontSize = 10.5.sp,
                lineHeight = 14.5.sp
            )
        }
    }
}

private fun getSampleCodeFor(lang: String): String {
    return when (lang) {
        "JavaScript" -> """// JavaScript / TypeScript
function debounce(fn, delay = 300) {
    let timer;
    return (...args) => {
        clearTimeout(timer);
        timer = setTimeout(() => fn(...args), delay);
    };
}
"""
        "Python" -> """# Python 3
def fetch_user_stats(user_id: int) -> dict:
    items = [x ** 2 for x in range(10) if x % 2 == 0]
    return {"user_id": user_id, "scores": items}

if __name__ == "__main__":
    print(fetch_user_stats(42))
"""
        "Kotlin" -> """// Kotlin Coroutines & Data Classes
data class User(val id: String, val name: String, val active: Boolean)

fun processUsers(users: List<User>): Map<Boolean, Int> {
    return users.groupBy { it.active }.mapValues { it.value.size }
}
"""
        "Rust" -> """// Rust Memory-Safe Function
fn main() {
    let numbers: Vec<i32> = (1..=10).filter(|x| x % 2 == 0).collect();
    println!("Filtered evens: {:?}", numbers);
}
"""
        "HTML" -> """<!-- Modern Component Layout -->
<div class="code-container dark-mode">
    <header class="syntax-bar">
        <h1>DreamBoard Studio</h1>
    </header>
</div>
"""
        "Shell" -> """#!/bin/bash
# DreamBoard Build & Deploy Script
echo "Compiling binary..."
gradle assembleDebug
echo "Deployment successful!"
"""
        else -> "// Start coding..."
    }
}
