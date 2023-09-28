package ch09.ex2_2_1_DeclaringFunctionsWithReifiedTypeParameters

inline fun <reified T> isA(value: Any) = value is T

fun main(args: Array<String>) {
    println(isA<String>("abc"))
    println(isA<String>(123))
}
