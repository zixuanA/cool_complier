package lexer

interface ILexer {
    public fun getNextToken(): Token?
    fun getLine(): Int
}