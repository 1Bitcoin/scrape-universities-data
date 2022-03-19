package generation.student

import dto.student.DistribStudentData
import dto.student.StudentData
import dto.student.StudentEGEData
import dto.student.StudentYGSNData
import org.jetbrains.database.student.*
import kotlin.random.Random

class Generator {
    private val studentList: MutableList<StudentData> = mutableListOf()
    private val egeList: MutableList<StudentEGEData> = mutableListOf()
    private val ygsnList: MutableList<StudentYGSNData> = mutableListOf()


    private val distribList: MutableList<DistribStudentData> = selectDistrib()
    private val EGEMap: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

    val seniorRange = 70..80
    val geniusRange = 80..99

    fun generateStudent() {
        // Заглушка
        val emptyRange: IntRange = 1..0

        // Заполняем мапу вида: ид угсн-set ид егэ
        fillMapEGE()

        // Обходим каждый регион распределения
        for (distrib in distribList) {
            val currentRegion = distrib.region
            val countVYP = distrib.countVYP
            val count100Ball = distrib.count100Ball
            val countParticipant = distrib.countParticipant

            val countFail = countVYP * DistribScore.FAIL.procent
            val countMiddle = countVYP * DistribScore.MIDDLE.procent
            val countSenior = countVYP * DistribScore.SENIOR.procent
            val countGenius = countVYP * DistribScore.GENIUS.procent

            for (j in 1..countMiddle) {
                val studentData = StudentData().apply {
                    region = currentRegion

                    // Пока что никто не уезжает в другие регионы
                    change = false
                }

                val studentId = insertStudent(studentData)

                // Создаем сданные ЕГЭ и их результаты
                val listStudentEGEData = buildStudent(studentId, emptyRange)

                // Сохраянем результаты ЕГЭ
                setActualEGE(listStudentEGEData)

                // Получаем ид выбранных егэ для поиска подходящих угсн
                val setEGEId = getSelectedEGEId(listStudentEGEData)

                // Ищем УГСН ид, которые подходят под имеющиеся ЕГЭ
                val actualYGSNId = getActualYGSN(setEGEId)

                // Задаем интересующие УГСН
                setActualYGSN(actualYGSNId, studentId)
            }

            for (j in 1..countSenior) {
                val studentData = StudentData().apply {
                    region = currentRegion

                    // Пока что никто не уезжает в другие регионы
                    change = false
                }

                val studentId = insertStudent(studentData)

                // Создаем сданные ЕГЭ и их результаты
                val listStudentEGEData = buildStudent(studentId, seniorRange)

                // Сохраянем результаты ЕГЭ
                setActualEGE(listStudentEGEData)

                // Получаем ид выбранных егэ для поиска подходящих угсн
                val setEGEId = getSelectedEGEId(listStudentEGEData)

                // Ищем УГСН ид, которые подходят под имеющиеся ЕГЭ
                val actualYGSNId = getActualYGSN(setEGEId)

                // Задаем интересующие УГСН
                setActualYGSN(actualYGSNId, studentId)
            }


            // Создаем студентов в каждом регионе по числу выпускников
            for (i in 1..countGenius) {
                val studentData = StudentData().apply {
                    region = currentRegion

                    // Пока что никто не уезжает в другие регионы
                    change = false
                }

                val studentId = insertStudent(studentData)

                // Создаем сданные ЕГЭ и их результаты
                val listStudentEGEData = buildStudent(studentId, geniusRange)

                // Сохраянем результаты ЕГЭ
                setActualEGE(listStudentEGEData)

                // Получаем ид выбранных егэ для поиска подходящих угсн
                val setEGEId = getSelectedEGEId(listStudentEGEData)

                // Ищем УГСН ид, которые подходят под имеющиеся ЕГЭ
                val actualYGSNId = getActualYGSN(setEGEId)

                // Задаем интересующие УГСН
                setActualYGSN(actualYGSNId, studentId)
            }
        }
    }

    private fun setActualEGE(listStudentEGEData: MutableList<StudentEGEData>) {
        insertStudentEGE(listStudentEGEData)
    }

    private fun setActualYGSN(actualYGSNId: MutableList<Int>, studentId: Int) {
        insertStudentYGSN(actualYGSNId, studentId)
    }

    private fun getActualYGSN(setEGEId: MutableSet<Int>): MutableList<Int> {
        val actualYGSNId = mutableListOf<Int>()

        for (item in EGEMap) {
            if (item.value.containsAll(setEGEId)) {
                actualYGSNId.add(item.key)
            }
        }
        return actualYGSNId
    }

    private fun fillMapEGE() {
        // id УГСН в таблице
        for (id in 1..58) {
            EGEMap[id] = selectEGE(id)
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
}