package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.model.ClipboardOp
import com.example.model.CursorDirection
import com.example.model.KeyAction
import com.example.model.KeyModel
import com.example.model.KeyboardLayouts
import com.example.model.KeyboardPage
import com.example.model.KeyboardSettings
import com.example.model.KeyboardTheme
import com.example.model.ModifierKey

@Composable
fun CodeKeyboardView(
    theme: KeyboardTheme,
    settings: KeyboardSettings,
    onAction: (KeyAction) -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    // Strictly bound keyboard height so it does not dominate the screen (38% to 44%)
    val calculatedHeight = (screenHeightDp * settings.heightRatio.coerceIn(0.36f, 0.44f))

    var currentPage by remember { mutableStateOf(KeyboardPage.PAGE_1) }
    var activeModifiers by remember { mutableStateOf(setOf<ModifierKey>()) }

    // Live Keystroke Visual Feedback State
    var activeKeystroke by remember { mutableStateOf<String?>(null) }
    val keystrokeAuditTrail = remember { mutableStateListOf<String>() }

    LaunchedEffect(activeKeystroke) {
        if (activeKeystroke != null) {
            delay(350)
            activeKeystroke = null
        }
    }

    fun onKeyPressed(label: String) {
        if (settings.showKeystrokePopup) {
            activeKeystroke = label
        }
        if (settings.enableKeystrokeAudit) {
            keystrokeAuditTrail.add(label)
            if (keystrokeAuditTrail.size > 20) {
                keystrokeAuditTrail.removeAt(0)
            }
        }
    }

    val isShiftActive = activeModifiers.contains(ModifierKey.SHIFT) || activeModifiers.contains(ModifierKey.CAPS_LOCK)

    fun handleAction(action: KeyAction) {
        when (action) {
            is KeyAction.ToggleModifier -> {
                activeModifiers = if (activeModifiers.contains(action.modifier)) {
                    activeModifiers - action.modifier
                } else {
                    activeModifiers + action.modifier
                }
            }
            is KeyAction.SwitchPage -> {
                currentPage = action.page
            }
            else -> {
                val isCtrl = activeModifiers.contains(ModifierKey.CTRL)
                val isShift = activeModifiers.contains(ModifierKey.SHIFT) || activeModifiers.contains(ModifierKey.CAPS_LOCK)

                val resolvedAction: KeyAction = when {
                    isCtrl && action is KeyAction.InsertText -> {
                        val text = action.text.lowercase()
                        if (text.length == 1) {
                            when (text[0]) {
                                'v' -> KeyAction.Clipboard(ClipboardOp.PASTE)
                                'c' -> KeyAction.Clipboard(ClipboardOp.COPY)
                                'x' -> KeyAction.Clipboard(ClipboardOp.CUT)
                                'a' -> KeyAction.Clipboard(ClipboardOp.SELECT_ALL)
                                'z' -> if (isShift) KeyAction.Clipboard(ClipboardOp.REDO) else KeyAction.Clipboard(ClipboardOp.UNDO)
                                'y' -> KeyAction.Clipboard(ClipboardOp.REDO)
                                'd' -> KeyAction.Clipboard(ClipboardOp.DUPLICATE_LINE)
                                'k' -> KeyAction.Clipboard(ClipboardOp.DELETE_LINE)
                                'w' -> KeyAction.Backspace
                                else -> action
                            }
                        } else {
                            action
                        }
                    }
                    isCtrl && action is KeyAction.Backspace -> {
                        KeyAction.Clipboard(ClipboardOp.DELETE_LINE)
                    }
                    isCtrl && action is KeyAction.MoveCursor -> {
                        when (action.direction) {
                            CursorDirection.LEFT -> KeyAction.MoveCursor(CursorDirection.LINE_START)
                            CursorDirection.RIGHT -> KeyAction.MoveCursor(CursorDirection.LINE_END)
                            CursorDirection.UP -> KeyAction.MoveCursor(CursorDirection.PAGE_UP)
                            CursorDirection.DOWN -> KeyAction.MoveCursor(CursorDirection.PAGE_DOWN)
                            else -> action
                        }
                    }
                    isShift && action is KeyAction.InsertText && action.text.length == 1 -> {
                        KeyAction.InsertText(action.text.uppercase(), action.moveCursorBack)
                    }
                    else -> action
                }

                onAction(resolvedAction)

                // Single-shot modifiers reset
                if (activeModifiers.contains(ModifierKey.SHIFT) && !activeModifiers.contains(ModifierKey.CAPS_LOCK)) {
                    activeModifiers = activeModifiers - ModifierKey.SHIFT
                }
                if (activeModifiers.contains(ModifierKey.CTRL)) {
                    activeModifiers = activeModifiers - ModifierKey.CTRL
                }
                if (activeModifiers.contains(ModifierKey.ALT)) {
                    activeModifiers = activeModifiers - ModifierKey.ALT
                }
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(calculatedHeight)
            .background(theme.keyboardBackground)
            .testTag("code_keyboard_root")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 2.dp, vertical = 2.dp)
        ) {
            // Subtle Top Page Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator Selector (1 / 2) or live Keystroke Stream if audit enabled
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KeyboardPage.values().forEach { page ->
                        val isSelected = currentPage == page
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) theme.tabActiveIndicator else theme.keyBackground)
                                .clickable { currentPage = page }
                                .padding(horizontal = 10.dp, vertical = 2.dp)
                                .testTag("page_tab_${page.name}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Page ${page.title}: ${page.badge}",
                                color = if (isSelected) Color(0xFF101010) else theme.secondaryText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                    }

                    if (settings.enableKeystrokeAudit && keystrokeAuditTrail.isNotEmpty()) {
                        Text(
                            text = "⌨ " + keystrokeAuditTrail.takeLast(6).joinToString(" "),
                            color = theme.glowColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1
                        )
                    }
                }

                // Active Modifiers & Settings Icon
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (activeModifiers.contains(ModifierKey.CTRL)) {
                        ModifierIndicator("CTRL ACTIVE", theme.glowColor, Color(0xFF18181B))
                    }
                    if (activeModifiers.contains(ModifierKey.SHIFT)) {
                        ModifierIndicator("SHIFT", theme.modifierActiveBg, theme.modifierActiveText)
                    }
                    if (activeModifiers.contains(ModifierKey.ALT)) {
                        ModifierIndicator("ALT", theme.modifierActiveBg, theme.modifierActiveText)
                    }

                    if (onOpenSettings != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(3.dp))
                                .background(theme.keyBackground)
                                .clickable { onOpenSettings() }
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                .testTag("btn_keyboard_settings"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = theme.secondaryText,
                                modifier = Modifier.height(12.dp)
                            )
                        }
                    }
                }
            }

            // Keyboard Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                when (currentPage) {
                    KeyboardPage.PAGE_1 -> {
                        if (settings.showDedicatedNumberRow) {
                            // 5-Row Main Layout with top numbers row
                            KeyboardRow(KeyboardLayouts.page1Row1, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row2, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row3, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row4, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row5, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        } else {
                            // 4-Row Compact Layout
                            KeyboardRow(KeyboardLayouts.page1Row2, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row3, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row4, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                            KeyboardRow(KeyboardLayouts.page1Row5Compact, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        }
                    }
                    KeyboardPage.PAGE_2 -> {
                        // Moved symbols from Page 1 + Essential navigation and coding functions
                        KeyboardRow(KeyboardLayouts.page2Row1, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        KeyboardRow(KeyboardLayouts.page2Row2, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        KeyboardRow(KeyboardLayouts.page2Row3, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        KeyboardRow(KeyboardLayouts.page2Row4, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        KeyboardRow(KeyboardLayouts.page2Row5, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                        KeyboardRow(KeyboardLayouts.page2Row6, theme, settings, activeModifiers, isShiftActive, ::handleAction, ::onKeyPressed, Modifier.weight(1f))
                    }
                }
            }
        }

        // Visual Keystrokes Bubble overlay in center-top of keyboard
        if (settings.showKeystrokePopup) {
            KeystrokeBubble(
                keyLabel = activeKeystroke,
                theme = theme,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun KeyboardRow(
    keys: List<KeyModel>,
    theme: KeyboardTheme,
    settings: KeyboardSettings,
    activeModifiers: Set<ModifierKey>,
    isShifted: Boolean,
    onAction: (KeyAction) -> Unit,
    onKeyPressed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 0.5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        keys.forEach { keyModel ->
            KeyButton(
                keyModel = keyModel,
                theme = theme,
                settings = settings,
                activeModifiers = activeModifiers,
                isShifted = isShifted,
                onAction = onAction,
                onKeyPressed = onKeyPressed,
                modifier = Modifier
                    .weight(keyModel.weight)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun ModifierIndicator(text: String, bg: Color, fg: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(3.dp))
            .background(bg)
            .padding(horizontal = 5.dp, vertical = 1.dp)
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace
        )
    }
}
