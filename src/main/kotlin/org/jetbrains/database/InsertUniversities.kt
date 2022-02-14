package org.jetbrains.database

import dao.University
import dto.UniversityData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

fun insertUniversity(universitiesData: UniversityData): Int {
    var insertedId = -1

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            insertedId = University.insert {
                it[yearOfData] = universitiesData.yearOfData
                it[name] = universitiesData.name
                it[region] = universitiesData.region
                it[hostel] = universitiesData.hostel
                it[averageAllStudentsEGE] = universitiesData.averageAllStudentsEGE
                it[dolyaOfflineEducation] = universitiesData.dolyaOfflineEducation
                it[averagedMinimalEGE] = universitiesData.averagedMinimalEGE
                it[averageBudgetEGE] = universitiesData.averageBudgetEGE
                it[countVserosBVI] = universitiesData.countVserosBVI
                it[countOlimpBVI] = universitiesData.countOlimpBVI
                it[countCelevoiPriem] = universitiesData.countCelevoiPriem
                it[dolyaCelevoiPriem] = universitiesData.dolyaCelevoiPriem
                it[ydelniyVesInostrancyWithoutSNG] = universitiesData.ydelniyVesInostrancyWithoutSNG
                it[ydelniyVesInostrancySNG] = universitiesData.ydelniyVesInostrancySNG
                it[averageBudgetWithoutSpecialRightsEGE] = universitiesData.averageBudgetWithoutSpecialRightsEGE
                it[dataSource] = universitiesData.dataSource
            } get University.id
        }
    } catch (exception: Exception) {
        println(exception.message)
    }

    return insertedId
}