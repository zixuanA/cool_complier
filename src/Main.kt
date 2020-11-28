import lexer.Lexer
import parser.Parser

fun main(args: Array<String>) {
    val lexer = Lexer()

    val parser = Parser(lexer)
    parser.program()

}
