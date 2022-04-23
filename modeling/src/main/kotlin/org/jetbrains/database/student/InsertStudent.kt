package main.kotlin.org.jetbrains.database.student

import main.kotlin.dao.Student
import main.kotlin.dto.student.StudentData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertStudent(studentData: StudentData): Int {
    var insertedId = -1

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            insertedId = Student.insert {
                it[region] = studentData.region
                it[change] = studentData.change

            } get Student.id
        }
    } catch (exception: Exception) {
        println(exception.message)
    }

    return insertedId
}