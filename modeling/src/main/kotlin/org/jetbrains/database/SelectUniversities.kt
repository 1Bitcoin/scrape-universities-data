package main.kotlin.org.jetbrains.database

import main.kotlin.dao.NameUniversitiesHSE
import main.kotlin.dao.NameUniversitiesMIREA
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun getHSEUniversityName(nameUniversity: String): String {
    var generalName = ""
    var hseUniversityName = ""

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            generalName = NameUniversitiesMIREA.select { NameUniversitiesMIREA.name eq nameUniversity }.single()[NameUniversitiesMIREA.generalname]
            hseUniversityName = NameUniversitiesHSE.select { NameUniversitiesHSE.generalname eq generalName }.single()[NameUniversitiesHSE.name]
        }
    } catch (e: Exception) {
        println(e.message)
    }

    return hseUniversityName
}
