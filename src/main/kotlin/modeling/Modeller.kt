package modeling

import modeling.dto.EGEResult
import modeling.dto.InformationUniversity
import modeling.dto.State

class Modeller(limitStudent: Boolean) {
    val helper = ModelingHelper(limitStudent)


    fun modeling() {
        firstStep()
    }

    // Базовое распределение студентов по универам, студенты просматривают универы, начиная с самого престижного,
    // проверяют наличие интересующего УГСН, смотрят какие ЕГЭ необходимы и сравнивают со своими результатами
    // Заявления можно подать не более чем в 5 вузов, не более чем на 10 УГСН
    private fun firstStep() {
        println("Начало первого этапа моделирования")
        val students = helper.informationStudent
        val universities = helper.informationUniversityMap2020

        for (student in students) {
            val studentId = student.studentData.studentId
            val studentRegion = student.studentData.region
            val studentYGSN = student.ygsnList
            val studentEGE = student.egeList
            val isChangeRegion = student.studentData.change

            // Готов ли студент сменить регион
            // Но в таком случае универ должен иметь общагу

            val currentListUniversities: MutableList<InformationUniversity> = if (isChangeRegion) {
                getAllUniversitiesFromMap(universities)
            } else {
                // Получить список ВУЗов конкретного региона
                universities[studentRegion]!!
            }

            for (university in currentListUniversities) {

                // Доки можно класть максимум в 5 универов!
                if (student.getCountRequests() < 6) {
                    val universityYGSNList = university.ygsnList

                    // Для тех, кто может переехать, нужна общага в другом регионе
                    // В родном регионе она не обязательна
                    if (isChangeRegion && university.getRegion() != studentRegion) {

                        // Общага нужна, но ее нет пропускаем такой универ
                        if (university.universityData.hostel == false) {
                            continue
                        }
                    }

                    // Проверяем есть ли интересующий УГСН в ВУЗе
                    // нужна оптимизация - map в которой ключ это ид УГСНа значение инфо об УГСН
                    for (universityYGSN in universityYGSNList) {

                        // Если в ВУЗе есть интересующий нас УГСН
                        if (universityYGSN.ygsnId in studentYGSN) {

                            // Нужно посчитать средний балл ЕГЭ студента для данного УГСН
                            // результаты необходимых ЕГЭ / кол-во ЕГЭ
                            val averageScoreStudent = calculateAverageScoreStudent(universityYGSN.acceptEGESet, studentEGE)

                            // Проверяем что средний балл студента не ниже среднего балла за прошлый год
                            // !!! Вот тут более умная система нужна, которая будет анализировать рост/падение за прошлые года !!!
                            if (averageScoreStudent - universityYGSN.averageScoreBudgetEGE >= 0) {
                                university.submitRequest(studentId, universityYGSN.ygsnId, averageScoreStudent, State.COPY)
                                student.addRequest(university.universityData.universityId, universityYGSN.ygsnId, State.COPY)

                                println("Абитуриент $studentId подал копию заявления в университет " +
                                        "${university.universityData.universityId} на УГСН ${universityYGSN.ygsnId} " +
                                        "| средний балл по УГСН: ${universityYGSN.averageScoreBudgetEGE} " +
                                        "| средний балл студента: $averageScoreStudent")
                            }
                        }
                    }
                } else {
                    println("Абитуриент может подать заявление максимум в 5 универов!")
                }
            }
        }
    }

    private fun calculateAverageScoreStudent(acceptEGESet: MutableSet<Int>, studentEGE: MutableList<EGEResult>): Double {
        var totalScoreStudent = 0.0
        var countEGE = 0

        // Смотрим какие сданные студентом ЕГЭ необходимы для этого УГСН
        for (egeResult in studentEGE) {
            if (egeResult.egeId in acceptEGESet) {
                totalScoreStudent += egeResult.score
                countEGE++
            }
        }

        return totalScoreStudent / countEGE
    }

    private fun getAllUniversitiesFromMap(map: MutableMap<String, MutableList<InformationUniversity>>): MutableList<InformationUniversity> {
        val resultList: MutableList<InformationUniversity> = mutableListOf()

        // перекладываем все универы в один список и сортируем их
        for (item in map) {
            resultList.addAll(item.value)
        }

        resultList.sortByDescending { it.universityData.averageAllStudentsEGE }
        return resultList
    }
}