package com.example.ime

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.model.KeyAction
import com.example.model.KeyboardPreferences
import com.example.model.KeyboardTheme
import com.example.ui.CodeKeyboardView

class CodeKeyboardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val appViewModelStore = ViewModelStore()

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore
        get() = appViewModelStore

    private lateinit var preferences: KeyboardPreferences
    private lateinit var inputHandler: InputConnectionHandler

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(Bundle())
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        preferences = KeyboardPreferences.getInstance(this)
        inputHandler = InputConnectionHandler(this) { currentInputConnection }
    }

    override fun onCreateInputView(): View {
        return ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@CodeKeyboardService))
            setViewTreeLifecycleOwner(this@CodeKeyboardService)
            setViewTreeSavedStateRegistryOwner(this@CodeKeyboardService)
            setViewTreeViewModelStoreOwner(this@CodeKeyboardService)

            setContent {
                val settings by preferences.settings.collectAsState()
                val currentTheme = KeyboardTheme.getThemeById(settings.themeId)

                CodeKeyboardView(
                    theme = currentTheme,
                    settings = settings,
                    onAction = { action ->
                        when (action) {
                            is KeyAction.SwitchInputMethod -> {
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    switchToNextInputMethod(false)
                                } else {
                                    imm?.showInputMethodPicker()
                                }
                            }
                            is KeyAction.HideKeyboard -> {
                                requestHideSelf(0)
                            }
                            else -> {
                                inputHandler.executeAction(
                                    action = action,
                                    settings = settings,
                                    activeModifiers = emptySet()
                                )
                            }
                        }
                    }
                )
            }
        }
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onFinishInputView(finishingInput)
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        appViewModelStore.clear()
        super.onDestroy()
    }
}
