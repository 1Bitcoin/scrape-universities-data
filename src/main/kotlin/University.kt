import org.jetbrains.exposed.sql.Table

object University : Table("university") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 255)
    var yearOfData = integer("yearofdata")
    var averageScoreBudgetEGE = double("averagescorebudgetege")
    var averageScorePaidEGE = double("averagescorepaidege")
    var growthDeclineAverageScoreBudgetEGE = double("growthdeclineaveragescorebudgetege")
    var growthDeclineAverageScorePaidEGE = double("growthdeclineaveragescorepaidege")
    var numbersBudgetStudents = integer("numbersbudgetstudents")
    var numbersPaidStudents = integer("numberspaidstudents")
    var numbersStudentWithoutExam = integer("numbersstudentwithoutexam")
    var averageScoreEGEWithoutIndividualAchievements = bool("averagescoreegewithoutindividualachievements")
    var researchActivities = double("researchactivities")
    var internationalActivity = double("internationalactivity")
    var financialAndEconomicActivities = double("financialandeconomicactivities")
    var salaryPPP = double("salaryppp")
    var additionalIndicator = double("additionalindicator")
}

data class UniversityData(
    var name: String = "",
    var yearOfData: Int = 0,
    var averageScoreBudgetEGE: Double = 0.0,
    var averageScorePaidEGE: Double = 0.0,
    var growthDeclineAverageScoreBudgetEGE: Double = 0.0,
    var growthDeclineAverageScorePaidEGE: Double = 0.0,
    var numbersBudgetStudents: Int = 0,
    var numbersPaidStudents: Int = 0,
    var numbersStudentWithoutExam: Int = 0,
    var averageScoreEGEWithoutIndividualAchievements: Boolean = true,
    var researchActivities: Double = 0.0,
    var internationalActivity: Double = 0.0,
    var financialAndEconomicActivities: Double = 0.0,
    var salaryPPP: Double = 0.0,
    var additionalIndicator: Double = 0.0
)