package org.jetbrains.database

import dao.NameUniversitiesHSE
import dto.NameUniversitiesData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertNameUniversitiesHSE(mutableListNameUniversitiesData: MutableList<NameUniversitiesData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (nameUniversitiesHSE in mutableListNameUniversitiesData) {
                NameUniversitiesHSE.insert {
                    it[name] = nameUniversitiesHSE.name
                    it[generalname] = nameUniversitiesHSE.generalname
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}