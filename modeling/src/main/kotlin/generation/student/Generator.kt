package main.kotlin.generation.student

import main.kotlin.dto.ModellerLog
import main.kotlin.dto.student.DistribStudentData
import main.kotlin.dto.student.StudentData
import main.kotlin.dto.student.StudentEGEData
import main.kotlin.dto.student.StudentYGSNData
import main.kotlin.org.jetbrains.database.student.selectDistrib
import main.kotlin.org.jetbrains.database.student.selectEGE
import main.kotlin.ru.batch.executor.MyQueryExecutor
import org.springframework.web.client.RestTemplate
import java.net.URI
import kotlin.random.Random

class Generator {
    private val distribList: MutableList<DistribStudentData> = selectDistrib()
    private val mapEGE: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    private val executor: MyQueryExecutor = MyQueryExecutor()

    val seniorRange = 70..80
    val geniusRange = 80..100

    val procentMiddleChange = 60
    val procentSeniorChange = 80
    val procentGeniusChange = 100

    val restTemplate = RestTemplate()

    val baseUrl = "http://localhost:8080/logs"
    val uri = URI(baseUrl)

    fun generateStudent() {

        // Заглушка
        val emptyRange: IntRange = 1..0

        // Заполняем мапу вида: ид угсн-set ид егэ
        val start0 = "Получение из БД списка допустимых предметов ЕГЭ для каждого УГСН"
        restTemplate.postForEntity(uri, ModellerLog(start0), String::class.java)
        fillMapEGE()

        // Рубильник - количество регионов на котором генерим студентов
        var countRegion = 500

        // Обходим каждый регион распределения
        for (distrib in distribList) {
            val start = "Обработка региона: ${distrib.region}"
            println(start)
            restTemplate.postForEntity(uri, ModellerLog(start), String::class.java)

            var insertedStudentIdList: MutableList<Long> = mutableListOf()

            val studentList: MutableList<StudentData> = mutableListOf()
            val egeList: MutableList<StudentEGEData> = mutableListOf()
            val ygsnList: MutableList<StudentYGSNData> = mutableListOf()

            val currentRegion = distrib.region

            val countVYP = distrib.countVYP
            val start1 = "Число выпускников: $countVYP"
            println(start1)
            restTemplate.postForEntity(uri, ModellerLog(start1), String::class.java)


            val count100Ball = distrib.count100Ball
            val countParticipant = distrib.countParticipant

            val countFail = countVYP * DistribScore.FAIL.procent
            val countMiddle = (countVYP * DistribScore.MIDDLE.procent / 100)
            val countSenior = (countVYP * DistribScore.SENIOR.procent / 100)
            val countGenius = (countVYP * DistribScore.GENIUS.procent / 100)

            // Создаем студентов в каждом регионе по числу выпускников
            var count = 1
            var isChange: Boolean
            val start2 = "Создание студентов группы middle"
            println(start2)
            restTemplate.postForEntity(uri, ModellerLog(start2), String::class.java)

            // Число абитуриентов, которые готовы переехать
            val countMiddleChange = countMiddle * procentMiddleChange / 100

            for (i in 1..countMiddle) {
                isChange = countMiddleChange >= count

                val studentData = StudentData().apply {
                    region = currentRegion
                    change = isChange
                }

                count++
                studentList.add(studentData)
            }

            val start3 = "Создание студентов группы senior"
            println(start3)
            restTemplate.postForEntity(uri, ModellerLog(start3), String::class.java)

            count = 1
            // Число абитуриентов, которые готовы переехать
            val countSeniorChange = countSenior * procentSeniorChange / 100

            for (j in 1..countSenior) {
                isChange = countSeniorChange >= count

                val studentData = StudentData().apply {
                    region = currentRegion
                    change = isChange
                }

                count++
                studentList.add(studentData)
            }

            val start4 = "Создание студентов группы genius"
            println(start4)
            restTemplate.postForEntity(uri, ModellerLog(start4), String::class.java)

            count = 1
            // Число абитуриентов, которые готовы переехать
            val countGeniusChange = countGenius * procentGeniusChange / 100

            for (k in 1..countGenius) {
                isChange = countGeniusChange >= count

                val studentData = StudentData().apply {
                    region = currentRegion
                    change = isChange
                }

                count++
                studentList.add(studentData)
            }

            val start5 = "Сохранение студентов в БД. Количество студентов ${studentList.size}"
            println(start5)
            restTemplate.postForEntity(uri, ModellerLog(start5), String::class.java)
            insertedStudentIdList = executor.batchInsertStudent(studentList)

            var currentIndex = 0

            val start6 = "Создание сданных ЕГЭ и интересующих УГСН для студентов группы middle"
            println(start6)
            restTemplate.postForEntity(uri, ModellerLog(start6), String::class.java)

            for (i in 1..countMiddle) {
                val currentStudentId = insertedStudentIdList[currentIndex]
                createFullInformation(currentStudentId.toInt(), emptyRange, egeList, ygsnList)
                currentIndex++
            }

            val start7 = "Создание сданных ЕГЭ и интересующих УГСН для студентов группы senior"
            println(start7)
            restTemplate.postForEntity(uri, ModellerLog(start7), String::class.java)

            for (j in 1..countSenior) {
                val currentStudentId = insertedStudentIdList[currentIndex]
                createFullInformation(currentStudentId.toInt(), seniorRange, egeList, ygsnList)
                currentIndex++
            }

            val start8 = "Создание сданных ЕГЭ и интересующих УГСН для студентов группы genius"
            println(start8)
            restTemplate.postForEntity(uri, ModellerLog(start8), String::class.java)

            for (k in 1..countGenius) {
                val currentStudentId = insertedStudentIdList[currentIndex]
                createFullInformation(currentStudentId.toInt(), geniusRange, egeList, ygsnList)
                currentIndex++
            }

            val start9 = "Сохранение сданных ЕГЭ в БД. Количество записей ${egeList.size}"
            println(start9)
            restTemplate.postForEntity(uri, ModellerLog(start9), String::class.java)

            executor.batchInsertEGE(egeList)

            val start10 = "Сохранение интересующих УГСН в БД. Количество записей ${ygsnList.size}"
            println(start10)
            restTemplate.postForEntity(uri, ModellerLog(start10), String::class.java)

            executor.batchInsertYGSN(ygsnList)

            countRegion--
            if (countRegion == 0) {

                // Заглушка, чтобы сделать распределение по заданному кол-ву регионов
                return
            }
        }
    }

    private fun createFullInformation(studentId: Int, range: IntRange, egeList: MutableList<StudentEGEData>,
                                      ygsnList: MutableList<StudentYGSNData>) {
        // Создаем сданные ЕГЭ и их результаты для студента
        val listStudentEGEData = buildStudent(studentId, range)

        // Сохраняем в список для подсчета кол-ва строк, которые будут сохранены в БД
        egeList.addAll(listStudentEGEData)

        // Получаем ид выбранных егэ для поиска подходящих угсн
        val setEGEId = getSelectedEGEId(listStudentEGEData)

        // Ищем УГСН ид, которые подходят под имеющиеся ЕГЭ
        val listStudentYGSNData = getActualYGSN(setEGEId, studentId)

        if (listStudentYGSNData.isEmpty()) {
            println("empty ygsn: student_id $studentId")
        }

        // !!! Заглушка !!!
        // из всех подходящих по сданным ЕГЭ УГСН выбираем 3 случайных (аля 3 направления)
        val resultListYGSNForStudent = getThreeMostActualYGSN(listStudentYGSNData)

        // Сохраняем в список
        ygsnList.addAll(resultListYGSNForStudent)
    }

    private fun getThreeMostActualYGSN(listStudentYGSNData: MutableList<StudentYGSNData>): MutableList<StudentYGSNData> {
        val resultListYGSNForStudent = mutableListOf<StudentYGSNData>()

        for (i in 1..3) {
            var randomElement = listStudentYGSNData.random()

            // Ищем элемент(ид угсн), которых еще не записывали в ответ
            while (resultListYGSNForStudent.contains(randomElement)) {
                randomElement = listStudentYGSNData.random()
            }
            resultListYGSNForStudent.add(randomElement)
        }
        return resultListYGSNForStudent
    }

    private fun getActualYGSN(setEGEId: MutableSet<Int>, studentId: Int): MutableList<StudentYGSNData> {
        val actualYGSNId = mutableListOf<StudentYGSNData>()

        for (item in mapEGE) {
            // УГСН, на которые есть 2 обязательных предмета и 2 по выбору (один из них)
            if (item.value.size < 5) {
                // Обход по всем выбранным студентом ЕГЭ
                for (egeId in setEGEId) {
                    // Рассматриваем без обязательных ЕГЭ (ид 1 и 2)
                    if (egeId != 1 && egeId != 2) {
                        if (egeId in item.value) {
                            actualYGSNId.add(StudentYGSNData(studentId, item.key))
                        }
                    }
                }
            } else {
                // Нужно чтобы хотя бы 2 сданных ЕГЭ (кроме основных) входили во множество с возможными ЕГЭ для этого УГСН
                var flagCount = 0
                // Обход по всем выбранным студентом ЕГЭ
                for (egeId in setEGEId) {
                    // Рассматриваем без обязательных ЕГЭ (ид 1 и 2)
                    if (egeId != 1 && egeId != 2) {
                        if (egeId in item.value) {
                            flagCount++
                            if (flagCount == 2) {
                                actualYGSNId.add(StudentYGSNData(studentId, item.key))
                            }
                        }
                    }
                }
            }
        }
        return actualYGSNId
    }

    private fun fillMapEGE() {
        // id УГСН в таблице
        for (id in 1..58) {
            mapEGE[id] = selectEGE(id)
        }
    }

    private fun getSelectedEGEId(list: MutableList<StudentEGEData>): MutableSet<Int> {
        val listEGEId = mutableSetOf<Int>()

        for (item in list) {
            listEGEId.add(item.egeId)
        }
        return listEGEId
    }

    private fun buildStudent(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData> {
        return when (Random.nextInt(0, 7)) {
            0 -> setHumanitarian(currentStudentId, intervalScore)
            1 -> setSocial(currentStudentId, intervalScore)
            2 -> setMedical(currentStudentId, intervalScore)
            3 -> setIT(currentStudentId, intervalScore)
            4 -> setEngineer(currentStudentId, intervalScore)
            5 -> setResearch(currentStudentId, intervalScore)
            6 -> setBioIT(currentStudentId, intervalScore)
            7 -> setPhilos(currentStudentId, intervalScore)

            else -> mutableListOf()
        }
    }

    private fun setRequired(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData> {
        val data1 = StudentEGEData().apply {
            // Русский
            studentId = currentStudentId
            egeId = 1
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.RU.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data2 = StudentEGEData().apply {
            // Математика
            studentId = currentStudentId
            egeId = 2
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.MATH.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        return mutableListOf(data1, data2)
    }

    private fun setHumanitarian(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // Иностранный
            studentId = currentStudentId
            egeId = 10
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.EN.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // Литература
            studentId = currentStudentId
            egeId = 15
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.LITER.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        return listStudentEGEData
    }

    private fun setSocial(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // История
            studentId = currentStudentId
            egeId = 5
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.HISTORY.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // Общество
            studentId = currentStudentId
            egeId = 6
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.SOCIAL.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        return listStudentEGEData
    }

    private fun setMedical(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // Химия
            studentId = currentStudentId
            egeId = 4
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.CHEM.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // Биология
            studentId = currentStudentId
            egeId = 8
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.BIO.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        return listStudentEGEData
    }

    private fun setIT(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // ИКТ
            studentId = currentStudentId
            egeId = 7
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.INFORMATION.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        return listStudentEGEData
    }

    private fun setEngineer(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData> {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // Физика
            studentId = currentStudentId
            egeId = 3
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.PHIS.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        return listStudentEGEData
    }

    private fun setResearch(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // Физика
            studentId = currentStudentId
            egeId = 3
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.PHIS.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // ИКТ
            studentId = currentStudentId
            egeId = 7
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.INFORMATION.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        return listStudentEGEData
    }

    private fun setBioIT(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // Химия
            studentId = currentStudentId
            egeId = 4
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.CHEM.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // Биология
            studentId = currentStudentId
            egeId = 8
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.BIO.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data5 = StudentEGEData().apply {
            // ИКТ
            studentId = currentStudentId
            egeId = 7
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.INFORMATION.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        listStudentEGEData.add(data5)
        return listStudentEGEData
    }

    private fun setPhilos(currentStudentId: Int, intervalScore: IntRange): MutableList<StudentEGEData>  {
        val listStudentEGEData = setRequired(currentStudentId, intervalScore)

        val data3 = StudentEGEData().apply {
            // история
            studentId = currentStudentId
            egeId = 5
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.HISTORY.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data4 = StudentEGEData().apply {
            // общество
            studentId = currentStudentId
            egeId = 6
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.SOCIAL.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data5 = StudentEGEData().apply {
            // География
            studentId = currentStudentId
            egeId = 9
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.GEO.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        val data6 = StudentEGEData().apply {
            // литература
            studentId = currentStudentId
            egeId = 15
            score = if (intervalScore.isEmpty()) {
                val currentScore = MinimalEGE.LITER.minimalScore..70
                currentScore.random()
            } else {
                intervalScore.random()
            }
        }

        listStudentEGEData.add(data3)
        listStudentEGEData.add(data4)
        listStudentEGEData.add(data5)
        return listStudentEGEData
    }

    fun testStudent() {
        val test = mutableListOf<StudentData>()

        for (i in 1..3) {
            test.add(StudentData())
        }

        val array = executor.batchInsertStudent(test)

        for (i in array) {
            print("$i ")
        }
    }

    fun testEGE() {
        val test = mutableListOf<StudentEGEData>()

        for (i in 1..60000) {
            test.add(StudentEGEData().apply {
                studentId = 333333
                egeId = 1
                score = 80
            })
        }

        executor.batchInsertEGE(test)
    }

    fun testYGSN() {
        val test = mutableListOf<StudentYGSNData>()

        for (i in 1..60000) {
            test.add(StudentYGSNData().apply {
                studentId = 333333
                ygsnId = 1
            })
        }

        executor.batchInsertYGSN(test)
    }
}