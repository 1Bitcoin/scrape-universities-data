package org.jetbrains.database

import dao.UniversityYGSNMIREA
import dto.UniversityYGSNMIREAData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertUniversityYGSNMIREA(universitiesYGSNMIREAData: MutableList<UniversityYGSNMIREAData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (universityYGSN in universitiesYGSNMIREAData) {
                UniversityYGSNMIREA.insert {
                    it[universityId] = universityYGSN.universityId
                    it[ygsnId] = universityYGSN.ygsnId
                    it[contingentStudents] = universityYGSN.contingentStudents
                    it[dolyaContingenta] = universityYGSN.dolyaContingenta
                    it[numbersBudgetStudents] = universityYGSN.numbersBudgetStudents
                    it[averageScoreBudgetEGE] = universityYGSN.averageScoreBudgetEGE
                    it[year] = universityYGSN.year
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}