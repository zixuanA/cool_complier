package lexer

const val INTEGER = 256
const val IDENTIFIER = 257
const val STRING = 258
const val KEYWORDS = 259
const val OPERATOR = 260
const val WHITE = 261

data class Token(val type:Int, val value:String)