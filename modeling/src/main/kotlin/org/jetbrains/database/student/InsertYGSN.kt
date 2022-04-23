package main.kotlin.org.jetbrains.database.student

import main.kotlin.dao.StudentYGSN
import main.kotlin.dto.student.StudentYGSNData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertYGSN(studentYGSNData: StudentYGSNData): Int {
    var insertedId = -1

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            insertedId = StudentYGSN.insert {
                it[studentId] = studentYGSNData.studentId
                it[ygsnId] = studentYGSNData.ygsnId

            } get StudentYGSN.id
        }
    } catch (exception: Exception) {
        println(exception.message)
    }

    return insertedId
}