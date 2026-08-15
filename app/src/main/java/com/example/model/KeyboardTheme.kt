package com.example.model

import androidx.compose.ui.graphics.Color

data class KeyboardTheme(
    val id: String,
    val name: String,
    val tagLine: String,
    val isDark: Boolean = true,
    val keyboardBackground: Color,
    val keyBackground: Color,
    val keyBorder: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accentKeyBg: Color,
    val accentKeyText: Color,
    val modifierKeyBg: Color,
    val modifierActiveBg: Color,
    val modifierActiveText: Color,
    val coderBarBg: Color,
    val coderBarKeyBg: Color,
    val coderBarText: Color,
    val tabActiveIndicator: Color,
    val enterKeyBg: Color,
    val glowColor: Color
) {
    companion object {
        val MonokaiPro = KeyboardTheme(
            id = "monokai_pro",
            name = "Monokai Pro",
            tagLine = "Classic pro dark editor theme with vivid syntax accents",
            isDark = true,
            keyboardBackground = Color(0xFF19181A),
            keyBackground = Color(0xFF221F22),
            keyBorder = Color(0xFF3B3539),
            primaryText = Color(0xFFFCFCFA),
            secondaryText = Color(0xFF787878),
            accentKeyBg = Color(0xFFFFD866),
            accentKeyText = Color(0xFF19181A),
            modifierKeyBg = Color(0xFF2D2A2E),
            modifierActiveBg = Color(0xFF38BDF8),
            modifierActiveText = Color(0xFF0F172A),
            coderBarBg = Color(0xFF141314),
            coderBarKeyBg = Color(0xFF282529),
            coderBarText = Color(0xFFA9DC76),
            tabActiveIndicator = Color(0xFF38BDF8),
            enterKeyBg = Color(0xFF38BDF8),
            glowColor = Color(0xFF38BDF8)
        )

        val Dracula = KeyboardTheme(
            id = "dracula",
            name = "Dracula Dark",
            tagLine = "Late-night purple canvas with frosty cyan highlights",
            isDark = true,
            keyboardBackground = Color(0xFF21222C),
            keyBackground = Color(0xFF282A36),
            keyBorder = Color(0xFF44475A),
            primaryText = Color(0xFFF8F8F2),
            secondaryText = Color(0xFF6272A4),
            accentKeyBg = Color(0xFFBD93F9),
            accentKeyText = Color(0xFF282A36),
            modifierKeyBg = Color(0xFF343746),
            modifierActiveBg = Color(0xFF8BE9FD),
            modifierActiveText = Color(0xFF282A36),
            coderBarBg = Color(0xFF191A21),
            coderBarKeyBg = Color(0xFF303242),
            coderBarText = Color(0xFF50FA7B),
            tabActiveIndicator = Color(0xFF8BE9FD),
            enterKeyBg = Color(0xFF50FA7B),
            glowColor = Color(0xFF8BE9FD)
        )

        val OneDarkPro = KeyboardTheme(
            id = "one_dark_pro",
            name = "One Dark / VS Code",
            tagLine = "Iconic developer workstation dark theme",
            isDark = true,
            keyboardBackground = Color(0xFF1E1E24),
            keyBackground = Color(0xFF282C34),
            keyBorder = Color(0xFF3E4451),
            primaryText = Color(0xFFABB2BF),
            secondaryText = Color(0xFF5C6370),
            accentKeyBg = Color(0xFF61AFEF),
            accentKeyText = Color(0xFF1E1E24),
            modifierKeyBg = Color(0xFF21252B),
            modifierActiveBg = Color(0xFF61AFEF),
            modifierActiveText = Color(0xFF1E1E24),
            coderBarBg = Color(0xFF181A1F),
            coderBarKeyBg = Color(0xFF2C313A),
            coderBarText = Color(0xFF98C379),
            tabActiveIndicator = Color(0xFF61AFEF),
            enterKeyBg = Color(0xFF61AFEF),
            glowColor = Color(0xFF61AFEF)
        )

        val NordNight = KeyboardTheme(
            id = "nord_night",
            name = "Nord Arctic",
            tagLine = "Arctic cold slate with frosty cyan contrast",
            isDark = true,
            keyboardBackground = Color(0xFF242933),
            keyBackground = Color(0xFF2E3440),
            keyBorder = Color(0xFF434C5E),
            primaryText = Color(0xFFECEFF4),
            secondaryText = Color(0xFFD8DEE9),
            accentKeyBg = Color(0xFF88C0D0),
            accentKeyText = Color(0xFF2E3440),
            modifierKeyBg = Color(0xFF3B4252),
            modifierActiveBg = Color(0xFF81A1C1),
            modifierActiveText = Color(0xFF2E3440),
            coderBarBg = Color(0xFF1E222A),
            coderBarKeyBg = Color(0xFF353C4A),
            coderBarText = Color(0xFF8FBCBB),
            tabActiveIndicator = Color(0xFF88C0D0),
            enterKeyBg = Color(0xFFA3BE8C),
            glowColor = Color(0xFF88C0D0)
        )

        val CyberpunkMatrix = KeyboardTheme(
            id = "cyberpunk_matrix",
            name = "Cyberpunk Matrix",
            tagLine = "OLED pitch black with radioactive neon green terminal glow",
            isDark = true,
            keyboardBackground = Color(0xFF000000),
            keyBackground = Color(0xFF0D140D),
            keyBorder = Color(0xFF1B381B),
            primaryText = Color(0xFF00FF66),
            secondaryText = Color(0xFF008F39),
            accentKeyBg = Color(0xFF00FF66),
            accentKeyText = Color(0xFF000000),
            modifierKeyBg = Color(0xFF091F09),
            modifierActiveBg = Color(0xFF39FF14),
            modifierActiveText = Color(0xFF000000),
            coderBarBg = Color(0xFF000000),
            coderBarKeyBg = Color(0xFF0A180A),
            coderBarText = Color(0xFF39FF14),
            tabActiveIndicator = Color(0xFF00FF66),
            enterKeyBg = Color(0xFF00FF66),
            glowColor = Color(0xFF00FF66)
        )

        val SolarizedDark = KeyboardTheme(
            id = "solarized_dark",
            name = "Solarized Dark",
            tagLine = "Deep blue-green hue crafted for eye fatigue reduction",
            isDark = true,
            keyboardBackground = Color(0xFF001E26),
            keyBackground = Color(0xFF002B36),
            keyBorder = Color(0xFF073642),
            primaryText = Color(0xFF93A1A1),
            secondaryText = Color(0xFF586E75),
            accentKeyBg = Color(0xFF268BD2),
            accentKeyText = Color(0xFFFDF6E3),
            modifierKeyBg = Color(0xFF073642),
            modifierActiveBg = Color(0xFFB58900),
            modifierActiveText = Color(0xFF002B36),
            coderBarBg = Color(0xFF001920),
            coderBarKeyBg = Color(0xFF073642),
            coderBarText = Color(0xFF2AA198),
            tabActiveIndicator = Color(0xFF2AA198),
            enterKeyBg = Color(0xFF859900),
            glowColor = Color(0xFF2AA198)
        )

        val CatppuccinMocha = KeyboardTheme(
            id = "catppuccin_mocha",
            name = "Catppuccin Mocha",
            tagLine = "Soothing pastel dark aesthetic with sky and lavender accents",
            isDark = true,
            keyboardBackground = Color(0xFF181825),
            keyBackground = Color(0xFF1E1E2E),
            keyBorder = Color(0xFF313244),
            primaryText = Color(0xFFCDD6F4),
            secondaryText = Color(0xFF6C7086),
            accentKeyBg = Color(0xFFCBA6F7),
            accentKeyText = Color(0xFF11111B),
            modifierKeyBg = Color(0xFF313244),
            modifierActiveBg = Color(0xFF89DCEB),
            modifierActiveText = Color(0xFF11111B),
            coderBarBg = Color(0xFF11111B),
            coderBarKeyBg = Color(0xFF252538),
            coderBarText = Color(0xFFA6E3A1),
            tabActiveIndicator = Color(0xFF89DCEB),
            enterKeyBg = Color(0xFF89B4FA),
            glowColor = Color(0xFF89DCEB)
        )

        val GitHubLight = KeyboardTheme(
            id = "github_light",
            name = "GitHub Light",
            tagLine = "Clean high-contrast daytime workspace",
            isDark = false,
            keyboardBackground = Color(0xFFF6F8FA),
            keyBackground = Color(0xFFFFFFFF),
            keyBorder = Color(0xFFD0D7DE),
            primaryText = Color(0xFF1F2328),
            secondaryText = Color(0xFF656D76),
            accentKeyBg = Color(0xFF0969DA),
            accentKeyText = Color(0xFFFFFFFF),
            modifierKeyBg = Color(0xFFEAEFF2),
            modifierActiveBg = Color(0xFF0969DA),
            modifierActiveText = Color(0xFFFFFFFF),
            coderBarBg = Color(0xFFECEFF2),
            coderBarKeyBg = Color(0xFFFFFFFF),
            coderBarText = Color(0xFF1A7F37),
            tabActiveIndicator = Color(0xFF0969DA),
            enterKeyBg = Color(0xFF1F883D),
            glowColor = Color(0xFF0969DA)
        )

        val allThemes = listOf(
            MonokaiPro,
            Dracula,
            OneDarkPro,
            NordNight,
            CyberpunkMatrix,
            SolarizedDark,
            CatppuccinMocha,
            GitHubLight
        )

        fun getThemeById(id: String): KeyboardTheme {
            return allThemes.find { it.id == id } ?: MonokaiPro
        }
    }
}
