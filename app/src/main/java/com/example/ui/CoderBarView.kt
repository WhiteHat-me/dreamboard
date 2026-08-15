package com.example.ui

import android.content.Context
import android.media.AudioManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyAction
import com.example.model.KeyboardLayouts
import com.example.model.KeyboardSettings
import com.example.model.KeyboardTheme

@Composable
fun CoderBarView(
    theme: KeyboardTheme,
    settings: KeyboardSettings = KeyboardSettings(),
    onKeyPressed: ((String) -> Unit)? = null,
    onAction: (KeyAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val view = LocalView.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(34.dp)
            .background(theme.coderBarBg)
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        KeyboardLayouts.coderBarShortcuts.forEach { item ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(theme.coderBarKeyBg)
                    .clickable {
                        if (settings.hapticLevel > 0) {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        }
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
                        onKeyPressed?.invoke(item.label)
                        onAction(KeyAction.InsertText(item.insertText, item.cursorOffset))
                    }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("coder_key_${item.label}"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.label,
                    color = theme.coderBarText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
