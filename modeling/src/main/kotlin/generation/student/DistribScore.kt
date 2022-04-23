package main.kotlin.generation.student

enum class DistribScore(val procent: Double) {
    // От 0 баллов до минимального по предмету
    FAIL(0.1),

    // От минимального до 70 баллов
    MIDDLE(0.4),

    // От 70 до 80 баллов
    SENIOR(0.35),

    // От 80 до 99 баллов
    GENIUS(0.15)

}