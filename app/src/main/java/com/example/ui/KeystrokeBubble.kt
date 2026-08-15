package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.KeyboardTheme

@Composable
fun KeystrokeBubble(
    keyLabel: String?,
    theme: KeyboardTheme,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !keyLabel.isNullOrEmpty(),
        enter = fadeIn() + scaleIn(initialScale = 0.8f),
        exit = fadeOut() + scaleOut(targetScale = 0.8f),
        modifier = modifier
    ) {
        if (!keyLabel.isNullOrEmpty()) {
            Box(
                modifier = Modifier
                    .shadow(12.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(theme.enterKeyBg)
                    .border(1.5.dp, theme.glowColor, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .sizeIn(minWidth = 44.dp, minHeight = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = keyLabel,
                    color = Color(0xFF18181B),
                    fontSize = if (keyLabel.length > 3) 14.sp else 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
