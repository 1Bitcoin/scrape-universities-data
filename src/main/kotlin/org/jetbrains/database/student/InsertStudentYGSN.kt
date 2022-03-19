package org.jetbrains.database.student

import dao.NameUniversitiesMIREA
import dao.Student
import dao.StudentYGSN
import dto.NameUniversitiesData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertStudentYGSN(actualEGEId: MutableList<Int>, currentStudentId: Int) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (currentYGSNId in actualEGEId) {
                StudentYGSN.insert {
                    it[studentId] = currentStudentId
                    it[ygsnId] = currentYGSNId
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}