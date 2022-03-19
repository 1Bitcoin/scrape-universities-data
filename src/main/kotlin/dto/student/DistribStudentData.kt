package dto.student

import dao.DistribStudent

data class DistribStudentData(
    var region: String = "",
    var countVYP: Int = 0,
    var countParticipant: Int = 0,
    var count100Ball: Int = 0
)