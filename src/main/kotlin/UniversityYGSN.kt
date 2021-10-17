import org.jetbrains.exposed.sql.Table

object UniversityYGSN : Table("universityygsn") {
    var id = integer("id").autoIncrement().primaryKey()
    var yearOfData = integer("yearofdata")
    var universityName = varchar("universityname", 255)
    var ygsnName = varchar("ygsnname", 255).references(ygsn.name)
    var averageScoreBudgetEGE = double("averagescorebudgetege")
    var averageScorePaidEGE = double("averagescorepaidege")
    var growthDeclineAverageScoreBudgetEGE = double("growthdeclineaveragescorebudgetege").nullable()
    var growthDeclineAverageScorePaidEGE = double("growthdeclineaveragescorepaidege").nullable()
    var numbersBudgetStudents = integer("numbersbudgetstudents")
    var numbersPaidStudents = integer("numberspaidstudents")
    var numbersStudentWithoutExam = integer("numbersstudentwithoutexam")
    var averageScoreEGEWithoutIndividualAchievements = bool("averagescoreegewithoutindividualachievements")
    val costEducation = double("costeducation").nullable()
}

data class UniversityYGSNData(
    var yearOfData: Int = 0,
    var universityName: String = "",
    var ygsnName: String = "",
    var averageScoreBudgetEGE: Double = 0.0,
    var averageScorePaidEGE: Double = 0.0,
    var growthDeclineAverageScoreBudgetEGE: Double? = null,
    var growthDeclineAverageScorePaidEGE: Double? = null,
    var numbersBudgetStudents: Int = 0,
    var numbersPaidStudents: Int = 0,
    var numbersStudentWithoutExam: Int = 0,
    var averageScoreEGEWithoutIndividualAchievements: Boolean = true,
    var costEducation: Double? = null
)

