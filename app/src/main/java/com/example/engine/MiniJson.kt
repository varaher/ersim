package com.example.engine

/**
 * Minimal strict JSON parser (RFC 8259 subset sufficient for case files).
 * Returns Map<String, Any?>, List<Any?>, String, Double, Boolean or null.
 * Pure Kotlin so case files parse identically on Android and in JVM unit tests.
 */
object MiniJson {

    class JsonParseException(message: String) : RuntimeException(message)

    fun parse(text: String): Any? {
        val p = Parser(text)
        p.skipWs()
        val v = p.readValue()
        p.skipWs()
        if (!p.atEnd()) throw p.error("Unexpected trailing content")
        return v
    }

    private class Parser(val s: String) {
        var i = 0

        fun atEnd() = i >= s.length
        fun error(msg: String): JsonParseException {
            val line = s.substring(0, i.coerceAtMost(s.length)).count { it == '\n' } + 1
            return JsonParseException("$msg at line $line (offset $i)")
        }

        fun skipWs() {
            while (i < s.length && s[i].isWhitespace()) i++
        }

        fun readValue(): Any? {
            skipWs()
            if (atEnd()) throw error("Unexpected end of input")
            return when (val c = s[i]) {
                '{' -> readObject()
                '[' -> readArray()
                '"' -> readString()
                't' -> readLiteral("true", true)
                'f' -> readLiteral("false", false)
                'n' -> readLiteral("null", null)
                else -> if (c == '-' || c.isDigit()) readNumber() else throw error("Unexpected character '$c'")
            }
        }

        fun readLiteral(word: String, value: Any?): Any? {
            if (!s.startsWith(word, i)) throw error("Expected $word")
            i += word.length
            return value
        }

        fun readNumber(): Double {
            val start = i
            if (s[i] == '-') i++
            while (i < s.length && (s[i].isDigit() || s[i] in ".eE+-")) i++
            return s.substring(start, i).toDoubleOrNull() ?: throw error("Bad number")
        }

        fun readString(): String {
            i++ // opening quote
            val sb = StringBuilder()
            while (true) {
                if (atEnd()) throw error("Unterminated string")
                val c = s[i++]
                when (c) {
                    '"' -> return sb.toString()
                    '\\' -> {
                        if (atEnd()) throw error("Bad escape")
                        when (val e = s[i++]) {
                            '"' -> sb.append('"'); '\\' -> sb.append('\\'); '/' -> sb.append('/')
                            'b' -> sb.append('\b'); 'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n'); 'r' -> sb.append('\r'); 't' -> sb.append('\t')
                            'u' -> {
                                if (i + 4 > s.length) throw error("Bad unicode escape")
                                sb.append(s.substring(i, i + 4).toInt(16).toChar()); i += 4
                            }
                            else -> throw error("Bad escape \\$e")
                        }
                    }
                    else -> sb.append(c)
                }
            }
        }

        fun readArray(): List<Any?> {
            i++
            val out = ArrayList<Any?>()
            skipWs()
            if (s.getOrNull(i) == ']') { i++; return out }
            while (true) {
                out.add(readValue())
                skipWs()
                when (s.getOrNull(i)) {
                    ',' -> i++
                    ']' -> { i++; return out }
                    else -> throw error("Expected , or ]")
                }
            }
        }

        fun readObject(): Map<String, Any?> {
            i++
            val out = LinkedHashMap<String, Any?>()
            skipWs()
            if (s.getOrNull(i) == '}') { i++; return out }
            while (true) {
                skipWs()
                if (s.getOrNull(i) != '"') throw error("Expected key")
                val key = readString()
                skipWs()
                if (s.getOrNull(i) != ':') throw error("Expected :")
                i++
                if (out.containsKey(key)) throw error("Duplicate key \"$key\"")
                out[key] = readValue()
                skipWs()
                when (s.getOrNull(i)) {
                    ',' -> i++
                    '}' -> { i++; return out }
                    else -> throw error("Expected , or }")
                }
            }
        }
    }
}
