package parser

import lexer.*

class Parser(val lexer: ILexer) {
    var next: Token = lexer.getNextToken() ?: Token(OPERATOR, "$")

    fun getLine(): Int {
        return lexer.getLine()
    }

    fun moveWhen(keywordOrOperator: String): String {
        if ((next.type == KEYWORDS && next.value == keywordOrOperator) || (next.type == OPERATOR && next.value == keywordOrOperator)) {
            print(next.value)
            val result = next.value
            next = lexer.getNextToken() ?: Token(OPERATOR, "$")
            return result
        } else {
            throw Exception("on line ${getLine()} now is ${next.value}")
        }
    }

    fun moveWhen(type: Int): String {
        if (next.type == type) {
            print(next.value)
            val result = next.value
            next = lexer.getNextToken() ?: Token(OPERATOR, "$")
            return result
        } else throw Exception("on line ${getLine()}")
    }

    fun program(): ASTNode {
        return when {
            nextIsKeywords("class") -> {
                return Program(classes())
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun classes(): MutableList<Class> {
        return when {
            nextIsKeywords("class") -> {
                val clazz = clazz()
                moveWhen(";")
                classes_nullable().apply {
                    add(0,clazz)
                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun classes_nullable(): MutableList<Class> {
        return when {
            nextIsKeywords("class") -> {
                val clazz = clazz()
                moveWhen(";")
                classes_nullable().apply {
                    add(0, clazz)
                }
            }
            nextIsOperator("$") -> mutableListOf()
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun clazz(): Class {
        when {
            nextIsKeywords("class") -> {
                moveWhen("class")
                val type = moveWhen(IDENTIFIER)
                val inheritsType = inheris_options()
                moveWhen("{")
                val features = features()
                moveWhen("}")
                return Class(type, inheritsType,features)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun inheris_options(): String? {
        return when {
            nextIsOperator("{") -> return null
            nextIsKeywords("inherits") -> {
                moveWhen("inherits")
                moveWhen(IDENTIFIER)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun features(): MutableList<Feature> {
        return when {
            nextIsOperator("}") -> return mutableListOf()
            nextIsIdentifier() -> {
                val feature = feature()
                features().apply {
                    add(0,feature)
                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun feature(): Feature {
        when {
            nextIsIdentifier() -> {
                val left = moveWhen(IDENTIFIER)
                val featureExceptLeft = feature_()
                moveWhen(";")
                return featureExceptLeft.toFeature(left)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }
    abstract class Feature_Except_Left{
        abstract fun toFeature(left: String): Feature
    }
    fun feature_(): Feature_Except_Left {
        when {
            nextIsOperator(":") -> {
                moveWhen(":")
                val returnType = moveWhen(IDENTIFIER)
                val expr = feature_options()
                return object: Feature_Except_Left(){
                    override fun toFeature(left: String): Feature {
                        return Feature_Attributes(left, returnType, expr)
                    }

                }
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                val formals = formals()
                moveWhen(")")
                moveWhen(":")
                val returnType = moveWhen(IDENTIFIER)
                moveWhen("{")
                val expr = expr()
                moveWhen("}")
                return object : Feature_Except_Left(){
                    override fun toFeature(left: String): Feature {
                        return Feature_Function(left, formals, returnType, expr)
                    }

                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun feature_options(): Expr? {
        return when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                expr()
            }
            nextIsOperator(";") -> return null
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun formals(): MutableList<Formal> {
        return when {
            nextIsOperator(")") -> return mutableListOf()
            nextIsIdentifier() -> {
                val formal = formal()
                formals_options().apply {
                    add(0, formal)
                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun formals_options(): MutableList<Formal> {
        return when {
            nextIsOperator(")") -> mutableListOf()
            nextIsOperator(",") -> {
                moveWhen(",")
                val formal = formal()
                formals_options().apply {
                    add(0, formal)
                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun formal(): Formal {
        when {
            nextIsIdentifier() -> {
                val id = moveWhen(IDENTIFIER)
                moveWhen(":")
                val type = moveWhen(IDENTIFIER)
                return Formal(id, type)
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun expr(): Expr {
        when {
            nextIsOperator("{") -> {
                moveWhen("{")
                val left = exprs()
                moveWhen("}")
                return expr_ops()?.toExpr(left) ?: left
            }
            nextIsOperator("~") -> {
                moveWhen("~")
                val expr = expr()
                return Expr_Neg(expr)
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                val left = expr()
                moveWhen(")")
                return expr_ops()?.toExpr(left) ?: left
            }
            nextIsIdentifier() -> {
                val id = moveWhen(IDENTIFIER)
                val child = expr_id()
                child.toExpr(id).let {
                    return expr_ops()?.toExpr(it) ?: it
                }

            }
            nextIsKeywords("if") -> {
                moveWhen("if")
                val condition = expr()
                moveWhen("then")
                val trueBranch = expr()
                moveWhen("else")
                val falseBranch = expr()
                moveWhen("fi")
                Expr_If(condition, trueBranch, falseBranch).let {
                    return expr_ops()?.toExpr(it) ?: it
                }
            }
            nextIsKeywords("while") -> {
                moveWhen("while")
                val condition = expr()
                moveWhen("loop")
                val body = expr()
                moveWhen("pool")
                Expr_While(condition, body).let {
                    return expr_ops()?.toExpr(it) ?: it

                }
            }
            nextIsKeywords("let") -> {
                moveWhen("let")
                val id = moveWhen(IDENTIFIER)
                moveWhen(":")
                val type = moveWhen(IDENTIFIER)
                val init = let_setvalue()
                val ops = let_options()
                moveWhen("in")
                val body = expr()
                Expr_Let(id, type, init, ops, body).let {
                    return expr_ops()?.toExpr(it) ?: it
                }
            }
            nextIsKeywords("case") -> {
                moveWhen("case")
                val baseExpr = expr()
                moveWhen("of")
                val id = moveWhen(IDENTIFIER)
                moveWhen(":")
                val type = moveWhen(IDENTIFIER)
                moveWhen("=>")
                val body = expr()
                moveWhen(";")
                val ops = case_options().apply {
                    add(0, AST_Case_Options(id, type, body))
                }
                moveWhen("esac")
                Expr_Case(baseExpr, ops).let {
                    return expr_ops()?.toExpr(it) ?: it
                }

            }
            nextIsKeywords("new") -> {
                moveWhen("new")
                val type = moveWhen(IDENTIFIER)
                Expr_New(type).let {
                    return expr_ops()?.toExpr(it)?:it
                }

            }
            nextIsKeywords("isvoid") -> {
                moveWhen("isvoid")
                val expr = expr()
                Expr_Isvoid(expr).let {
                    return expr_ops()?.toExpr(it)?:it
                }

            }
            nextIsKeywords("not") -> {
                moveWhen("not")
                val expr = expr()
                Expr_Not(expr).let {
                    return expr_ops()?.toExpr(it)?:it

                }
            }
            nextIsInteger() -> {
                val value = moveWhen(INTEGER).toInt()
                Expr_Integer(value).let {
                    return expr_ops()?.toExpr(it)?:it
                }
            }
            nextIsString() -> {
                val value = moveWhen(STRING)
                Expr_String(value).let {
                    return expr_ops()?.toExpr(it)?:it
                }
            }
            nextIsKeywords("true") -> {
                val value = moveWhen("true").toBoolean()
                Expr_Boolean(value).let {
                    return expr_ops()?.toExpr(it)?:it
                }
            }
            nextIsKeywords("false") -> {
                val value = moveWhen("false").toBoolean()
                Expr_Boolean(value).let {
                    return expr_ops()?.toExpr(it)?:it
                }
            }
//            nextIsOperator("+") || nextIsOperator("}") || nextIsOperator("-") || nextIsOperator("*") || nextIsOperator("/") || nextIsOperator(
//                "<"
//            ) ||
//                    nextIsOperator("<=") || nextIsOperator("=") || nextIsOperator("@") || nextIsOperator(".") ||
//            nextIsOperator(")") || nextIsOperator(";") || nextIsOperator(",") || nextIsKeywords("then") || nextIsKeywords("else") ||
//                    nextIsKeywords("fi") || nextIsKeywords("loop") || nextIsKeywords("pool") ||
//                    nextIsKeywords("in") || nextIsKeywords("of")
//            -> return
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun exprs(): Expr_Exprs {
        when {
            nextIsOperator("{") || nextIsOperator("~") || nextIsOperator("(") || nextIsIdentifier() || nextIsKeywords("if") || nextIsKeywords(
                "while"
            ) ||
                    nextIsKeywords("let") || nextIsKeywords("case") || nextIsKeywords("new") || nextIsKeywords("isvoid") || nextIsKeywords(
                "not"
            ) || nextIsInteger() || nextIsString() || nextIsKeywords("true") || nextIsKeywords("false")
            -> {
                val expr = expr()
                moveWhen(";")
                return exprs().apply { exprs.add(0, expr) }
            }
            nextIsOperator("}") -> return Expr_Exprs(mutableListOf())
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun let_setvalue(): ASTNode? {
        return when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                expr()
            }
            nextIsOperator(",") || nextIsKeywords("in") -> return null
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun let_options(): MutableList<AST_Let_Options> {
        when {
            nextIsOperator(",") -> {
                moveWhen(",")
                val id = moveWhen(IDENTIFIER)
                moveWhen(":")
                val type = moveWhen(IDENTIFIER)
                val body = let_setvalue()
                return let_options().apply {
                    add(0, AST_Let_Options(id, type, body))
                }
            }
            nextIsKeywords("in") -> return mutableListOf()
            else -> throw Exception("on line ${getLine()}now is ${next.value}")
        }
    }

    fun case_options(): MutableList<AST_Case_Options> {
        when {
            nextIsIdentifier() -> {
                val id = moveWhen(IDENTIFIER)
                moveWhen(":")
                val type = moveWhen(IDENTIFIER)
                moveWhen("=>")
                val body = expr()
                moveWhen(";")
                return case_options().apply {
                    add(0, AST_Case_Options(id, type, body))
                }
            }
            nextIsKeywords("easc") -> return mutableListOf()
            else -> throw Exception("on line ${getLine()}")
        }
    }
    abstract class Expr_ID_Excepr_Left {
        abstract fun toExpr(left: String): Expr
    }
    fun expr_id(): Expr_ID_Excepr_Left {
        when {
            nextIsOperator("<-") -> {
                moveWhen("<-")
                val child = expr()
                return object : Expr_ID_Excepr_Left() {
                    override fun toExpr(left: String): Expr {
                        return Expr_Assignment(left, child)
                    }

                }
            }
            nextIsOperator("(") -> {
                moveWhen("(")
                val ops = exprs_par()
                moveWhen(")")
                return object : Expr_ID_Excepr_Left() {
                    override fun toExpr(left: String): Expr {
                        return Expr_Local_FunctionCall(left, ops)
                    }

                }
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
                    nextIsKeywords("in") || nextIsKeywords("of") -> return object : Expr_ID_Excepr_Left(){
                override fun toExpr(left: String): Expr {
                    return Expr_Identifier(left)
                }

            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    abstract class Expr_Except_Left {
        abstract fun toExpr(left: Expr): Expr
    }

    fun expr_ops(): Expr_Except_Left? {
        when {
            nextIsOperator("}") || nextIsOperator(")") || nextIsOperator(";") || nextIsOperator(",") || nextIsKeywords("then") || nextIsKeywords(
                "else"
            )
                    || nextIsKeywords("fi") || nextIsKeywords("loop") || nextIsKeywords("pool") || nextIsKeywords("in") || nextIsKeywords(
                "of"
            ) -> return null
            nextIsOperator("+") -> {
                moveWhen("+")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_Add(left, right)
                    }
                }
            }
            nextIsOperator("-") -> {
                moveWhen("-")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_Less(left, right)
                    }
                }
            }
            nextIsOperator("*") -> {
                moveWhen("*")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_Multiply(left, right)
                    }
                }
            }
            nextIsOperator("/") -> {
                moveWhen("/")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_Division(left, right)
                    }
                }
            }
            nextIsOperator("<") -> {
                moveWhen("<")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_LessThan(left, right)
                    }
                }
            }
            nextIsOperator("<=") -> {
                moveWhen("<=")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_LessOrEquals(left, right)
                    }
                }
            }
            nextIsOperator("=") -> {
                moveWhen("=")
                val right = expr()
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_Equals(left, right)
                    }
                }
            }
            nextIsOperator("@") -> {
                moveWhen("@")
                val type = moveWhen(IDENTIFIER)
                moveWhen(".")
                val id = moveWhen(IDENTIFIER)
                moveWhen("(")
                val ops = exprs_par()
                moveWhen(")")
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_StaticFunctionCall(left, type, id, ops)
                    }

                }

            }
            nextIsOperator(".") -> {
                moveWhen(".")
                val id = moveWhen(IDENTIFIER)
                moveWhen("(")
                val ops = exprs_par()
                moveWhen(")")
                return object : Expr_Except_Left() {
                    override fun toExpr(left: Expr): Expr {
                        return Expr_FunctionCall(left, id, ops)
                    }

                }
            }
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun exprs_par(): MutableList<ASTNode> {
        when {
            nextIsOperator("~") || nextIsOperator("(") || nextIsIdentifier() || nextIsKeywords("if") || nextIsKeywords("while") || nextIsKeywords(
                "let"
            ) || nextIsKeywords("case") || nextIsKeywords("new") || nextIsKeywords("isvoid") || nextIsKeywords("not") || nextIsInteger() || nextIsString() || nextIsKeywords(
                "true"
            ) || nextIsKeywords("false") -> {
                val expr = expr()
                return exprs_par_().apply {
                    add(0,expr)
                }

            }
            nextIsOperator(")") -> return mutableListOf()
            else -> throw Exception("on line ${getLine()}")
        }
    }

    fun exprs_par_(): MutableList<ASTNode> {
        when {
            nextIsOperator(",") -> {
                moveWhen(",")
                val expr = expr()
                return exprs_par_().apply {
                    add(0,expr)
                }

            }
            nextIsOperator(")") -> return mutableListOf()
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