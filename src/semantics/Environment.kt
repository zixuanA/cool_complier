package semantics

import parser.Formal

object Environment {
    fun findSymbol(identifier: String): Type {

    }
    fun getFunctionResult(type: Type, identifier: String, formals: List<Formal>): Type {

    }
    fun add(identifier: String, type: Type){

    }

    fun parent(source: Type, target: Type): Boolean {

    }
    fun subType(source: Type, target: Type): Boolean{

    }
    fun currentClassType(): Type{

    }
    fun enterClass(type: Type){

    }
    fun registerMethod(clazz :Type, function: String) {

    }
    //返回t1和t2的最小公共父类
    fun lub(t1: Type, t2: Type): Type {

    }
    fun checkScope(identifier: String): Boolean {

    }
    fun enterScope() {

    }

    fun exitScope() {

    }

}