package modeling.dto

class Statement(currentStudentId: Int, currentScore: Int, currentState: State) {
    var studentId = currentStudentId
    var score = currentScore
    var state = currentState
}