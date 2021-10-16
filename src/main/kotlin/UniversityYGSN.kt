import org.jetbrains.exposed.sql.Table

object UniversityYGSN : Table("universityygsn") {
    var id = integer("id").autoIncrement().primaryKey()
    var yearOfData = integer("sequel_id")
    var universityName = varchar("universityName", 255).references(University.name)
    var ysgnName = varchar("ysgnName", 255).references(ygsn.name)
    var averageScoreBudgetEGE = double("averageScoreBudgetEGE")
    var averageScorePaidEGE = double("averageScorePaidEGE")
    var growthDeclineAverageScoreBudgetEGE = double("growthDeclineAverageScoreBudgetEGE")
    var growthDeclineAverageScorePaidEGE = double("growthDeclineAverageScorePaidEGE")
    var numbersBudgetStudents = integer("numbersBudgetStudents")
    var numbersPaidStudents = integer("numbersPaidStudents")
    var numbersStudentWithoutExam = integer("numbersStudentWithoutExam")
    var averageScoreEGEWithoutIndividualAchievements = bool("averageScoreEGEWithoutIndividualAchievements")
}


