package modeling.dto

class Statement(currentStudentId: Int, currentScore: Double, currentState: State,
                currentDatePutRequest: String, currentDateUpdateRequest: String = currentDatePutRequest) {
    var studentId = currentStudentId
    var score = currentScore
    var state = currentState
    var datePutRequest = currentDatePutRequest
    var dateUpdateRequest = currentDateUpdateRequest
}