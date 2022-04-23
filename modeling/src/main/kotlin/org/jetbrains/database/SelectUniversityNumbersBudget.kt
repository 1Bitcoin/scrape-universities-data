package main.kotlin.org.jetbrains.database

import main.kotlin.dao.UniversityYGSN
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun selectUniversityNumberBudget(universityName: String, year: Int): Int {
    var numbersBudget = 0

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            UniversityYGSN
                .select { UniversityYGSN.universityName.eq(universityName) and UniversityYGSN.yearOfData.eq(year) }
                .forEach{ numbersBudget += it[UniversityYGSN.numbersBudgetStudents]!! }
        }
    } catch (e: Exception) {
        println(e.message)
    }

    return numbersBudget
}
