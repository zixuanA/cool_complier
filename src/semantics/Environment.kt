package semantics

import FunctionTable
import parser.Formal
import java.util.*


object Environment {
    private lateinit var currentType: Type
    class Scope {
        private val identifiers = mutableMapOf<String, Type>()
        fun find(identifier: String): Type? {
            return identifiers[identifier]
        }

        fun add(identifier: String, type: Type) {

        }
    }

    private val scopes = ArrayDeque<Scope>()
    fun findSymbol(identifier: String): Type {
        scopes.forEach {
            it.find(identifier)?.let { type ->
                return type
            }
        }
        throw ClassNotFoundException(identifier)
    }

    fun getFunctionResult(type: Type, identifier: String, formals: List<Type>): Type =
        FunctionTable.getFunction(type, identifier, formals).resultType

    fun add(identifier: String, type: Type) {
        scopes.peek().add(identifier, type)
    }


    fun currentClassType(): Type = currentType

    fun enterClass(type: Type) {
        currentType = type
    }

    fun checkScope(identifier: String): Boolean {
        return scopes.peek().find(identifier) != null
    }

    fun enterScope() {
        scopes.push(Scope())
    }

    fun exitScope() {
        scopes.pop()
    }

}