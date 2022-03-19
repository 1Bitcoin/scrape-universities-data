package dao

import dao.UniversityYGSNMIREA.references
import org.jetbrains.exposed.sql.Table

object StudentEGE : Table("student_ege") {
    var id = integer("id").autoIncrement().primaryKey()
    var studentId = integer("student_id") references Student.id
    var egeId = integer("ege_id") references EGE.id
    var score = integer("score_ege")
}