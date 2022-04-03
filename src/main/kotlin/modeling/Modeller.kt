package modeling

import modeling.dto.*
import java.util.concurrent.CopyOnWriteArrayList


class Modeller(limitStudent: Int = 1000000) {
    val helper = ModelingHelper(limitStudent)

    val students = helper.informationStudent
    val universities = helper.informationUniversityMap2020

    val comparator = compareBy<Statement> { it.score }

    var currentHour = 9
    var currentDay = 20
    var currentMouth = 6
    var currentYear = 21

    fun modeling() {
        firstStep()
        secondStep()
    }

    // Базовое распределение студентов по универам, студенты просматривают универы, начиная с самого престижного,
    // проверяют наличие интересующего УГСН, смотрят какие ЕГЭ необходимы и сравнивают со своими результатами
    // Заявления можно подать не более чем в 5 вузов, не более чем на 10 УГСН
    private fun firstStep() {
        println("\n||| Начало первого этапа моделирования |||\n")

        val currentDate = "$currentDay/$currentMouth/$currentYear $currentHour:00:00"

        for (student in students) {
            val studentId = student.studentData.studentId
            val studentRegion = student.studentData.region
            val studentYGSN = student.ygsnList
            val studentEGE = student.egeList
            val isChangeRegion = student.studentData.change

            for (university in universities.values) {

                // Доки можно класть максимум в 5 универов!
                if (student.getCountUniversities() < 6) {
                    val informationYGSNCollection = university.getInformationYGSNMap().values

                    val universityId = university.universityData.universityId

                    // Если уник в другом регионе, а студент не может переезжать
                    if (isChangeRegion == false && university.getRegion() != studentRegion) {
                        continue
                    }

                    // Для тех, кто может переехать, нужна общага в другом регионе
                    // В родном регионе она не обязательна
                    if (isChangeRegion == true && university.getRegion() != studentRegion) {

                        // Общага нужна, но ее нет пропускаем такой универ
                        if (university.universityData.hostel == false) {
                            continue
                        }
                    }

                    // Проверяем есть ли интересующий УГСН в ВУЗе
                    for (informationYGSN in informationYGSNCollection) {

                        // Если в ВУЗе есть интересующий нас УГСН
                        if (informationYGSN.ygsnData.ygsnId in studentYGSN) {

                            // Нужно посчитать средний балл ЕГЭ студента для данного УГСН
                            // результаты необходимых ЕГЭ / кол-во ЕГЭ
                            val averageScoreStudent = calculateAverageScoreStudent(informationYGSN.ygsnData.acceptEGESet, studentEGE)

                            // Проверяем что средний балл студента не ниже среднего балла за прошлый год
                            // !!! Вот тут более умная система нужна, которая будет анализировать рост/падение за прошлые года !!!
                            if (averageScoreStudent - informationYGSN.ygsnData.averageScoreBudgetEGE >= 0) {
                                university.submitRequest(studentId, informationYGSN.ygsnData.ygsnId, averageScoreStudent,
                                    State.COPY, currentDate)
                                student.addRequest(universityId, ChoiceStudent(informationYGSN.ygsnData.ygsnId, State.COPY))

                                println("Абитуриент $studentId подал копию заявления в университет " +
                                        "${university.universityData.universityId} на УГСН ${informationYGSN.ygsnData.ygsnId} " +
                                        "| средний балл по УГСН: ${informationYGSN.ygsnData.averageScoreBudgetEGE} " +
                                        "| средний балл студента: $averageScoreStudent")
                            }
                        }
                    }
                } else {
                    println("Абитуриент может подать заявление максимум в 5 универов!")
                    break
                }
            }
        }

        currentDay++
    }

    // Каждый абитуриент будет оценивать свою ситуацию, анализируя свое положение в конкурсном списке
    // и по кол-ву доступных бюджетных мест на данный УГСН. Если он не попадает в n доступных мест,
    // то забирает копию своего заявления и кладет в универ пониже статусом.
    // Так продолжается до дня X - когда надо положить оригинал и произойдет зачисление
    private fun secondStep(countDays: Int = 30) {
        println("\n||| Начало второго этапа моделирования |||\n")
        val students = helper.informationStudent

        // Моделируем приемную кампанию длительностью countDays(по умолчанию 30)
        for (i in 1..countDays) {
            val currentDate = "$currentDay/$currentMouth/$currentYear $currentHour:00:00"

            // Смотрим каждый универ, в который подал заявления студент
            val studentIterator = students.listIterator()

            // Обходим каждого студента и рассматриваем его ситуацию
            while (studentIterator.hasNext()) {
                val student = studentIterator.next()
                val studentId = student.studentData.studentId
                val isChangeRegion = student.studentData.change
                val studentRegion = student.studentData.region

                // Смотрим каждый универ, в который подал заявления студент
                val mapIterator: MutableIterator<Map.Entry<Int, CopyOnWriteArrayList<ChoiceStudent>>> = student.choice.iterator()

                while (mapIterator.hasNext()) {
                    val entryChoseStudent = mapIterator.next()

                    val universityId = entryChoseStudent.key
                    val choseStudentList = entryChoseStudent.value
                    val countChoseStudent = entryChoseStudent.value.size

                    // Число заявок, на которые студент не проходит по кол-ву бюджетных мест
                    var countFailRequest = 0

                    // Список ид УГСН, на которые студент не может попасть
                    // Этот список необходим, чтобы искать ВУЗы, в которых есть такие УГСН, чтобы подать заявление
                    val ygsnFailList = mutableListOf<Int>()

                    // Список заявок, на которые студент не может попасть
                    val requestFailList = mutableListOf<ChoiceStudent>()

                    // Смотрим каждую заявку в этом ВУЗе
                    val listIterator = choseStudentList.listIterator()

                    while (listIterator.hasNext()) {
                        val choseStudent = listIterator.next()

                        val ygsnId = choseStudent.ygsnId

                        val informationYGSN = universities[universityId]!!.getInformationYGSNMap()[ygsnId]!!
                        val numbersBudget = informationYGSN.ygsnData.numbersBudgetStudents
                        val competitiveList = informationYGSN.competitiveList

                        // Смотрим на каком месте в рейтинге
                        val actualIndex = competitiveList.indexOf(competitiveList.find { it.studentId == studentId })
                        val lastIndex = competitiveList.lastIndex

                        // Число заявок между этими индексами
                        val countRequest = lastIndex - actualIndex + 1

                        // Если вышли за рамки бюджетных мест, то надо положить доки в другой универ
                        // НО на тот же УГСН
                        if (numbersBudget < countRequest) {

                            // Если лимит на кол-во вузов у студента превышен, то забираем доки по данному УГСН
                            if (student.getCountUniversities() == 5) {

                                // Если в данном универе заявление только на один УГСН
                                // тогда можно спокойно убирать это заявление
                                if (countChoseStudent == 1) {
                                    universities[universityId]!!.revokeRequest(studentId, ygsnId)
                                    student.revokeRequest(listIterator, universityId, choseStudent)

                                    println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                            "$universityId на УГСН $ygsnId. Заявление перекладывается в другой ВУЗ.")

                                    // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем
                                    searchUniversities(listIterator, student, mutableListOf(ygsnId), currentDate)

                                } else {
                                    // Считаем кол-во заявлений, на которые не проходим
                                    countFailRequest++

                                    // Запоминаем этот УГСН, чтобы переподать именно на него в другом ВУЗе
                                    ygsnFailList.add(ygsnId)

                                    // запоминаем заявки, чтобы удалить их
                                    requestFailList.add(choseStudent)
                                }

                            } else {
                                println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                        "$universityId на УГСН $ygsnId. Заявление необходимо положить в еще один ВУЗ.")

                                // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем
                                searchUniversities(listIterator, student, mutableListOf(ygsnId), currentDate)
                            }

                        } else {
                            println("Абитуриент $studentId находится в пределе бюджетных мест в университете " +
                                    "$universityId на УГСН $ygsnId. Заявление не трогаем.")
                        }
                    }

                    // В этом случае у студента есть несколько заявлений в одном ВУЗе
                    // Проверяем по простому соотношению: если кол-во УГСН, на которые студент проходит
                    // больше или равно кол-ву на которые не проходит, то заявление не трогаем
                    // Пример: студент подал на УГСН 1, УГСН 2, УГСН 3, на да из них он проходит -
                    // заявление не трогаем

                    // Проверка, что нужно переложить заявление(я)
                    if (countFailRequest != 0) {

                        // Если число заявок, на которые студент не проходит больше числа заявок,
                        // на которые он проходит
                        if (countFailRequest / countChoseStudent > 0.5) {

                            // Удаляем предыдущие заявления
                            for (ygsnId in ygsnFailList) {
                                universities[universityId]!!.revokeRequest(studentId, ygsnId)
                            }

                            val listDeleteIterator: MutableIterator<ChoiceStudent> = choseStudentList.iterator()

                            while (listDeleteIterator.hasNext()) {
                                val choseStudent = listDeleteIterator.next()
                                student.revokeRequest(listDeleteIterator, universityId, choseStudent)
                            }

                            // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем
                            searchUniversities(listIterator, student, ygsnFailList, currentDate)

                            println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId. Прошлые заявления в данном ВУЗе были отозваны. " +
                                    "Заявления перекладываются в другой подходящий ВУЗ.")
                        } else {
                            println("Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId по некоторым УГСН. Заявления не трогаем.")
                        }
                    }
                }
            }

            currentDay++
            if (currentDay == 32) {
                currentDay = 1
                currentMouth++
            }
        }
    }

    // Просматриваем уники заново и ищем подходящие
    fun searchUniversities(
        listIterator: MutableListIterator<ChoiceStudent>,
        student: InformationStudent,
        ygsnList: MutableList<Int>,
        currentDate: String
    ) {
        val studentId = student.studentData.studentId
        val studentRegion = student.studentData.region
        val studentEGE = student.egeList
        val isChangeRegion = student.studentData.change

        // Число универов, в которые можно подавать заявления
        val countAvailableUniversities = 5 - student.getCountUniversities()

        // Если еще не нашли новые места для всех УГСН
        if (ygsnList.isNotEmpty()) {
            // Идем по кол-ву свободных универов
            for (i in 1..countAvailableUniversities) {

                // На каждой итерации запоминаем универ и УГСНы, на которые оптимальнее всего положить доки
                val holder = HolderResult()

                for (university in universities.values) {
                    val universityId = university.universityData.universityId

                    // Для каждого универа сравниваем такой объект с результирующим
                    // Если в этом объекте элементов больше, чем в результирующем => больше УГСН => это наиболее выгодный вариант
                    val currentHolder = HolderResult()

                    // Если уник в другом регионе, а студент не может переезжать
                    if (isChangeRegion == false && university.getRegion() != studentRegion) {
                        continue
                    }

                    // Для тех, кто может переехать, нужна общага в другом регионе
                    // В родном регионе она не обязательна
                    if (isChangeRegion == true && university.getRegion() != studentRegion) {

                        // Общага нужна, но ее нет пропускаем такой универ
                        if (university.universityData.hostel == false) {
                            continue
                        }
                    }

                    val informationYGSNCollection = university.getInformationYGSNMap().values

                    // Проверяем есть ли интересующий УГСН в ВУЗе
                    for (informationYGSN in informationYGSNCollection) {

                        // Если в ВУЗе есть интересующий нас УГСН
                        val currentYGSN = informationYGSN.ygsnData.ygsnId
                        if (currentYGSN in ygsnList) {

                            val numbersBudget = informationYGSN.ygsnData.numbersBudgetStudents
                            val competitiveList = informationYGSN.competitiveList

                            // Нужно посчитать средний балл ЕГЭ студента для данного УГСН
                            // результаты необходимых ЕГЭ / кол-во ЕГЭ
                            val averageScoreStudent = calculateAverageScoreStudent(informationYGSN.ygsnData.acceptEGESet, studentEGE)

                            // Смотрим на каком месте в рейтинге были бы, если подали доки
                            val mockStatement = Statement(studentId, averageScoreStudent, State.COPY, "mock")
                            val actualIndex = competitiveList.binarySearch(mockStatement, comparator)

                            val lastIndex = competitiveList.lastIndex

                            // Число заявок между этими индексами
                            val countRequest = lastIndex - actualIndex + 1

                            // Если на данный УГСН нет поданых заявок
                            if (competitiveList.find { it.studentId == studentId } == null) {

                                // Если попадаем в бюджетные места, то помечаем данный УГСН в данном универе как подходящий

                                if (numbersBudget >= countRequest) {
                                    currentHolder.universityId = universityId
                                    currentHolder.ygsnList.add(currentYGSN)
                                    currentHolder.additionalInformation.add(HolderResult.AdditionalInformation(
                                        currentYGSN, averageScoreStudent, State.COPY, countRequest, numbersBudget))
                                }
                            }
                        }
                    }

                    // Сравниваем текущий холдер с результирующим
                    if (currentHolder.ygsnList.size > holder.ygsnList.size) {
                        holder.universityId = currentHolder.universityId
                        holder.ygsnList = currentHolder.ygsnList
                        holder.additionalInformation = currentHolder.additionalInformation
                    }
                }

                // Необходимо подать заявления на отобранные универы
                for (item in holder.additionalInformation) {
                    val universityId = holder.universityId
                    val university = universities[universityId]

                    university!!.submitRequest(studentId, item.ygsnId, item.score,
                        item.state, currentDate)

                    student.addRequest(universityId, ChoiceStudent(item.ygsnId, item.state), listIterator)

                    println("Абитуриент $studentId ПЕРЕПОДАЛ копию заявления в университет " +
                            "${university.universityData.universityId} на УГСН ${item.ygsnId} " +
                            "| место в рейтинге ${item.studentPosition} |" +
                            "бюджетных мест ${item.countBudget}.")
                }

                // Необходимо удалить из массива УГСН ид те ид, на которые подали заявление
                ygsnList.removeAll(holder.ygsnList)
            }
        }

        if (ygsnList.isNotEmpty()) {
            println("Абитуриент $studentId НЕ НАШЕЛ КУДА переподать копии на следующие УГСН: $ygsnList.")
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
}