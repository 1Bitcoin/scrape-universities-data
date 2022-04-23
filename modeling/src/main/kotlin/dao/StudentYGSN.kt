package main.kotlin.dao

import main.kotlin.dao.UniversityYGSNMIREA.references
import org.jetbrains.exposed.sql.Table

object StudentYGSN : Table("student_ygsn") {
    var id = integer("id").autoIncrement().primaryKey()
    var studentId = integer("student_id") references Student.id
    var ygsnId = integer("ygsn_id") references ygsn.id
}