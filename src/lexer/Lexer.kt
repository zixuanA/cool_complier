package lexer

import java.io.File

class Lexer : ILexer {
    val keyWords = listOf(
        "class", "else", "false", "fi", "if", "in", "inherits", "isvoid",
        "let", "loop", "pool", "then", "while", "case", "esac", "new", "of", "not", "true"
    )
    val input = File("input/Main").inputStream()
    var next = input.read().toChar()
    var buffered = input.read().toChar()
    private var isInputEnd = false
    private var line = 1
    override fun getLine(): Int = line


    public override fun getNextToken(): Token? {

        return when {
            isDigit() -> analysisInteger()
            isLetter_() -> analysisIdentifier()
            next == '"' -> analysisString()
            isBlack() -> analysisWhiteSpace()
            isSingleLineComment() -> analysisSingleLineComment()
            isStartOfMutableLineComment() -> analysisMutableLineComment()
            isOperators() -> analysisOperators()
            next == (-1).toChar() -> null
            else -> throw Exception("in line $line")
        }
    }



    fun readNextChar() {
        next = buffered
        if (next == '\n') line++
        buffered = input.read().toChar()
    }

    private fun analysisInteger(): Token {
        val builder = StringBuilder()
        while (next in '0'..'9') {
            builder.append(next)
            readNextChar()
        }
        return Token(INTEGER, builder.toString())
    }

    private fun analysisIdentifier(): Token {
        val builder = StringBuilder()

        builder.append(next)
        readNextChar()

        while (isValidNamingChar()) {
            builder.append(next)
            readNextChar()
        }
        builder.toString().let {
            return if (keyWords.contains(it)) Token(KEYWORDS, it)
            else Token(IDENTIFIER, it)
        }
    }

    private fun analysisSingleLineComment(): Token? {
        while (next != '\n') {
            readNextChar()
        }
        readNextChar()
        return getNextToken()
    }

    private fun analysisMutableLineComment(): Token? {
        while (!isEndOfMutableLineComment()) {
            readNextChar()
        }
        readNextChar()
        readNextChar()
        return getNextToken()
    }

    private fun analysisString(): Token {
        val builder = StringBuilder()
        readNextChar()
        while (next != '"') {
            builder.append(next)
            readNextChar()
        }
        readNextChar()
        return Token(STRING, builder.toString())
    }

    private fun analysisWhiteSpace(): Token? {
        while (isBlack()) {
            readNextChar()
        }
        return getNextToken()
    }

    private fun analysisOperators(): Token {
        return when {
            next == '<' && (buffered == '-' || buffered == '=') -> Token(OPERATOR, next.plus(buffered.toString())).apply { readNextChar() }
            next == '=' && buffered == '>' -> Token(OPERATOR, next.plus(buffered.toString())).apply { readNextChar() }

            else -> Token(OPERATOR, next.toString())
        }.apply { readNextChar() }
    }

    private fun isDigit(): Boolean = next in '0'..'9'

    private fun isLetter_(): Boolean = next in 'a'..'z' || next in 'A'..'Z' || next == '_'

    private fun isValidNamingChar(): Boolean = isLetter_() || isDigit()

    private fun isSingleLineComment(): Boolean = (next == '-' && buffered == '-')

    private fun isOperators(): Boolean =
        next == '+' || next == '-' || next == '*' || next == '/' || next == '=' ||
                next == '<' || next == ':' || next == '(' || next == ')' || next == '.' || next == ';' || next == ',' || next == '{' || next == '}'

    private fun isStartOfMutableLineComment(): Boolean = (next == '(' && buffered == '*')
    private fun isEndOfMutableLineComment(): Boolean = (next == '*' && buffered == ')')

    private fun isBlack(): Boolean = when (next) {
        ' ', '\n', '\r', '\t', 12.toChar(), 11.toChar() -> true
        else -> false
    }

}