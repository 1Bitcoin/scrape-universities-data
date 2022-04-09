package modeling.dto

class ResultChoice(currentChoice: ChoiceStudent, currentStatement: Statement,
                   currentUniversityScore: Double, currentScoreYGSN: Double, currentUniversityId: Int) {
    var choiceStudent = currentChoice
    var currentStatement = currentStatement

    // Для определения престижности
    var universityScore = currentUniversityScore
    var scoreYGSN = currentScoreYGSN
    var universityId = currentUniversityId
}