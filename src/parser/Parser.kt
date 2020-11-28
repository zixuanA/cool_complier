package parser

import lexer.*

class Parser(val lexer: ILexer) {
    var next: Token = lexer.getNextToken()?:Token(OPERATOR,"$")

    fun getLine():Int{
        return lexer.getLine()
    }
    fun moveWhen(keywordOrOperator: String) {
        if((next.type == KEYWORDS && next.value == keywordOrOperator) || (next.type == OPERATOR && next.value == keywordOrOperator)){
            print(next.value)
            next = lexer.getNextToken()?:Token(OPERATOR,"$")
        }else{
            throw Exception("on line ${getLine()} now is ${next.value}")
        }
    }

    fun moveWhen(type: Int) {
        if(next.type == type){
            print(next.value)
            next = lexer.getNextToken()?:Token(OPERATOR,"$")
        }
        else throw Exception("on line ${getLine()}")
    }

    fun program() {
        when {
            nextIsKeywords("class") -> classes()
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun classes() {
        when {
            nextIsKeywords("class") -> {
                class_()
                moveWhen(";")
                classes_nullable()
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun classes_nullable() {
        when {
            nextIsKeywords("class") -> {
                class_()
                moveWhen(";")
                classes_nullable()
            }
            nextIsOperator("$") -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun class_() {
        when {
            nextIsKeywords("class") -> {
                moveWhen("class")
                moveWhen(IDENTIFIER)
                inheris_options()
                moveWhen("{")
                features()
                moveWhen("}")
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun inheris_options() {
        when {
            nextIsOperator("{") -> return
            nextIsKeywords("inherits") -> {
                moveWhen("inherits")
                moveWhen(IDENTIFIER)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun features() {
        when {
            nextIsOperator("}") -> return
            nextIsIdentifier() -> {
                feature()
                features()
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }
    fun feature() {
        when {
            nextIsIdentifier()->{
                moveWhen(IDENTIFIER)
                feature_()
                moveWhen(";")
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun feature_() {
        when {
            nextIsOperator(":") -> {
                moveWhen(":")
                moveWhen(IDENTIFIER)
                feature_options()
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                formals()
                moveWhen(")")
                moveWhen(":")
                moveWhen(IDENTIFIER)
                moveWhen("{")
                expr()
                moveWhen("}")
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun feature_options() {
        when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                expr()
            }
            nextIsOperator(";") -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun formals() {
        when {
            nextIsOperator(")") -> return
            nextIsIdentifier() -> {
                formal()
                formals_options()
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun formals_options() {
        when {
            nextIsOperator(")") -> return
            nextIsOperator(",") -> {
                moveWhen(",")
                formal()
                formals_options()
            }
            else-> throw Exception("on line ${getLine()}")
        }
    }

    fun formal() {
        when {
            nextIsIdentifier() -> {
                moveWhen(IDENTIFIER)
                moveWhen(":")
                moveWhen(IDENTIFIER)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun expr() {
        when {
            nextIsOperator("{") -> {
                moveWhen("{")
                exprs()
                moveWhen("}")
                expr_ops()
            }
            nextIsOperator("~") -> {
                moveWhen("~")
                expr()
                expr_ops()
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                expr()
                moveWhen(")")
                expr_ops()
            }
            nextIsIdentifier() -> {
                moveWhen(IDENTIFIER)
                expr_id()
                expr_ops()
            }
            nextIsKeywords("if") -> {
                moveWhen("if")
                expr()
                moveWhen("then")
                expr()
                moveWhen("else")
                expr()
                moveWhen("fi")
                expr_ops()
            }
            nextIsKeywords("while") -> {
                moveWhen("while")
                expr()
                moveWhen("loop")
                expr()
                moveWhen("pool")
                expr()
                expr_ops()
            }
            nextIsKeywords("let") -> {
                moveWhen("let")
                moveWhen(IDENTIFIER)
                moveWhen(":")
                moveWhen(IDENTIFIER)
                let_setvalue()
                let_options()
                moveWhen("in")
                expr()
                expr_ops()
            }
            nextIsKeywords("case") -> {
                moveWhen("case")
                expr()
                moveWhen("of")
                moveWhen(IDENTIFIER)
                moveWhen(":")
                moveWhen(IDENTIFIER)
                moveWhen("=>")
                expr()
                moveWhen(";")
                case_options()
                moveWhen("esac")
                expr_ops()
            }
            nextIsKeywords("new") -> {
                moveWhen("new")
                moveWhen(IDENTIFIER)
                expr_ops()
            }
            nextIsKeywords("isvoid") -> {
                moveWhen("isvoid")
                expr()
                expr_ops()
            }
            nextIsKeywords("not") -> {
                moveWhen("not")
                expr()
                expr_ops()
            }
            nextIsInteger() -> {
                moveWhen(INTEGER)
                expr_ops()
            }
            nextIsString() -> {
                moveWhen(STRING)
                expr_ops()
            }
            nextIsKeywords("true") -> {
                moveWhen("true")
                expr_ops()
            }
            nextIsKeywords("false") -> {
                moveWhen("false")
                expr_ops()
            }
                nextIsOperator("+") || nextIsOperator("}") || nextIsOperator("-") || nextIsOperator("*") || nextIsOperator("/") || nextIsOperator(
                    "<"
                ) ||
                        nextIsOperator("<=") || nextIsOperator("=") || nextIsOperator(")") || nextIsOperator(";") || nextIsOperator(
                    ","
                ) || nextIsOperator("@") ||
                        nextIsOperator(".") || nextIsKeywords("then") || nextIsKeywords("else") || nextIsKeywords("fi") || nextIsKeywords(
                    "loop"
                ) || nextIsKeywords("pool") ||
                        nextIsKeywords("in") || nextIsKeywords("of")
            -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun exprs() {
        when {
            nextIsOperator("{") || nextIsOperator("~") || nextIsOperator("(") || nextIsIdentifier() || nextIsKeywords("if") || nextIsKeywords(
                "while"
            ) ||
                    nextIsKeywords("let") || nextIsKeywords("case") || nextIsKeywords("new") || nextIsKeywords("isvoid") || nextIsKeywords(
                "not"
            ) || nextIsInteger() || nextIsString() || nextIsKeywords("true") || nextIsKeywords("false")
            -> {
                expr()
                moveWhen(";")
                exprs()
            }
            nextIsOperator("}")  -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun let_setvalue() {
        when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                expr()
            }
            nextIsOperator(",") || nextIsKeywords("in") -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun let_options() {
        when {
            nextIsOperator(",") -> {
                moveWhen(",")
                moveWhen(IDENTIFIER)
                moveWhen(":")
                moveWhen(IDENTIFIER)
                let_setvalue()
                let_options()
            }
            nextIsKeywords("in") -> return
            else -> throw Exception("on line ${getLine()}now is ${next.value}")
        }
    }

    fun case_options() {
        when {
            nextIsIdentifier() -> {
                moveWhen(IDENTIFIER)
                moveWhen(":")
                moveWhen(IDENTIFIER)
                moveWhen("=>")
                expr()
                moveWhen(";")
                case_options()
            }
            nextIsKeywords("easc") -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun expr_id() {
        when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                expr()
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                exprs_par()
                moveWhen(")")
            }
            nextIsOperator("+") || nextIsOperator("}") || nextIsOperator("-") || nextIsOperator("*") || nextIsOperator("/") || nextIsOperator(
                "<"
            ) ||
                    nextIsOperator("<=") || nextIsOperator("=") || nextIsOperator(")") || nextIsOperator(";") || nextIsOperator(
                ","
            ) || nextIsOperator("@") || nextIsOperator(".") || nextIsKeywords("then") || nextIsKeywords("then") || nextIsKeywords(
                "else"
            ) || nextIsKeywords("fi") || nextIsKeywords(
                "loop"
            ) || nextIsKeywords("pool") ||
                    nextIsKeywords("in") || nextIsKeywords("of") -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun expr_ops() {
        when {
            nextIsOperator("}") || nextIsOperator(")") || nextIsOperator(";") || nextIsOperator(",") || nextIsKeywords("then") || nextIsKeywords(
                "else"
            )
                    || nextIsKeywords("fi") || nextIsKeywords("loop") || nextIsKeywords("pool") || nextIsKeywords("in") || nextIsKeywords(
                "of"
            ) -> return
            nextIsOperator("+") -> {
                moveWhen("+")
                expr()
            }
            nextIsOperator("-") -> {
                moveWhen("-")
                expr()
            }
            nextIsOperator("*") -> {
                moveWhen("*")
                expr()
            }
            nextIsOperator("/") -> {
                moveWhen("/")
                expr()
            }
            nextIsOperator("<") -> {
                moveWhen("<")
                expr()
            }
            nextIsOperator("<=") -> {
                moveWhen("<=")
                expr()
            }
            nextIsOperator("=") -> {
                moveWhen("=")
                expr()
            }
            nextIsOperator("@") -> {
                moveWhen("@")
                moveWhen(IDENTIFIER)
                moveWhen(".")
                moveWhen(IDENTIFIER)
                moveWhen("(")
                exprs_par()
                moveWhen(")")
            }
            nextIsOperator(".")->{
                moveWhen(".")
                moveWhen(IDENTIFIER)
                moveWhen("(")
                exprs_par()
                moveWhen(")")
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }
    fun exprs_par() {
        when {
            nextIsOperator("~") || nextIsOperator("(") || nextIsIdentifier() || nextIsKeywords("if") || nextIsKeywords("while") || nextIsKeywords("let") || nextIsKeywords("case") || nextIsKeywords("new") || nextIsKeywords("isvoid") || nextIsKeywords("not") || nextIsInteger() || nextIsString() || nextIsKeywords("true") || nextIsKeywords("false")->{
                expr()
                exprs_par_()
            }
            nextIsOperator(")")->return
            else -> throw Exception("on line ${getLine()}")
        }
    }
    fun exprs_par_() {
            when{
                nextIsOperator(",")->{
                    moveWhen(",")
                    expr()
                    exprs_par_()
                }
                nextIsOperator(")")->return
                else -> throw Exception("on line ${getLine()}")
            }
    }

    fun nextIsOperator(value: String): Boolean = next.type == OPERATOR && next.value == value


    fun nextIsKeywords(value: String): Boolean {
        return next.type == KEYWORDS && next.value == value
    }

    fun nextIsString(): Boolean {
        return next.type == STRING
    }

    fun nextIsIdentifier(): Boolean {
        return next.type == IDENTIFIER
    }

    fun nextIsInteger(): Boolean {
        return next.type == IDENTIFIER
    }

}