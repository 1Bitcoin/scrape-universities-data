package modeling.dto

class Statement(currentStudentId: Int, currentYGSNId: Int, currentScore: Double, currentState: State) {
    var studentId = currentStudentId
    var ygsnId = currentYGSNId
    var score = currentScore
    var state = currentState
}