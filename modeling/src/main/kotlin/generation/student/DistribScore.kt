package main.kotlin.generation.student

enum class DistribScore(var procent: Int) {
    // От 0 баллов до минимального по предмету
    FAIL(2),

    // От минимального до 70 баллов
    MIDDLE(70),

    // От 70 до 80 баллов
    SENIOR(15),

    // От 80 до 99 баллов
    GENIUS(8)

}