import lexer.Lexer
import parser.Parser

fun main(args: Array<String>) {
    val lexer = Lexer()

    val parser = Parser(lexer)
    val astTree = parser.program()
    astTree.typeCheck()
}
