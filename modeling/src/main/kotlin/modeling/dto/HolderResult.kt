package main.kotlin.modeling.dto

class HolderResult() {
    var universityId: Int = 0
    var ygsnList: MutableList<Int> = mutableListOf()
    var additionalInformation: MutableList<AdditionalInformation> = mutableListOf()

    class AdditionalInformation(currentYGSN: Int, averageScoreStudent: Double,
                                currentState: State, currentPosition: Int, currentCountBudget: Int) {
        val ygsnId = currentYGSN
        val score = averageScoreStudent
        val state = currentState
        val studentPosition = currentPosition
        val countBudget = currentCountBudget
    }
}