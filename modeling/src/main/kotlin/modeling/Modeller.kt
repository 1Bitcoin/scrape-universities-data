package main.kotlin.modeling

import main.kotlin.dto.ModellerLog
import main.kotlin.modeling.dto.*
import org.springframework.web.client.RestTemplate
import java.io.BufferedWriter
import java.net.URI
import java.util.concurrent.CopyOnWriteArrayList


class Modeller(limitStudent: Int = 100000, year: Int, bufferedWriter: BufferedWriter) {

    val restTemplate = RestTemplate()

    val baseUrl = "http://localhost:8080/logs"
    val uri = URI(baseUrl)

    val countStudents = limitStudent

    val helper = ModelingHelper(limitStudent, year)

    // Анализ производится по сравнению со стат.данными ВУЗов и их УГСН следующего года
    val analyzer = Analyzer(year + 1);

    val students = helper.informationStudent
    val universities = helper.informationUniversityMap

    val comparator = compareBy<Statement> { it.score }

    var currentMinutes = 10
    var currentHour = 9
    var currentDay = 20
    var currentMouth = 6
    var currentYear = 21

    val writer = bufferedWriter

    fun modeling() {
        val start = System.currentTimeMillis()

        firstStep()
        secondStep()
        thirdStep()

        val end = System.currentTimeMillis()

        val messageModeling = "Время моделирования при $countStudents студентах = " + (end - start) / 1000 + " секунд\n"
        println(messageModeling)

        restTemplate.postForEntity(uri, ModellerLog(messageModeling), String::class.java)

        //writer.write(messageModeling)

        val messageAnalyze = "Анализ результатов проведенного моделирования\n"
        println(messageAnalyze)

        //writer.write(messageAnalyze)

        restTemplate.postForEntity(uri, ModellerLog(messageAnalyze), String::class.java)

        analyzer.analyzeResults(universities, writer)
    }

    /**
     * Базовое распределение студентов по универам, студенты просматривают универы, начиная с самого престижного,
     * проверяют наличие интересующего УГСН, смотрят какие ЕГЭ необходимы и сравнивают со своими результатами
     * Заявления можно подать не более чем в 5 вузов, не более чем на 10 УГСН
     */
    private fun firstStep() {
        val messageFirstStep = "||| Начало первого этапа моделирования |||\n"

        println(messageFirstStep)
        restTemplate.postForEntity(uri, ModellerLog(messageFirstStep), String::class.java)
        //writer.write(messageFirstStep)

        val currentDate = "$currentDay/$currentMouth/$currentYear $currentHour:$currentMinutes:00"

        for (student in students) {
            val studentId = student.studentData.studentId
            val studentRegion = student.studentData.region
            val studentYGSN = student.ygsnList
            val studentEGE = student.egeList
            val isChangeRegion = student.studentData.change

            for (university in universities.values) {

                // Доки можно класть максимум в 5 универов!
                if (student.getCountUniversities() < 5) {
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

                                val messageSubmitFirstStep = "Абитуриент $studentId подал копию заявления в университет " +
                                        "${university.universityData.universityId} на УГСН ${informationYGSN.ygsnData.ygsnId} " +
                                        "| средний балл по УГСН: ${informationYGSN.ygsnData.averageScoreBudgetEGE} " +
                                        "| средний балл студента: $averageScoreStudent\n"

                                println(messageSubmitFirstStep)
                                restTemplate.postForEntity(uri, ModellerLog(messageSubmitFirstStep), String::class.java)

                                //writer.write(messageSubmitFirstStep)

                            }
                        }
                    }
                } else {
                    val messageMaxUniversities = "Абитуриент $studentId может подать заявление максимум в 5 универов!\n"

                    println(messageMaxUniversities)
                    restTemplate.postForEntity(uri, ModellerLog(messageMaxUniversities), String::class.java)

                    //writer.write(messageMaxUniversities)

                    break
                }
            }

            if (student.getCountUniversities() == 0) {
                val messageZeroSubmit = "Абитуриент $studentId не смог подать копию ни в один университет!\n"

                println(messageZeroSubmit)
                restTemplate.postForEntity(uri, ModellerLog(messageZeroSubmit), String::class.java)

                //writer.write(messageZeroSubmit)
            }
        }

        currentDay++
    }

    /**
     * Каждый абитуриент будет оценивать свою ситуацию, анализируя свое положение в конкурсном списке
     * и по кол-ву доступных бюджетных мест на данный УГСН. Если он не попадает в n доступных мест,
     * то забирает копию своего заявления и кладет в другой универ, где есть места на интересующем его УГСН.
     * Так продолжается до дня X - когда надо положить оригинал и произойдет зачисление
     */
    private fun secondStep(countDays: Int = 10) {
        val messageSecondStep = "||| Начало второго этапа моделирования |||\n"

        println(messageSecondStep)
        restTemplate.postForEntity(uri, ModellerLog(messageSecondStep), String::class.java)

        //writer.write(messageSecondStep)

        val students = helper.informationStudent

        // Моделируем приемную кампанию длительностью countDays(по умолчанию 30)
        for (i in 1..countDays) {
            val currentDate = "$currentDay/$currentMouth/$currentYear $currentHour:$currentMinutes:00"

            // Смотрим каждый универ, в который подал заявления студент
            val studentIterator = students.listIterator()

            // Обходим каждого студента и рассматриваем его ситуацию
            while (studentIterator.hasNext()) {
                val student = studentIterator.next()
                val studentId = student.studentData.studentId
                val isChangeRegion = student.studentData.change
                val studentRegion = student.studentData.region

                // Смотрим каждую заявку студента
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

                    // Смотрим каждую заявку в конкретном вузе
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
                                    student.revokeRequest(universityId, choseStudent)

                                    val messageOutOfNumbers = "Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                            "$universityId на УГСН $ygsnId. Заявление перекладывается в другой ВУЗ.\n"

                                    println(messageOutOfNumbers)
                                    restTemplate.postForEntity(uri, ModellerLog(messageOutOfNumbers), String::class.java)

                                    //writer.write(messageOutOfNumbers)

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

                                val messageOutOfNumbers1 = "Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                        "$universityId на УГСН $ygsnId. Заявление необходимо положить в еще один ВУЗ.\n"

                                println(messageOutOfNumbers1)
                                restTemplate.postForEntity(uri, ModellerLog(messageOutOfNumbers1), String::class.java)

                                //writer.write(messageOutOfNumbers1)

                                // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем
                                searchUniversities(listIterator, student, mutableListOf(ygsnId), currentDate)
                            }

                        } else {
                            val messageNumbersOk = "Абитуриент $studentId находится в пределе бюджетных мест в университете " +
                                    "$universityId на УГСН $ygsnId. Заявление не трогаем.\n"

                            println(messageNumbersOk)
                            restTemplate.postForEntity(uri, ModellerLog(messageNumbersOk), String::class.java)

                            //writer.write(messageNumbersOk)
                        }
                    }

                    // В этом случае у студента есть несколько заявлений в одном ВУЗе
                    // Проверяем по простому соотношению: если кол-во УГСН, на которые студент проходит
                    // больше или равно кол-ву на которые не проходит, то заявление не трогаем
                    // Пример: студент подал на УГСН 1, УГСН 2, УГСН 3, на да из них он проходит -
                    // заявление не трогаем

                    // Проверка, что нужно переложить заявления
                    if (countFailRequest != 0) {

                        // Если число заявок, на которые студент не проходит больше числа заявок,
                        // на которые он проходит
                        if (countFailRequest / countChoseStudent > 0.5) {

                            // Удаляем предыдущие заявления данного студента в данном универе
                            for (ygsnId in ygsnFailList) {
                                universities[universityId]!!.revokeRequest(studentId, ygsnId)
                            }

                            // Удаляем все заявление у студента для данного универа
                            val listDeleteIterator: MutableIterator<ChoiceStudent> = choseStudentList.iterator()

                            while (listDeleteIterator.hasNext()) {
                                val choseStudent = listDeleteIterator.next()
                                student.revokeRequest(universityId, choseStudent)
                            }

                            val messageRevokeOldRequest = "Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId. Прошлые заявления на УГСН $ygsnFailList в данном ВУЗе были отозваны. " +
                                    "Заявления перекладываются в другой подходящий ВУЗ.\n"

                            println(messageRevokeOldRequest)
                            restTemplate.postForEntity(uri, ModellerLog(messageRevokeOldRequest), String::class.java)

                            //writer.write(messageRevokeOldRequest)

                            // Ищем подходящий универ, в который еще не подавали и нужный УГСН в нем
                            searchUniversities(listIterator, student, ygsnFailList, currentDate)

                        } else {
                            val messageOk = "Абитуриент $studentId вышел за пределы бюджетных мест в университете " +
                                    "$universityId по некоторым УГСН. Заявления не трогаем.\n"

                            println(messageOk)
                            restTemplate.postForEntity(uri, ModellerLog(messageOk), String::class.java)

                            //writer.write(messageOk)
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
    private fun searchUniversities(
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
                    currentHolder.universityId = universityId

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

                            // Если на данный УГСН нет поданной заявки этого студента
                            if (competitiveList.find { it.studentId == studentId } == null) {

                                // Если попадаем в бюджетные места, то помечаем данный УГСН в данном универе как подходящий
                                if (numbersBudget >= countRequest) {
                                    currentHolder.ygsnList.add(currentYGSN)
                                    currentHolder.additionalInformation.add(
                                        HolderResult.AdditionalInformation(
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
                    val university = universities[universityId]!!

                    university.submitRequest(studentId, item.ygsnId, item.score,
                        item.state, currentDate)

                    student.addRequest(universityId, ChoiceStudent(item.ygsnId, item.state), listIterator)

                    val messageNewRequest = "Абитуриент $studentId ПЕРЕПОДАЛ копию заявления в университет " +
                            "${university.universityData.universityId} на УГСН ${item.ygsnId} " +
                            "| место в рейтинге ${item.studentPosition} " +
                            "| бюджетных мест ${item.countBudget}.\n"

                    println(messageNewRequest)
                    restTemplate.postForEntity(uri, ModellerLog(messageNewRequest), String::class.java)

                    //writer.write(messageNewRequest)
                }

                // Необходимо удалить из массива УГСН ид те ид, на которые подали заявление
                ygsnList.removeAll(holder.ygsnList)
            }
        }

        if (ygsnList.isNotEmpty()) {
            val messageNotFound = "Абитуриент $studentId НЕ НАШЕЛ КУДА переподать копии на следующие УГСН: $ygsnList.\n"

            println(messageNotFound)
            restTemplate.postForEntity(uri, ModellerLog(messageNotFound), String::class.java)

            //writer.write(messageNotFound)
        }
    }

    // наступил день Х - необходимо положить оригинал заявлений
    private fun thirdStep(countIterations: Int = 2) {
        val messageThirdStep = "||| Начало третьего этапа моделирования |||\n"

        println(messageThirdStep)
        restTemplate.postForEntity(uri, ModellerLog(messageThirdStep), String::class.java)

        //writer.write(messageThirdStep)

        val students = helper.informationStudent

        // Укрупненное моделирование дня подачи оригиналов заявлений
        for (i in 1..countIterations) {
            val currentDate = "$currentDay/$currentMouth/$currentYear $currentHour:$currentMinutes:00"

            // Смотрим каждый универ, в который подал заявления студент
            val studentIterator = students.listIterator()

            // Обходим каждого студента и рассматриваем его ситуацию
            while (studentIterator.hasNext()) {
                val student = studentIterator.next()
                val studentId = student.studentData.studentId

                if (student.getCountUniversities() == 0) {
                    val messageZeroCopy = "Абитуриент $studentId не имеет копий заявлений ни в одном университете! (пропускается)\n"

                    println(messageZeroCopy)
                    restTemplate.postForEntity(uri, ModellerLog(messageZeroCopy), String::class.java)

                    //writer.write(messageZeroCopy)

                    continue
                }

                // Для каждого студента ищем наиболее выгодный вариант для оригинала заявления
                var resultChoice: ResultChoice? = null

                // Смотрим каждую заявку студента
                val mapIterator: MutableIterator<Map.Entry<Int, CopyOnWriteArrayList<ChoiceStudent>>> = student.choice.iterator()

                while (mapIterator.hasNext()) {
                    val entryChoseStudent = mapIterator.next()

                    val universityId = entryChoseStudent.key
                    val choseStudentList = entryChoseStudent.value

                    // Смотрим каждую заявку в конкретном вузе
                    val listIterator = choseStudentList.listIterator()

                    while (listIterator.hasNext()) {
                        val choseStudent = listIterator.next()

                        val ygsnId = choseStudent.ygsnId

                        val informationYGSN = universities[universityId]!!.getInformationYGSNMap()[ygsnId]!!
                        val numbersBudget = informationYGSN.ygsnData.numbersBudgetStudents
                        val competitiveList = informationYGSN.competitiveList

                        // Смотрим на каком месте в рейтинге среди заявок с оригиналами
                        val statement = competitiveList
                            .filter { it.state == State.ORIGINAL || it.studentId == studentId }
                            .find { it.studentId == studentId }!!

                        val actualIndex = competitiveList
                            .filter { it.state == State.ORIGINAL || it.studentId == studentId }
                            .indexOf(statement)

                        val lastIndex = competitiveList
                            .filter { it.state == State.ORIGINAL || it.studentId == studentId }
                            .lastIndex

                        // Число заявок между этими индексами
                        val countRequest = lastIndex - actualIndex + 1

                        // Если проходим на данный УГСН
                        if (numbersBudget >= countRequest) {

                            // Если на данном УГСН в данном унике уже не лежит оригинал
                            if (statement.state != State.ORIGINAL) {

                                val currentScoreUniversity = universities[universityId]!!
                                    .universityData
                                    .averageAllStudentsEGE

                                val currentScoreYGSN = universities[universityId]!!
                                    .getInformationYGSNMap()[ygsnId]!!
                                    .ygsnData
                                    .averageScoreBudgetEGE

                                // Надо сравнить престижность текущего выбора с предыдущим

                                // Если это заявление - первое рассматриваемое
                                if (resultChoice == null) {
                                    resultChoice = ResultChoice(choseStudent, statement,
                                        currentScoreUniversity, currentScoreYGSN, universityId)

                                } else {
                                    // Нужно сравнить прошлое заявление с текущим по престижности уников
                                    // Или УГСН, если прошлое заявление в тот же унике

                                    // Если прошлое заявление в том же унике
                                    if (resultChoice.universityId == universityId) {

                                        // Сравниваем престижности УГСН
                                        if (currentScoreYGSN > resultChoice.scoreYGSN) {

                                            // Текущий УГСН престижнее результирующего - меняем выбор
                                            resultChoice.apply {
                                                choiceStudent = choseStudent
                                                currentStatement = statement
                                                universityScore = currentScoreUniversity
                                                scoreYGSN = currentScoreYGSN
                                                this.universityId = universityId
                                            }
                                        }
                                    } else {

                                        // Сравниваем по престижности уников
                                        if (currentScoreUniversity > resultChoice.universityScore) {
                                            resultChoice.apply {
                                                choiceStudent = choseStudent
                                                currentStatement = statement
                                                universityScore = currentScoreUniversity
                                                scoreYGSN = currentScoreYGSN
                                                this.universityId = universityId
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Если студенту не нашел место под свой оригинал
                if (resultChoice == null) {
                    if (student.getCurrentOriginal() != null) {

                        val messageOk = "Абитуриент $studentId НЕ НАШЕЛ куда ЛУЧШЕ положить оригинал заявления(уже есть активный).\n"

                        println(messageOk)
                        restTemplate.postForEntity(uri, ModellerLog(messageOk), String::class.java)

                        //writer.write(messageOk)
                    } else {
                        val messageNotOk = "Абитуриент $studentId ВООБЩЕ НЕ НАШЕЛ куда положить оригинал заявления(активных нет).\n"

                        println(messageNotOk)
                        restTemplate.postForEntity(uri, ModellerLog(messageNotOk), String::class.java)

                        //writer.write(messageNotOk)
                    }
                } else {
                    val currentOriginalChoice = student.getCurrentOriginal()

                    // Если оригинал уже есть в каком-то ВУЗе
                    if (currentOriginalChoice != null) {
                        val originalUniversity = universities[currentOriginalChoice.universityId]!!
                        val originalStudentId = currentOriginalChoice.currentStatement.studentId
                        val originalYGSNId = currentOriginalChoice.choiceStudent.ygsnId

                        // Смотрим, выгоднее ли переложить оригинал в другой ВУЗ
                        if (resultChoice.universityScore > currentOriginalChoice.universityScore) {

                            // Если есть другой активный оригинал - убираем ее
                            originalUniversity.changeState(originalStudentId, originalYGSNId, State.COPY, currentDate)
                            student.changeState(originalUniversity.universityData.universityId, originalYGSNId, State.COPY)

                            val message1 = "Абитуриент $studentId убрал ОРИГИНАЛ заявления из университета " +
                                    "${originalUniversity.universityData.universityId} на УГСН " +
                                    "${currentOriginalChoice.choiceStudent.ygsnId}\n"

                            println(message1)
                            restTemplate.postForEntity(uri, ModellerLog(message1), String::class.java)

                            //writer.write(message1)

                            // И кладем новый оригинал
                            val university = universities[resultChoice.universityId]
                            val resultStudentId = resultChoice.currentStatement.studentId
                            val resultYGSNId = resultChoice.choiceStudent.ygsnId

                            university?.changeState(resultStudentId, resultYGSNId, State.ORIGINAL, currentDate)
                            student.changeState(university!!.universityData.universityId, resultYGSNId, State.ORIGINAL)
                            student.setCurrentOriginal(resultChoice)

                            val message2 = "Абитуриент $studentId положил ОРИГИНАЛ заявления в более престижный университет " +
                                    "${university.universityData.universityId} на УГСН $resultYGSNId.\n"

                            println(message2)
                            restTemplate.postForEntity(uri, ModellerLog(message2), String::class.java)

                            //writer.write(message2)

                        } else {

                            val message3 = "Абитуриент $studentId оставил ОРИГИНАЛ заявления в университете " +
                            "${originalUniversity.universityData.universityId} на УГСН " +
                                    "${currentOriginalChoice.choiceStudent.ygsnId}\n"

                            println(message3)
                            restTemplate.postForEntity(uri, ModellerLog(message3), String::class.java)

                            //writer.write(message3)
                        }
                    } else {
                        // Если оригинал еще не подавался ни в один ВУЗ - то подаем
                        val university = universities[resultChoice.universityId]
                        val resultStudentId = resultChoice.currentStatement.studentId
                        val resultYGSNId = resultChoice.choiceStudent.ygsnId

                        university?.changeState(resultStudentId, resultYGSNId, State.ORIGINAL, currentDate)
                        student.changeState(university!!.universityData.universityId, resultYGSNId, State.ORIGINAL)
                        student.setCurrentOriginal(resultChoice)


                        val message4 = "Абитуриент $studentId первый раз положил ОРИГИНАЛ заявления в университет " +
                                "${university.universityData.universityId} на УГСН $resultYGSNId.\n"

                        println(message4)
                        restTemplate.postForEntity(uri, ModellerLog(message4), String::class.java)

                        //writer.write(message4)
                    }
                }
            }

            currentMinutes++
            if (currentMinutes == 61) {
                currentMinutes = 10
                currentHour++
            }

            println("Итерация завершена\n")
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