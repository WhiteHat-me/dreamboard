package com.example.model

data class CodeSnippet(
    val id: String,
    val title: String,
    val code: String,
    val language: String,
    val cursorOffset: Int = 0 // Offset from end
) {
    companion object {
        val defaultSnippets = listOf(
            CodeSnippet("fn_arrow", "Arrow Fn", "() => {\n    \n}", "JS/TS", 3),
            CodeSnippet("console_log", "console.log", "console.log();", "JS/TS", 2),
            CodeSnippet("func_def", "function", "function name() {\n    \n}", "JS/TS", 3),
            CodeSnippet("if_block", "if () {}", "if () {\n    \n}", "Universal", 8),
            CodeSnippet("for_loop", "for (i)", "for (let i = 0; i < n; i++) {\n    \n}", "JS/TS", 3),
            CodeSnippet("try_catch", "try catch", "try {\n    \n} catch (e) {\n    \n}", "Universal", 18),
            CodeSnippet("py_def", "def fn():", "def my_func():\n    pass", "Python", 4),
            CodeSnippet("py_print", "print()", "print()", "Python", 1),
            CodeSnippet("py_if", "if __main__", "if __name__ == \"__main__\":\n    ", "Python", 0),
            CodeSnippet("kt_fun", "fun main()", "fun main() {\n    \n}", "Kotlin", 3),
            CodeSnippet("kt_println", "println()", "println()", "Kotlin", 1),
            CodeSnippet("git_commit", "git commit", "git commit -m \"\"", "Git", 1),
            CodeSnippet("git_push", "git push", "git push origin main", "Git", 0),
            CodeSnippet("sh_shebang", "#!/bin/bash", "#!/usr/bin/env bash\n\n", "Shell", 0),
            CodeSnippet("html_div", "<div />", "<div className=\"\">\n    \n</div>", "HTML", 9)
        )
    }
}

data class KeyboardSettings(
    val themeId: String = KeyboardTheme.MonokaiPro.id,
    val heightRatio: Float = 0.44f, // 44% of screen height - strictly strictly <= 0.48f
    val hapticLevel: Int = 2, // 0 = off, 1 = light, 2 = medium, 3 = strong
    val keySound: Boolean = true,
    val autoCloseBrackets: Boolean = true,
    val autoCloseQuotes: Boolean = true,
    val smartBracketStepOver: Boolean = true,
    val smartBackspacePair: Boolean = true,
    val autoIndentOnEnter: Boolean = true,
    val showDedicatedNumberRow: Boolean = true,
    val spacebarSwipeCursor: Boolean = true,
    val autoCapitalize: Boolean = true,
    val tabSpaces: Int = 4, // 2, 4 or 0 (for \t)
    val keyRoundnessDp: Int = 8,
    val showSecondaryLabels: Boolean = true,
    val showKeystrokePopup: Boolean = true,
    val enableKeystrokeAudit: Boolean = false,
    val persistentCoderBar: Boolean = true,
    val coderBarKeys: List<String> = listOf(
        "(", ")", "{", "}", "[", "]", ";", "=", "=>", "&&", "||", "!", ":", ".", "\"", "'", "`", "$", "#", "?"
    )
)
