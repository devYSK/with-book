fun main() {
    val l = (1..100).toList()

    l.filter { it % 2 == 0 }
        // 값을 출력하지만, 자료 구조나 데이터를 변경하진 않음
        .also { println(it) }
        .map { it * it }
}