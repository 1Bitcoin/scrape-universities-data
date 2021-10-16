import org.jetbrains.exposed.sql.Table

object University : Table("university") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 255)
    var yearOfData = integer("sequel_id").uniqueIndex()
    var averageScoreBudgetEGE = double("averageScoreBudgetEGE")
    var averageScorePaidEGE = double("averageScorePaidEGE")
    var growthDeclineAverageScoreBudgetEGE = double("growthDeclineAverageScoreBudgetEGE")
    var growthDeclineAverageScorePaidEGE = double("growthDeclineAverageScorePaidEGE")
    var numbersBudgetStudents = integer("numbersBudgetStudents")
    var numbersStudentWithoutExam = integer("numbersStudentWithoutExam")
    var averageScoreEGEWithoutIndividualAchievements = bool("averageScoreEGEWithoutIndividualAchievements")
    var researchActivities = double("researchActivities")
    var internationalActivity = double("internationalActivity")
    var financialAndEconomicActivities = double("financialAndEconomicActivities")
    var salaryPPP = double("salaryPPP")
    var additionalIndicator = double("additionalIndicator")
}