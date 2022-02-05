package org.jetbrains.database

import dao.NameUniversitiesHSE
import dao.NameUniversitiesMIREA
import dao.University
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
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
