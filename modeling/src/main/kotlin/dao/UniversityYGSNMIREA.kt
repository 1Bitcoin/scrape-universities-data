package main.kotlin.dao

import org.jetbrains.exposed.sql.Table

object UniversityYGSNMIREA : Table("university_ygsn_mirea") {
    var id = integer("id").autoIncrement().primaryKey()
    var universityId = integer("university_id") references University.id
    var ygsnId = integer("ygsn_id") references ygsn.id
    var year = integer("year")

    var contingentStudents = double("contingentstudents")
    var dolyaContingenta = double("dolyacontingenta")
    var numbersBudgetStudents = integer("numbersbudgetstudents")
    var averageScoreBudgetEGE = double("averagescorebudgetege")
}