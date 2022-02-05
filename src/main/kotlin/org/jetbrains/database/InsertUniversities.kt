package org.jetbrains.database

import dao.University
import dto.UniversityData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertUniversities(mutableListUniversitiesData: MutableList<UniversityData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (university in mutableListUniversitiesData) {
                University.insert {
                    it[yearOfData] = university.yearOfData
                    it[name] = university.name
                    it[region] = university.region
                    it[hostel] = university.hostel
                    it[averageAllStudentsEGE] = university.averageAllStudentsEGE
                    it[dolyaOfflineEducation] = university.dolyaOfflineEducation
                    it[averagedMinimalEGE] = university.averagedMinimalEGE
                    it[averageBudgetEGE] = university.averageBudgetEGE
                    it[countVserosBVI] = university.countVserosBVI
                    it[countOlimpBVI] = university.countOlimpBVI
                    it[countCelevoiPriem] = university.countCelevoiPriem
                    it[dolyaCelevoiPriem] = university.dolyaCelevoiPriem
                    it[ydelniyVesInostrancyWithoutSNG] = university.ydelniyVesInostrancyWithoutSNG
                    it[ydelniyVesInostrancySNG] = university.ydelniyVesInostrancySNG
                    it[averageBudgetWithoutSpecialRightsEGE] = university.averageBudgetWithoutSpecialRightsEGE
                    it[jsonYGSN] = university.jsonYGSN
                    it[dataSource] = university.dataSource
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}