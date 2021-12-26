import org.jetbrains.exposed.sql.Table

object UniversityYGSN : Table("universityygsn") {
    var id = integer("id").autoIncrement().primaryKey()
    var yearOfData = integer("yearofdata")
    var universityName = varchar("universityname", 255)
    var ygsnName = varchar("ygsnname", 255).references(ygsn.name)
    var averageScoreBudgetEGE = double("averagescorebudgetege").nullable()
    var averageScorePaidEGE = double("averagescorepaidege").nullable()
    var growthDeclineAverageScoreBudgetEGE = double("growthdeclineaveragescorebudgetege").nullable()
    var growthDeclineAverageScorePaidEGE = double("growthdeclineaveragescorepaidege").nullable()
    var numbersBudgetStudents = integer("numbersbudgetstudents").nullable()
    var numbersPaidStudents = integer("numberspaidstudents").nullable()
    var numbersStudentWithoutExam = integer("numbersstudentwithoutexam").nullable()
    var averageScoreEGEWithoutIndividualAchievements = bool("averagescoreegewithoutindividualachievements")
    val costEducation = double("costeducation").nullable()
}

data class UniversityYGSNData(
    var yearOfData: Int = 0,
    var universityName: String = "",
    var ygsnName: String = "",
    var averageScoreBudgetEGE: Double? = null,
    var averageScorePaidEGE: Double? = null,
    var growthDeclineAverageScoreBudgetEGE: Double? = null,
    var growthDeclineAverageScorePaidEGE: Double? = null,
    var numbersBudgetStudents: Int? = null,
    var numbersPaidStudents: Int? = null,
    var numbersStudentWithoutExam: Int? = null,
    var averageScoreEGEWithoutIndividualAchievements: Boolean = true,
    var costEducation: Double? = null
)

