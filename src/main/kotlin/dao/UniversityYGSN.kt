package dao

import org.jetbrains.exposed.sql.Table

object UniversityYGSN : Table("universityygsn") {
    var id = integer("id").autoIncrement().primaryKey()
    var yearOfData = integer("yearofdata")
    var universityName = varchar("universityname", 255)
    var ygsnName = varchar("ygsnname", 255).references(ygsn.name)
    var averageScoreBudgetEGE = double("averagescorebudgetege").nullable()
    var averageScorePaidEGE = double("averagescorepaidege").nullable()
    var numbersBudgetStudents = integer("numbersbudgetstudents").nullable()
    var numbersPaidStudents = integer("numberspaidstudents").nullable()
}