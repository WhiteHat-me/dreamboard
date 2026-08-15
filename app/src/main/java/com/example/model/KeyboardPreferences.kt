package com.example.model

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KeyboardPreferences(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("codekey_keyboard_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<KeyboardSettings> = _settings.asStateFlow()

    private fun loadSettings(): KeyboardSettings {
        val themeId = prefs.getString("theme_id", KeyboardTheme.MonokaiPro.id) ?: KeyboardTheme.MonokaiPro.id
        val heightRatio = prefs.getFloat("height_ratio", 0.44f).coerceIn(0.36f, 0.48f)
        val hapticLevel = prefs.getInt("haptic_level", 2)
        val keySound = prefs.getBoolean("key_sound", true)
        val autoCloseBrackets = prefs.getBoolean("auto_close_brackets", true)
        val autoCloseQuotes = prefs.getBoolean("auto_close_quotes", true)
        val smartBracketStepOver = prefs.getBoolean("smart_bracket_step_over", true)
        val smartBackspacePair = prefs.getBoolean("smart_backspace_pair", true)
        val autoIndentOnEnter = prefs.getBoolean("auto_indent_on_enter", true)
        val showDedicatedNumberRow = prefs.getBoolean("show_dedicated_number_row", true)
        val spacebarSwipeCursor = prefs.getBoolean("spacebar_swipe_cursor", true)
        val autoCapitalize = prefs.getBoolean("auto_capitalize", true)
        val tabSpaces = prefs.getInt("tab_spaces", 4)
        val showSecondary = prefs.getBoolean("show_secondary", true)
        val showKeystrokePopup = prefs.getBoolean("show_keystroke_popup", true)
        val enableKeystrokeAudit = prefs.getBoolean("enable_keystroke_audit", false)
        val persistentCoderBar = prefs.getBoolean("persistent_coder_bar", true)

        return KeyboardSettings(
            themeId = themeId,
            heightRatio = heightRatio,
            hapticLevel = hapticLevel,
            keySound = keySound,
            autoCloseBrackets = autoCloseBrackets,
            autoCloseQuotes = autoCloseQuotes,
            smartBracketStepOver = smartBracketStepOver,
            smartBackspacePair = smartBackspacePair,
            autoIndentOnEnter = autoIndentOnEnter,
            showDedicatedNumberRow = showDedicatedNumberRow,
            spacebarSwipeCursor = spacebarSwipeCursor,
            autoCapitalize = autoCapitalize,
            tabSpaces = tabSpaces,
            showSecondaryLabels = showSecondary,
            showKeystrokePopup = showKeystrokePopup,
            enableKeystrokeAudit = enableKeystrokeAudit,
            persistentCoderBar = persistentCoderBar
        )
    }

    fun updateTheme(themeId: String) {
        prefs.edit().putString("theme_id", themeId).apply()
        _settings.value = _settings.value.copy(themeId = themeId)
    }

    fun updateHeightRatio(ratio: Float) {
        val safeRatio = ratio.coerceIn(0.36f, 0.48f)
        prefs.edit().putFloat("height_ratio", safeRatio).apply()
        _settings.value = _settings.value.copy(heightRatio = safeRatio)
    }

    fun updateHapticLevel(level: Int) {
        prefs.edit().putInt("haptic_level", level).apply()
        _settings.value = _settings.value.copy(hapticLevel = level)
    }

    fun updateKeySound(enabled: Boolean) {
        prefs.edit().putBoolean("key_sound", enabled).apply()
        _settings.value = _settings.value.copy(keySound = enabled)
    }

    fun updateAutoCloseBrackets(enabled: Boolean) {
        prefs.edit().putBoolean("auto_close_brackets", enabled).apply()
        _settings.value = _settings.value.copy(autoCloseBrackets = enabled)
    }

    fun updateAutoCloseQuotes(enabled: Boolean) {
        prefs.edit().putBoolean("auto_close_quotes", enabled).apply()
        _settings.value = _settings.value.copy(autoCloseQuotes = enabled)
    }

    fun updateSmartBracketStepOver(enabled: Boolean) {
        prefs.edit().putBoolean("smart_bracket_step_over", enabled).apply()
        _settings.value = _settings.value.copy(smartBracketStepOver = enabled)
    }

    fun updateSmartBackspacePair(enabled: Boolean) {
        prefs.edit().putBoolean("smart_backspace_pair", enabled).apply()
        _settings.value = _settings.value.copy(smartBackspacePair = enabled)
    }

    fun updateAutoIndentOnEnter(enabled: Boolean) {
        prefs.edit().putBoolean("auto_indent_on_enter", enabled).apply()
        _settings.value = _settings.value.copy(autoIndentOnEnter = enabled)
    }

    fun updateShowDedicatedNumberRow(show: Boolean) {
        prefs.edit().putBoolean("show_dedicated_number_row", show).apply()
        _settings.value = _settings.value.copy(showDedicatedNumberRow = show)
    }

    fun updateSpacebarSwipeCursor(enabled: Boolean) {
        prefs.edit().putBoolean("spacebar_swipe_cursor", enabled).apply()
        _settings.value = _settings.value.copy(spacebarSwipeCursor = enabled)
    }

    fun updateAutoCapitalize(enabled: Boolean) {
        prefs.edit().putBoolean("auto_capitalize", enabled).apply()
        _settings.value = _settings.value.copy(autoCapitalize = enabled)
    }

    fun updateTabSpaces(spaces: Int) {
        prefs.edit().putInt("tab_spaces", spaces).apply()
        _settings.value = _settings.value.copy(tabSpaces = spaces)
    }

    fun updateShowSecondary(show: Boolean) {
        prefs.edit().putBoolean("show_secondary", show).apply()
        _settings.value = _settings.value.copy(showSecondaryLabels = show)
    }

    fun updateShowKeystrokePopup(show: Boolean) {
        prefs.edit().putBoolean("show_keystroke_popup", show).apply()
        _settings.value = _settings.value.copy(showKeystrokePopup = show)
    }

    fun updateEnableKeystrokeAudit(enable: Boolean) {
        prefs.edit().putBoolean("enable_keystroke_audit", enable).apply()
        _settings.value = _settings.value.copy(enableKeystrokeAudit = enable)
    }

    fun updatePersistentCoderBar(show: Boolean) {
        prefs.edit().putBoolean("persistent_coder_bar", show).apply()
        _settings.value = _settings.value.copy(persistentCoderBar = show)
    }

    companion object {
        @Volatile
        private var INSTANCE: KeyboardPreferences? = null

        fun getInstance(context: Context): KeyboardPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KeyboardPreferences(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
