package com.example.model

object AutoIndentHelper {

    data class IndentResult(
        val insertText: String,
        val moveCursorBack: Int = 0
    )

    fun calculateEnterInsertion(
        textBeforeCursor: String,
        textAfterCursor: String,
        tabSpaces: Int
    ): IndentResult {
        val indentUnit = if (tabSpaces <= 0) "\t" else " ".repeat(tabSpaces)
        val lastNewline = textBeforeCursor.lastIndexOf('\n')
        val currentLine = if (lastNewline >= 0) textBeforeCursor.substring(lastNewline + 1) else textBeforeCursor

        // Calculate existing leading whitespace on the current line
        val leadingWhitespace = StringBuilder()
        for (ch in currentLine) {
            if (ch == ' ' || ch == '\t') {
                leadingWhitespace.append(ch)
            } else {
                break
            }
        }
        val baseIndent = leadingWhitespace.toString()
        val trimmedLine = currentLine.trim()

        // Check if line ends with an opening block syntax
        val opensBlock = trimmedLine.endsWith("{") ||
                         trimmedLine.endsWith("(") ||
                         trimmedLine.endsWith("[") ||
                         trimmedLine.endsWith(":") ||
                         trimmedLine.endsWith("->") ||
                         trimmedLine.endsWith("=>") ||
                         trimmedLine.endsWith("do") ||
                         trimmedLine.endsWith("then")

        // Check if next character right after cursor is closing delimiter
        val trimmedAfter = textAfterCursor.trimStart(' ', '\t')
        val nextChar = if (trimmedAfter.isNotEmpty()) trimmedAfter[0] else null
        val closesBlock = (trimmedLine.endsWith("{") && nextChar == '}') ||
                          (trimmedLine.endsWith("(") && nextChar == ')') ||
                          (trimmedLine.endsWith("[") && nextChar == ']')

        if (closesBlock) {
            // Split block insertion: \n<baseIndent + indentUnit>\n<baseIndent>
            val fullText = "\n" + baseIndent + indentUnit + "\n" + baseIndent
            val moveBack = baseIndent.length + 1
            return IndentResult(insertText = fullText, moveCursorBack = moveBack)
        }

        if (opensBlock) {
            return IndentResult(insertText = "\n" + baseIndent + indentUnit)
        }

        return IndentResult(insertText = "\n" + baseIndent)
    }
}
