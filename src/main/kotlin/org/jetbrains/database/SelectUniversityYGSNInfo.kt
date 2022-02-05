package org.jetbrains.database

import dao.UniversityYGSN
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun selectUniversityYGSNInfo(universityName: String, year: Int,
                             ygsnName: String, dolyaYGSN: Double): Triple<Int, Double, Double>? {
    var result: Triple<Int, Double, Double>? = null

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            val infoYGSN = UniversityYGSN
                .select { UniversityYGSN.universityName.eq(universityName)and
                        UniversityYGSN.yearOfData.eq(year) and
                        UniversityYGSN.ygsnName.eq(ygsnName) }

                .single()

            val numbersBudgetStudents = infoYGSN[UniversityYGSN.numbersBudgetStudents]!!
            val averageScoreBudgetEGE = infoYGSN[UniversityYGSN.averageScoreBudgetEGE]!!

            result = Triple(numbersBudgetStudents, averageScoreBudgetEGE, dolyaYGSN)
        }
    } catch (e: Exception) {
        println(e.message)
    }

    return result
}
