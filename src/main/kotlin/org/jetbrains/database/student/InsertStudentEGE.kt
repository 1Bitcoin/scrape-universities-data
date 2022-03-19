package org.jetbrains.database.student

import dao.StudentEGE
import dto.student.StudentEGEData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertStudentEGE(listStudentEGEData: MutableList<StudentEGEData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (item in listStudentEGEData) {
                StudentEGE.insert {
                    it[studentId] = item.studentId
                    it[egeId] = item.egeId
                    it[score] = item.score
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}