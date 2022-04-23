package main.kotlin.modeling.dto.result

class UniversityTotalResult(id: Int, averageScore: Double, minScore: Double, maxScore: Double,
                            ygsnList: MutableList<YGSNTotalResult>, countStudents: Int) {
    val universityId = id
    val averageAllBudgetScoreUniversity = averageScore
    val minScore = minScore
    val maxScore = maxScore

    val resultYGSNList = ygsnList
    val countStudents = countStudents

}