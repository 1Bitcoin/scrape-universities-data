package ru.batch.executor

import dto.UniversityData
import dto.UniversityYGSNMIREAData
import dto.student.StudentData
import dto.student.StudentEGEData
import dto.student.StudentYGSNData
import modeling.dto.EGEResult
import modeling.dto.InformationStudent
import modeling.dto.InformationUniversity
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class MyQueryExecutor {
    private val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres?reWriteBatchedInserts=true",
        "postgres",
        "qwerty"
    )

    fun selectInformationUniversities(year: Int): MutableMap<String, MutableList<InformationUniversity>> {
        println("Получение из БД информации о универах и их УГСН")

        val informationUniversityMap: MutableMap<String, MutableList<InformationUniversity>> = mutableMapOf()

        val prepareStatement = connection.prepareStatement("select * from university where yearofdata = ?")
        prepareStatement.setInt(1, year)

        val resultSet = prepareStatement.executeQuery()

        val start = System.currentTimeMillis()

        // Обработка каждого вуза
        resultSet.use {
            while (it.next()) {
                val universityData = UniversityData().apply {
                    universityId = it.getInt("id")
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

                // Если в мапе есть ключ-регион, то кладем в соответствующий ему массив информацию об универе
                // иначе - добавляем ключ и начальное значение

                val key = universityData.region
                if (informationUniversityMap.containsKey(key)) {
                    informationUniversityMap[key]!!.add(InformationUniversity(universityData, ygsnList))
                } else {
                    informationUniversityMap[key] = mutableListOf(InformationUniversity(universityData, ygsnList))
                }
            }
        }
        val end = System.currentTimeMillis()

        println("Информация об универах получена!")
        println("Получения информации заняло = " + (end - start) / 1000 + " секунд")

        return informationUniversityMap
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

    fun selectInformationStudent(limit: Boolean): MutableList<InformationStudent> {
        val resultSet: ResultSet = if (limit) {
            println("Получение первых 100 записей из БД с информацией о студентах, интересующих их УСГН и сданных ЕГЭ")

            val prepareStatement = connection.prepareStatement("select * from student limit ?")
            prepareStatement.setInt(1, 100)
            prepareStatement.executeQuery()

        } else {
            println("Получение полной информации из БД информации о студентах, интересующих их УСГН и сданных ЕГЭ")
            connection.prepareStatement("select * from student").executeQuery()
        }

        val list: MutableList<InformationStudent> = mutableListOf()

        val start = System.currentTimeMillis()

        // Обработка каждого студента
        resultSet.use {
            while (it.next()) {
                val studentData = StudentData().apply {
                    studentId = it.getInt("id")
                    region = it.getString("region")
                    change = it.getBoolean("change_region")
                }

                val studentId = it.getInt("id")

                // Получаем все УГСН, интересующие студента
                val ygsnList = selectStudentYGSN(studentId)

                // Получаем все результаты ЕГЭ студента
                val egeList = selectStudentEGE(studentId)

                list.add(InformationStudent(studentData, ygsnList, egeList))
                println("Получена полная информация о студента из БД!")
            }
        }
        val end = System.currentTimeMillis()

        println("Информация о студентах получена!")
        println("Получения информации заняло = " + (end - start) / 1000 + " секунд")

        return list
    }

    private fun selectStudentEGE(studentId: Int): MutableList<EGEResult> {
        val prepareStatement = connection.prepareStatement("select * from student_ege where " +
                "student_id = ?")
        prepareStatement.setInt(1, studentId)

        val resultSet = prepareStatement.executeQuery()

        val list: MutableList<EGEResult> = mutableListOf()

        // Обработка каждой записи
        resultSet.use {
            while (it.next()) {
                val egeId = it.getInt("ege_id")
                val score = it.getInt("score_ege")

                val egeResult = EGEResult(egeId, score)

                list.add(egeResult)
            }
        }
        return list
    }

    private fun selectStudentYGSN(studentId: Int): MutableList<Int> {
        val prepareStatement = connection.prepareStatement("select * from student_ygsn where " +
                "student_id = ?")
        prepareStatement.setInt(1, studentId)

        val resultSet = prepareStatement.executeQuery()

        val list: MutableList<Int> = mutableListOf()

        // Обработка каждой записи
        resultSet.use {
            while (it.next()) {
                val ygsnId = it.getInt("ygsn_id")
                list.add(ygsnId)
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
