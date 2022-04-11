package modeling.dto.result

class UniversityTotalResult(id: Int, averageScore: Double, minScore: Double, maxScore: Double,
                            ygsnList: MutableList<YGSNTotalResult>) {
    val universityId = id
    val averageAllBudgetScoreUniversity = averageScore
    val minScore = minScore
    val maxScore = maxScore

    val resultYGSNList = ygsnList

}