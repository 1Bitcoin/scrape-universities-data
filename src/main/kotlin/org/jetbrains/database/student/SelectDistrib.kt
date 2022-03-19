package org.jetbrains.database.student

import dao.DistribStudent
import dto.student.DistribStudentData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun selectDistrib(): MutableList<DistribStudentData> {
    val list = mutableListOf<DistribStudentData>()
    transaction {
        addLogger(StdOutSqlLogger)

        for (row in DistribStudent.selectAll()) {
            val distribStudentData = DistribStudentData().apply {
                region = row[DistribStudent.region]
                countVYP = row[DistribStudent.countVYP]
                countParticipant = row[DistribStudent.countParticipant]
                count100Ball = row[DistribStudent.count100Ball]
            }
            list.add(distribStudentData)
        }
    }
    return list
}