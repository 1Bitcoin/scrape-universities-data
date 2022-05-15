package main.kotlin.generation.student

enum class DistribScore(val procent: Int) {
    // От 0 баллов до минимального по предмету
    FAIL(10),

    // От минимального до 70 баллов
    MIDDLE(40),

    // От 70 до 80 баллов
    SENIOR(35),

    // От 80 до 99 баллов
    GENIUS(15)

}