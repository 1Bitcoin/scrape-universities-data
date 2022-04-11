package modeling.dto.result

class YGSNTotalResult(id: Int, averageScore: Double, minScore: Double, maxScore: Double, count: Int) {
    val ygsnId = id
    val averageScore = averageScore
    val minScore = minScore
    val maxScore = maxScore
    val countStudents = count
}