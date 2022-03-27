package modeling

import modeling.dto.EGEResult
import modeling.dto.InformationUniversity
import modeling.dto.State

class Modeller(limitStudent: Boolean) {
    val helper = ModelingHelper(limitStudent)

    val students = helper.informationStudent
    val universities = helper.informationUniversityMap2020
    val allUniversitiesList = getAllUniversitiesFromMap()


    fun modeling() {
        firstStep()
        secondStep()
    }

    // Базовое распределение студентов по универам, студенты просматривают универы, начиная с самого престижного,
    // проверяют наличие интересующего УГСН, смотрят какие ЕГЭ необходимы и сравнивают со своими результатами
    // Заявления можно подать не более чем в 5 вузов, не более чем на 10 УГСН
    private fun firstStep() {
        println("\n||| Начало первого этапа моделирования |||\n")
        val currentDate = "20.06.21"

        for (student in students) {
            val studentId = student.studentData.studentId
            val studentRegion = student.studentData.region
            val studentYGSN = student.ygsnList
            val studentEGE = student.egeList
            val isChangeRegion = student.studentData.change

            // Готов ли студент сменить регион
            // Но в таком случае универ должен иметь общагу
            val currentListUniversities: MutableList<InformationUniversity> = if (isChangeRegion) {
                allUniversitiesList
            } else {
                // Получить список ВУЗов конкретного региона
                universities[studentRegion]!!
            }

            for (university in currentListUniversities) {

                // Доки можно класть максимум в 5 универов!
                if (student.getCountUniversities() < 6) {
                    val informationYGSNCollection = university.getInformationYGSNMap().values

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
                    for (informationYGSN in informationYGSNCollection) {

                        // Если в ВУЗе есть интересующий нас УГСН
                        if (informationYGSN.ygsn.ygsnId in studentYGSN) {

                            // Нужно посчитать средний балл ЕГЭ студента для данного УГСН
                            // результаты необходимых ЕГЭ / кол-во ЕГЭ
                            val averageScoreStudent = calculateAverageScoreStudent(informationYGSN.ygsn.acceptEGESet, studentEGE)

                            // Проверяем что средний балл студента не ниже среднего балла за прошлый год
                            // !!! Вот тут более умная система нужна, которая будет анализировать рост/падение за прошлые года !!!
                            if (averageScoreStudent - informationYGSN.ygsn.averageScoreBudgetEGE >= 0) {
                                university.submitRequest(studentId, informationYGSN.ygsn.ygsnId, averageScoreStudent,
                                    State.COPY, currentDate)
                                student.addRequest(university, informationYGSN.ygsn.ygsnId)

                                println("Абитуриент $studentId подал копию заявления в университет " +
                                        "${university.universityData.universityId} на УГСН ${informationYGSN.ygsn.ygsnId} " +
                                        "| средний балл по УГСН: ${informationYGSN.ygsn.averageScoreBudgetEGE} " +
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

    // Каждый абитуриент будет оценивать свою ситуацию, анализируя свое положение в конкурсном списке
    // и по кол-ву доступных бюджетных мест на данный УГСН. Если он не попадает в n доступных мест,
    // то забирает копию своего заявления и кладет в универ пониже статусом.
    // Так продолжается до дня X - когда надо положить оригинал и произойдет зачисление
    private fun secondStep(countDays: Int = 30) {
        println("\n||| Начало второго этапа моделирования |||\n")
        val students = helper.informationStudent
        val universities = helper.informationUniversityMap2020

        // Моделируем приемную кампанию длительностью countDays(по умолчанию 30)
        for (i in 1..countDays) {

            // Обходим каждого студента и рассматриваем его ситуацию
            for (student in students) {
                val studentId = student.studentData.studentId
                val isChangeRegion = student.studentData.change
                val studentRegion = student.studentData.region

                // Смотрим каждую ссылку на объект InformationUniversity в который студент подал заявление
                for (informationUniversityPair in student.getInformationUniversitiesPair()) {
                    val ygsnId = informationUniversityPair.first
                    val numbersBudget = informationUniversityPair.second.getInformationYGSNMap()[ygsnId]!!.ygsn.numbersBudgetStudents
                    val universityId = informationUniversityPair.second.universityData.universityId
                    val competitiveList = informationUniversityPair.second.getInformationYGSNMap()[ygsnId]!!.competitiveList

                    // Смотрим на каком месте в рейтинге
                    val placeIndexRating = competitiveList.indexOf(competitiveList.find { it.studentId == studentId })

                    val placeRating = placeIndexRating + 1

                    // Если вышли за рамки бюджетных мест, то надо положить доки в другой универ
                    // НО на тот же УГСН
                    if (placeRating > numbersBudget) {

                        // Если лимит на кол-во вузов у студента превышен, то забираем доки по данному УГСН
                        if (student.getCountUniversities() == 5) {
                            informationUniversityPair.second.revokeRequest(studentId, ygsnId)
                            student.revokeRequest(informationUniversityPair.second, ygsnId)

                            println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId на УГСН $ygsnId. Заявление необходимо переложить в другой ВУЗ.")
                        } else {
                            println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId на УГСН $ygsnId. Заявление необходимо положить также в другой ВУЗ.")
                        }

                        // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем



                    } else {
                        println("Абитуриент $studentId находится в пределе бюджетных мест в университете " +
                                "$universityId на УГСН $ygsnId. Заявление не трогаем.")
                    }
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

    private fun getAllUniversitiesFromMap(): MutableList<InformationUniversity> {
        val resultList: MutableList<InformationUniversity> = mutableListOf()

        // перекладываем все универы в один список и сортируем их
        for (item in universities) {
            resultList.addAll(item.value)
        }

        resultList.sortByDescending { it.universityData.averageAllStudentsEGE }
        return resultList
    }
}