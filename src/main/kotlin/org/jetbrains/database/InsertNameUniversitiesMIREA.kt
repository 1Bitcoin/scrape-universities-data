package org.jetbrains.database

import dao.NameUniversitiesMIREA
import dto.NameUniversitiesData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertNameUniversitiesMIREA(mutableListNameUniversitiesData: MutableList<NameUniversitiesData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (nameUniversitiesMIREA in mutableListNameUniversitiesData) {
                NameUniversitiesMIREA.insert {
                    it[name] = nameUniversitiesMIREA.name
                    it[generalname] = nameUniversitiesMIREA.generalname
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}