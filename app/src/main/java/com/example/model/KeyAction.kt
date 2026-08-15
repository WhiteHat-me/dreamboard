package com.example.model

enum class ModifierKey {
    CTRL,
    ALT,
    SHIFT,
    FN,
    CAPS_LOCK
}

enum class CursorDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    LINE_START,
    LINE_END,
    PAGE_UP,
    PAGE_DOWN
}

enum class ClipboardOp {
    SELECT_ALL,
    CUT,
    COPY,
    PASTE,
    UNDO,
    REDO,
    DELETE_LINE,
    DUPLICATE_LINE
}

enum class KeyboardPage(val title: String, val badge: String) {
    PAGE_1("1", "Main QWERTY"),
    PAGE_2("2", "Symbols & Code")
}

sealed interface KeyAction {
    data class InsertText(val text: String, val moveCursorBack: Int = 0) : KeyAction
    data class SendKeyCode(val keyCode: Int) : KeyAction
    data class ToggleModifier(val modifier: ModifierKey) : KeyAction
    data class SwitchPage(val page: KeyboardPage) : KeyAction
    object Tab : KeyAction
    object Escape : KeyAction
    object Backspace : KeyAction
    object Enter : KeyAction
    object Space : KeyAction
    data class MoveCursor(val direction: CursorDirection) : KeyAction
    data class Clipboard(val op: ClipboardOp) : KeyAction
    object SwitchInputMethod : KeyAction
    object HideKeyboard : KeyAction
}

data class KeyModel(
    val primaryLabel: String,
    val secondaryLabel: String? = null,
    val action: KeyAction,
    val weight: Float = 1.0f,
    val isAccent: Boolean = false,
    val isModifier: Boolean = false,
    val isDanger: Boolean = false,
    val iconType: KeyIconType? = null
)

enum class KeyIconType {
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    GLOBE,
    ARROW_UP_DOWN,
    ARROW_LEFT,
    ARROW_RIGHT,
    ARROW_UP,
    ARROW_DOWN,
    TAB,
    KEYBOARD_HIDE
}
