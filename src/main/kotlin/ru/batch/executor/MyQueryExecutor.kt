package ru.batch.executor

import dto.UniversityData
import dto.UniversityYGSNMIREAData
import dto.student.StudentData
import dto.student.StudentEGEData
import dto.student.StudentYGSNData
import modeling.dto.InformationUniversity
import java.sql.DriverManager
import java.sql.Statement

class MyQueryExecutor {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres?reWriteBatchedInserts=true",
        "postgres",
        "qwerty"
    )

    fun selectInformationUniversities(year: Int): MutableList<InformationUniversity> {
        println("Получение из БД информации о универах и их УГСН")
        val prepareStatement = connection.prepareStatement("select * from university where yearofdata = ?")
        prepareStatement.setInt(1, year)

        val resultSet = prepareStatement.executeQuery()

        val list: MutableList<InformationUniversity> = mutableListOf()

        // Обработка каждого вуза
        resultSet.use {
            while (it.next()) {
                val universityData = UniversityData().apply {
                    name = it.getString("name")
                    region = it.getString("region")
                    yearOfData = it.getInt("yearofdata")
                    hostel = it.getBoolean("hostel")
                    averageAllStudentsEGE = it.getDouble("averageallstudentsege")
                    dolyaOfflineEducation = it.getDouble("dolyaofflineeducation")
                    averagedMinimalEGE = it.getDouble("averagedminimalege")
                    averageBudgetEGE = it.getDouble("averagebudgetege")
                    averageAllStudentsEGE = it.getDouble("averageallstudentsege")
                    countVserosBVI = it.getInt("countvserosbvi")
                    countOlimpBVI = it.getInt("countolimpbvi")
                    countCelevoiPriem = it.getInt("countcelevoipriem")
                    dolyaCelevoiPriem = it.getDouble("dolyacelevoipriem")
                    ydelniyVesInostrancyWithoutSNG = it.getDouble("ydelniyvesinostrancywithoutsng")
                    ydelniyVesInostrancySNG = it.getDouble("ydelniyvesinostrancysng")
                    averageBudgetWithoutSpecialRightsEGE = it.getDouble("averagebudgetwithoutspecialrightsege")
                    dataSource = it.getString("datasource")
                }

                val universityId = it.getInt("id")

                // Получаем все УГСН текущего вуза
                val ygsnList = selectUniversityYGSN(universityId, year)

                list.add(InformationUniversity(universityData, ygsnList))
            }
        }
        return list
    }

    private fun selectUniversityYGSN(currentUniversityId: Int, currentYear: Int): MutableList<UniversityYGSNMIREAData> {
        val prepareStatement = connection.prepareStatement("select * from university_ygsn_mirea where " +
                "year = ? and university_id = ?")
        prepareStatement.setInt(1, currentYear)
        prepareStatement.setInt(2, currentUniversityId)

        val resultSet = prepareStatement.executeQuery()

        val list: MutableList<UniversityYGSNMIREAData> = mutableListOf()

        // Обработка каждого УГСН
        resultSet.use {
            while (it.next()) {
                val universityYGSNData = UniversityYGSNMIREAData().apply {
                    universityId = it.getInt("university_id")
                    ygsnId = it.getInt("ygsn_id")
                    year = it.getInt("year")
                    contingentStudents = it.getDouble("contingentstudents")
                    dolyaContingenta = it.getDouble("dolyacontingenta")
                    contingentStudents = it.getDouble("contingentStudents")
                    numbersBudgetStudents = it.getInt("numbersbudgetstudents")
                    averageScoreBudgetEGE = it.getDouble("averagescorebudgetege")
                }
                list.add(universityYGSNData)
            }
        }
        return list
    }

    fun batchInsertStudent(list: MutableList<StudentData>): MutableList<Long> {
        val compiledQuery = "INSERT INTO student(region, change_region)" +
                " VALUES" + "(?, ?)"

        val preparedStatement = connection.prepareStatement(compiledQuery, Statement.RETURN_GENERATED_KEYS)

        for (item in list) {
            preparedStatement.setString(1, item.region)
            preparedStatement.setBoolean(2, item.change)
            preparedStatement.addBatch()
        }

        val start = System.currentTimeMillis()
        val inserted: IntArray = preparedStatement.executeBatch()
        val end = System.currentTimeMillis()

        val rs = preparedStatement.generatedKeys
        val insertedKeys = mutableListOf<Long>()

        while (rs.next()) {
            insertedKeys.add(rs.getLong(1))
        }

        println("total time taken to insert the batch = " + (end - start) + " ms")

        preparedStatement.close()

        return insertedKeys

    }

    fun batchInsertEGE(list: MutableList<StudentEGEData>) {
        val compiledQuery = "INSERT INTO student_ege(student_id, ege_id, score_ege)" +
                " VALUES" + "(?, ?, ?)"

        val preparedStatement = connection.prepareStatement(compiledQuery)

        for (item in list) {
            preparedStatement.setInt(1, item.studentId)
            preparedStatement.setInt(2, item.egeId)
            preparedStatement.setInt(3, item.score)
            preparedStatement.addBatch()
        }

        val start = System.currentTimeMillis()
        val inserted: IntArray = preparedStatement.executeBatch()
        val end = System.currentTimeMillis()

        println("total time taken to insert the batch = " + (end - start) + " ms")

        preparedStatement.close()
    }

    fun batchInsertYGSN(list: MutableList<StudentYGSNData>) {
        val compiledQuery = "INSERT INTO student_ygsn(student_id, ygsn_id)" +
                " VALUES" + "(?, ?)"

        val preparedStatement = connection.prepareStatement(compiledQuery)

        for (item in list) {
            preparedStatement.setInt(1, item.studentId)
            preparedStatement.setInt(2, item.ygsnId)
            preparedStatement.addBatch()
        }

        val start = System.currentTimeMillis()
        val inserted: IntArray = preparedStatement.executeBatch()
        val end = System.currentTimeMillis()

        println("total time taken to insert the batch = " + (end - start) + " ms")

        preparedStatement.close()
    }
}
