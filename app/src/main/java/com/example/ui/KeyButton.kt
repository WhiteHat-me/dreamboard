package com.example.ui

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.KeyboardReturn
import androidx.compose.material.icons.automirrored.filled.KeyboardTab
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardCapslock
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import com.example.model.CursorDirection
import com.example.model.KeyAction
import com.example.model.KeyIconType
import com.example.model.KeyModel
import com.example.model.KeyboardSettings
import com.example.model.KeyboardTheme
import com.example.model.ModifierKey

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    keyModel: KeyModel,
    theme: KeyboardTheme,
    settings: KeyboardSettings,
    activeModifiers: Set<ModifierKey>,
    isShifted: Boolean,
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier,
    onKeyPressed: ((String) -> Unit)? = null
) {
    val view = LocalView.current

    val isModifierActive = when (val action = keyModel.action) {
        is KeyAction.ToggleModifier -> activeModifiers.contains(action.modifier)
        else -> false
    }

    val isCtrlActive = activeModifiers.contains(ModifierKey.CTRL)

    val ctrlHint = if (isCtrlActive && keyModel.primaryLabel.length == 1) {
        when (keyModel.primaryLabel.lowercase()) {
            "v" -> "PASTE"
            "c" -> "COPY"
            "x" -> "CUT"
            "a" -> "ALL"
            "z" -> "UNDO"
            "y" -> "REDO"
            "d" -> "DUP"
            "k" -> "DEL"
            else -> null
        }
    } else null

    val isKeyHighlighted = isModifierActive || (isCtrlActive && ctrlHint != null)

    val backgroundColor = when {
        isModifierActive -> theme.modifierActiveBg
        isKeyHighlighted -> Color(0xFF3B2F18)
        keyModel.isAccent -> theme.enterKeyBg
        keyModel.isDanger -> Color(0xFF2C2224)
        keyModel.isModifier -> theme.modifierKeyBg
        else -> theme.keyBackground
    }

    val textColor = when {
        isModifierActive -> theme.modifierActiveText
        isKeyHighlighted -> theme.glowColor
        keyModel.isAccent -> theme.accentKeyText
        keyModel.isDanger -> theme.primaryText
        else -> theme.primaryText
    }

    val borderStroke = if (isModifierActive || isKeyHighlighted) {
        BorderStroke(1.dp, theme.glowColor)
    } else {
        BorderStroke(0.5.dp, theme.keyBorder)
    }

    // Determine displayed label
    val displayLabel = if (isShifted && keyModel.primaryLabel.length == 1 && keyModel.primaryLabel[0].isLetter()) {
        keyModel.primaryLabel.uppercase()
    } else {
        keyModel.primaryLabel
    }

    val shape = RoundedCornerShape(settings.keyRoundnessDp.dp)

    fun performHaptics() {
        if (settings.hapticLevel > 0) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    fun performSoundFeedback() {
        if (settings.keySound) {
            try {
                val audioManager = view.context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
                if (audioManager != null) {
                    audioManager.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 0.4f)
                } else {
                    view.playSoundEffect(SoundEffectConstants.CLICK)
                }
            } catch (e: Exception) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }
        }
    }

    val isSpace = keyModel.action is KeyAction.Space
    var accumulatedDrag by remember { mutableFloatStateOf(0f) }
    val dragStepPx = with(LocalDensity.current) { 18.dp.toPx() }

    val clickModifier = if (isSpace && settings.spacebarSwipeCursor) {
        Modifier
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { accumulatedDrag = 0f },
                    onDragEnd = { accumulatedDrag = 0f },
                    onDragCancel = { accumulatedDrag = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        accumulatedDrag += dragAmount
                        if (accumulatedDrag > dragStepPx) {
                            performHaptics()
                            onAction(KeyAction.MoveCursor(CursorDirection.RIGHT))
                            accumulatedDrag = 0f
                        } else if (accumulatedDrag < -dragStepPx) {
                            performHaptics()
                            onAction(KeyAction.MoveCursor(CursorDirection.LEFT))
                            accumulatedDrag = 0f
                        }
                    }
                )
            }
            .combinedClickable(
                onClick = {
                    performHaptics()
                    performSoundFeedback()
                    onKeyPressed?.invoke(displayLabel)
                    onAction(keyModel.action)
                },
                onLongClick = {
                    if (keyModel.secondaryLabel != null) {
                        performHaptics()
                        performSoundFeedback()
                        onKeyPressed?.invoke(keyModel.secondaryLabel)
                        onAction(KeyAction.InsertText(keyModel.secondaryLabel))
                    }
                }
            )
    } else {
        Modifier.combinedClickable(
            onClick = {
                performHaptics()
                performSoundFeedback()
                onKeyPressed?.invoke(displayLabel)
                onAction(keyModel.action)
            },
            onLongClick = {
                if (keyModel.secondaryLabel != null) {
                    performHaptics()
                    performSoundFeedback()
                    onKeyPressed?.invoke(keyModel.secondaryLabel)
                    onAction(KeyAction.InsertText(keyModel.secondaryLabel))
                }
            }
        )
    }

    Box(
        modifier = modifier
            .padding(horizontal = 1.dp, vertical = 1.5.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(borderStroke, shape)
            .then(clickModifier)
            .testTag("key_${keyModel.primaryLabel}"),
        contentAlignment = Alignment.Center
    ) {
        // Render Shortcut / Secondary Hint
        if (ctrlHint != null) {
            Text(
                text = ctrlHint,
                color = theme.glowColor,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 1.dp)
            )
        } else if (keyModel.secondaryLabel != null && !isShifted) {
            Text(
                text = keyModel.secondaryLabel,
                color = theme.secondaryText.copy(alpha = 0.55f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 1.dp, end = 2.5.dp)
            )
        }

        // Handle vector icon rendering if key has iconType
        if (keyModel.iconType != null) {
            RenderKeyIcon(
                iconType = keyModel.iconType,
                tint = textColor,
                isShiftActive = isShifted
            )
        } else {
            // Render text key
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if (ctrlHint != null || keyModel.secondaryLabel != null) 3.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = displayLabel,
                    color = textColor,
                    fontSize = when {
                        displayLabel.length > 5 -> 10.sp
                        displayLabel.length > 2 -> 11.5.sp
                        displayLabel.length == 2 -> 13.sp
                        else -> 14.5.sp
                    },
                    fontWeight = if (keyModel.isModifier || keyModel.isAccent || isKeyHighlighted) FontWeight.SemiBold else FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RenderKeyIcon(
    iconType: KeyIconType,
    tint: Color,
    isShiftActive: Boolean
) {
    val iconVector = when (iconType) {
        KeyIconType.SHIFT -> Icons.Default.KeyboardCapslock
        KeyIconType.BACKSPACE -> Icons.AutoMirrored.Filled.Backspace
        KeyIconType.ENTER -> Icons.AutoMirrored.Filled.KeyboardReturn
        KeyIconType.SPACE -> Icons.Default.SpaceBar
        KeyIconType.GLOBE -> Icons.Default.Language
        KeyIconType.ARROW_UP_DOWN -> Icons.Default.UnfoldMore
        KeyIconType.ARROW_LEFT -> Icons.AutoMirrored.Filled.ArrowBack
        KeyIconType.ARROW_RIGHT -> Icons.AutoMirrored.Filled.ArrowForward
        KeyIconType.ARROW_UP -> Icons.Default.ArrowUpward
        KeyIconType.ARROW_DOWN -> Icons.Default.ArrowDownward
        KeyIconType.TAB -> Icons.AutoMirrored.Filled.KeyboardTab
        KeyIconType.KEYBOARD_HIDE -> Icons.Default.KeyboardHide
    }

    Icon(
        imageVector = iconVector,
        contentDescription = iconType.name,
        tint = tint,
        modifier = Modifier.size(16.dp)
    )
}
