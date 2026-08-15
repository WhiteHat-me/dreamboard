package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClipboardOp
import com.example.model.CursorDirection
import com.example.model.KeyAction
import com.example.model.KeyboardTheme

@Composable
fun NavTerminalView(
    theme: KeyboardTheme,
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(theme.keyboardBackground)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Quick clipboard & edit actions row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavButton("SEL ALL", Modifier.weight(1f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.SELECT_ALL)) }
            NavButton("CUT", Modifier.weight(1f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.CUT)) }
            NavButton("COPY", Modifier.weight(1f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.COPY)) }
            NavButton("PASTE", Modifier.weight(1.2f), theme, isAccent = true) { onAction(KeyAction.Clipboard(ClipboardOp.PASTE)) }
            NavButton("UNDO", Modifier.weight(1f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.UNDO)) }
            NavButton("REDO", Modifier.weight(1f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.REDO)) }
        }

        // Navigation Row 1: Home, Up, End, PageUp, Duplicate Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavButton("HOME", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.LINE_START)) }
            NavButton("▲ UP", Modifier.weight(1.2f), theme, isAccent = true) { onAction(KeyAction.MoveCursor(CursorDirection.UP)) }
            NavButton("END", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.LINE_END)) }
            NavButton("PG UP", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.PAGE_UP)) }
            NavButton("DUP LN", Modifier.weight(1.2f), theme) { onAction(KeyAction.Clipboard(ClipboardOp.DUPLICATE_LINE)) }
        }

        // Navigation Row 2: Left, Down, Right, PageDown, Delete Line
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavButton("◀ LEFT", Modifier.weight(1.2f), theme, isAccent = true) { onAction(KeyAction.MoveCursor(CursorDirection.LEFT)) }
            NavButton("▼ DOWN", Modifier.weight(1.2f), theme, isAccent = true) { onAction(KeyAction.MoveCursor(CursorDirection.DOWN)) }
            NavButton("RIGHT ▶", Modifier.weight(1.2f), theme, isAccent = true) { onAction(KeyAction.MoveCursor(CursorDirection.RIGHT)) }
            NavButton("PG DN", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.PAGE_DOWN)) }
            NavButton("DEL LN", Modifier.weight(1.2f), theme, isDanger = true) { onAction(KeyAction.Clipboard(ClipboardOp.DELETE_LINE)) }
        }

        // Vim Navigation Shortcuts Row (h, j, k, l, w, b, 0, $, x, u)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            NavButton("h (◀)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.LEFT)) }
            NavButton("j (▼)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.DOWN)) }
            NavButton("k (▲)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.UP)) }
            NavButton("l (▶)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.RIGHT)) }
            NavButton("0 (^)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.LINE_START)) }
            NavButton("$ ($)", Modifier.weight(1f), theme) { onAction(KeyAction.MoveCursor(CursorDirection.LINE_END)) }
            NavButton("ESC", Modifier.weight(1f), theme, isAccent = true) { onAction(KeyAction.Escape) }
        }
    }
}

@Composable
private fun NavButton(
    label: String,
    modifier: Modifier = Modifier,
    theme: KeyboardTheme,
    isAccent: Boolean = false,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        isDanger -> Color(0xFFE53935)
        isAccent -> theme.accentKeyBg
        else -> theme.keyBackground
    }
    val fg = when {
        isDanger -> Color.White
        isAccent -> theme.accentKeyText
        else -> theme.primaryText
    }

    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            maxLines = 1
        )
    }
}
