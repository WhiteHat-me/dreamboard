package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.model.KeyboardPreferences
import com.example.model.KeyboardSettings
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeyboardSettingsSheet(
    settings: KeyboardSettings,
    preferences: KeyboardPreferences,
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF18181B)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Keyboard Settings & Ergonomics",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Practical, functional controls for code editing, delimiters, and mechanical feel",
                color = Color(0xFFA1A1AA),
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ==========================================
            // SECTION 1: CODING & SYNTAX AUTOMATION
            // ==========================================
            SettingCategoryHeader("Coding & Syntax Automation")

            // Auto-pair brackets
            SettingToggleRow(
                title = "Auto-Pair Brackets",
                subtitle = "Automatically insert matching closer for ( ), { }, [ ], < >",
                checked = settings.autoCloseBrackets,
                onCheckedChange = { preferences.updateAutoCloseBrackets(it) },
                testTag = "switch_auto_brackets",
                activeColor = Color(0xFFA9DC76)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Auto-pair quotes
            SettingToggleRow(
                title = "Auto-Pair Quotes",
                subtitle = "Automatically insert matching pair for \" \", ' ', and ` `",
                checked = settings.autoCloseQuotes,
                onCheckedChange = { preferences.updateAutoCloseQuotes(it) },
                testTag = "switch_auto_quotes",
                activeColor = Color(0xFFA9DC76)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Step-Over
            SettingToggleRow(
                title = "Smart Bracket Step-Over",
                subtitle = "Typing a closing bracket or quote skips past it instead of duplicating",
                checked = settings.smartBracketStepOver,
                onCheckedChange = { preferences.updateSmartBracketStepOver(it) },
                testTag = "switch_smart_step_over",
                activeColor = Color(0xFF78DCE8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Backspace Pair Deletion
            SettingToggleRow(
                title = "Smart Backspace Pair Deletion",
                subtitle = "Deleting an opening delimiter also removes its matching empty closer",
                checked = settings.smartBackspacePair,
                onCheckedChange = { preferences.updateSmartBackspacePair(it) },
                testTag = "switch_smart_backspace",
                activeColor = Color(0xFFFF6188)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Auto-Indent on Enter
            SettingToggleRow(
                title = "Auto-Indent on Enter",
                subtitle = "Preserves current line indent and indents new block scope after { [ ( :",
                checked = settings.autoIndentOnEnter,
                onCheckedChange = { preferences.updateAutoIndentOnEnter(it) },
                testTag = "switch_auto_indent",
                activeColor = Color(0xFFFFD866)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Smart Auto-Capitalization for Sentences & Comments
            SettingToggleRow(
                title = "Smart Sentence & Comment Capitalization",
                subtitle = "Capitalize after sentence endings (. ? !), comments (// #), and lists while keeping code lowercase",
                checked = settings.autoCapitalize,
                onCheckedChange = { preferences.updateAutoCapitalize(it) },
                testTag = "switch_auto_capitalize",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Indentation Behavior
            SettingSubHeader("Tab Key Indentation")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(2, "2 Spaces"),
                    Pair(4, "4 Spaces"),
                    Pair(0, "Raw \\t")
                ).forEach { (spaces, label) ->
                    val isSelected = settings.tabSpaces == spaces
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF78DCE8) else Color(0xFF27272A))
                            .clickable { preferences.updateTabSpaces(spaces) }
                            .padding(vertical = 10.dp)
                            .testTag("tab_size_${spaces}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF18181B) else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTION 2: KEYBOARD LAYOUT & GESTURES
            // ==========================================
            SettingCategoryHeader("Layout, Sizing & Gestures")

            // Dedicated Number Row Toggle
            SettingToggleRow(
                title = "Dedicated Top Number Row (1-0)",
                subtitle = "Show permanent 0-9 number row on Page 1 or switch to 4-row compact mode",
                checked = settings.showDedicatedNumberRow,
                onCheckedChange = { preferences.updateShowDedicatedNumberRow(it) },
                testTag = "switch_dedicated_numbers",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Spacebar Swipe Cursor Navigation
            SettingToggleRow(
                title = "Spacebar Cursor Glide",
                subtitle = "Swipe your finger across the spacebar to smoothly scrub the cursor left and right",
                checked = settings.spacebarSwipeCursor,
                onCheckedChange = { preferences.updateSpacebarSwipeCursor(it) },
                testTag = "switch_spacebar_swipe",
                activeColor = Color(0xFFA9DC76)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sticky Coder Bar
            SettingToggleRow(
                title = "Sticky Coder Symbol Bar",
                subtitle = "Always pin top bracket & syntax shortcut strip above the keyboard",
                checked = settings.persistentCoderBar,
                onCheckedChange = { preferences.updatePersistentCoderBar(it) },
                testTag = "switch_coder_bar",
                activeColor = Color(0xFF78DCE8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Long-Press Labels
            SettingToggleRow(
                title = "Secondary Long-Press Labels",
                subtitle = "Show small symbol hints on corner of keys for long-press shortcuts",
                checked = settings.showSecondaryLabels,
                onCheckedChange = { preferences.updateShowSecondary(it) },
                testTag = "switch_secondary_labels",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Height Ratio Slider
            SettingSubHeader("Keyboard Height Ratio: ${(settings.heightRatio * 100).roundToInt()}% of screen")
            Slider(
                value = settings.heightRatio,
                onValueChange = { preferences.updateHeightRatio(it) },
                valueRange = 0.36f..0.46f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF78DCE8),
                    activeTrackColor = Color(0xFF78DCE8)
                ),
                modifier = Modifier.testTag("slider_height_ratio")
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTION 3: HAPTICS & SOUND FEEDBACK
            // ==========================================
            SettingCategoryHeader("Haptics & Audio Feedback")

            // Haptic Feedback Intensity
            SettingSubHeader("Haptic Vibration Level")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair(0, "Off"),
                    Pair(1, "Light"),
                    Pair(2, "Medium"),
                    Pair(3, "Strong")
                ).forEach { (level, label) ->
                    val isSelected = settings.hapticLevel == level
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFFFD866) else Color(0xFF27272A))
                            .clickable { preferences.updateHapticLevel(level) }
                            .padding(vertical = 10.dp)
                            .testTag("haptic_level_${level}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFF18181B) else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subtle Keystroke Sound
            SettingToggleRow(
                title = "Subtle Keystroke Sound",
                subtitle = "Play a crisp, subtle mechanical click sound on every key press",
                checked = settings.keySound,
                onCheckedChange = { preferences.updateKeySound(it) },
                testTag = "switch_key_sound",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // SECTION 4: VISUAL FEEDBACK & HUD
            // ==========================================
            SettingCategoryHeader("Visual Feedback & HUD")

            // Visual Keystroke Bubble
            SettingToggleRow(
                title = "Visual Keystroke Callout Bubble",
                subtitle = "Show responsive keystroke popup callout on every key press",
                checked = settings.showKeystrokePopup,
                onCheckedChange = { preferences.updateShowKeystrokePopup(it) },
                testTag = "switch_keystroke_popup",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Keystroke Stream Audit Trail
            SettingToggleRow(
                title = "Live Keystroke Audit Trail",
                subtitle = "Display live keystroke stream in keyboard header for input verification",
                checked = settings.enableKeystrokeAudit,
                onCheckedChange = { preferences.updateEnableKeystrokeAudit(it) },
                testTag = "switch_keystroke_audit",
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SettingCategoryHeader(text: String) {
    Text(
        text = text.uppercase(),
        color = Color(0xFF78DCE8),
        fontSize = 12.sp,
        fontWeight = FontWeight.Black,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(top = 8.dp, bottom = 10.dp)
    )
}

@Composable
private fun SettingSubHeader(text: String) {
    Text(
        text = text,
        color = Color(0xFFA1A1AA),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String,
    activeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = Color(0xFFA1A1AA),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF18181B),
                checkedTrackColor = activeColor
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}
