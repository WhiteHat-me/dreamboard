package com.example.model

object AutoCapitalizationHelper {

    fun shouldAutoCapitalize(textBeforeCursor: CharSequence?): Boolean {
        if (textBeforeCursor.isNullOrEmpty()) {
            return true
        }

        var index = textBeforeCursor.length - 1
        var spacesCount = 0

        while (index >= 0 && (textBeforeCursor[index] == ' ' || textBeforeCursor[index] == '\t')) {
            spacesCount++
            index--
        }

        if (index < 0) {
            return true
        }

        val lastChar = textBeforeCursor[index]

        // 1. Line starts / Newlines
        if (lastChar == '\n' || lastChar == '\r') {
            return true
        }

        val sub = textBeforeCursor.substring(0, index + 1)

        // 2. Code comments followed by spaces
        if (spacesCount > 0) {
            val commentBlockStart = "/" + "*"
            if (sub.endsWith("//") || sub.endsWith(commentBlockStart) || sub.endsWith("#") || sub.endsWith("--") || sub.endsWith("<!--")) {
                return true
            }
        }

        // 3. Sentence ending punctuation followed by spaces/newlines
        if (spacesCount > 0 && (lastChar == '.' || lastChar == '?' || lastChar == '!' || lastChar == ':')) {
            // Prevent capitalizing floating point numbers (e.g. "3.14 ")
            if (index > 0 && textBeforeCursor[index - 1].isDigit()) {
                return false
            }
            return true
        }

        // 4. Markdown bullet lists at line start
        if (spacesCount > 0 && (lastChar == '-' || lastChar == '*')) {
            val lineStart = sub.lastIndexOf('\n')
            val line = if (lineStart >= 0) sub.substring(lineStart + 1) else sub
            if (line.trim() == "-" || line.trim() == "*") {
                return true
            }
        }

        return false
    }
}
