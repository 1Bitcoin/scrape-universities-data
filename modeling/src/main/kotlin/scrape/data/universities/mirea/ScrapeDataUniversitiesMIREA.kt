package main.kotlin.scrape.data.universities.mirea

import main.kotlin.datasource.vo.DataAboutUniversity
import main.kotlin.dto.UniversityData
import main.kotlin.dto.UniversityYGSNMIREAData
import main.kotlin.org.jetbrains.database.*
import org.jetbrains.database.*
import main.kotlin.parser.parseCSVFileWithDolyaYGSN
import main.kotlin.scrape.data.ssl.SSLHelper
import kotlin.math.roundToInt


fun scrapeUniversityMIREA(dataAboutUniversity: DataAboutUniversity) {
    val mutableListUniversitiesData: MutableList<UniversityData> = mutableListOf()

    getPersonalityUniversityData(dataAboutUniversity.monitoring, mutableListUniversitiesData)

    // Ищем и сопоставляем текущие названия с найденными в интернете
    //val matchedNames = findGeneralNameUniversities(mutableListUniversitiesData)

    // Сохраняем полученные сопоставления
    //insertNameUniversitiesMIREA(matchedNames)
}

fun getPersonalityUniversityData(
    monitoring: MutableList<String>,
    mutableListUniversitiesData: MutableList<UniversityData>
) {
    val parsedDolyaYGSNMutableMap = parseCSVFileWithDolyaYGSN()
    val yearOfMonitoring = arrayListOf(2021, 2020, 2019, 2018, 2017, 2016)

    // Идем по округам
    for (district in monitoring) {
        val helper = SSLHelper()
        val districtPage = helper.getConnection(district)!!.get()
        val universitiesURL = districtPage.select("table[class=an] > tbody")

        // Идем по универам
        for (university in universitiesURL.select("tr")) {
            val id = university.select("td")[1].select("a").attr("href")

            // Имя универа в 2021 году - его установим для всех остальных годов
            var actualUniversityName = ""

            // Имя этого вуза на сайте вышки
            var hseNameUniversity = ""

            // Идем по годам универа
            var countAddedUniversities = 0
            for (year in yearOfMonitoring) {
                val listUniversityYGSNMIREAData = mutableListOf<UniversityYGSNMIREAData>()

                val url = "https://monitoring.miccedu.ru/iam/$year/_vpo/$id"

                println(url)

                val universityPage = helper.getConnection(url)!!.get()

                if (universityPage.select("table#info > tbody > tr").size > 0) {
                    val region = universityPage
                        .select("table#info > tbody > tr")[1]
                        .select("td")[1]
                        .select("a")
                        .text()

                    var nameUniversity = universityPage.select("table#info > tbody > tr > td")[1]
                        .text()
                        .replace('"', ' ')

                    // Запоминаем название в 2021 году и проставляем такое же в остальные года
                    if (year == 2021) {

                        // Получаем название вуза на сайте вышки (т.к инфу будет брать оттуда)
                        hseNameUniversity = getHSEUniversityName(nameUniversity)
                        actualUniversityName = nameUniversity

                    } else {
                        nameUniversity = actualUniversityName
                    }

                    // Если соответствующий вуз на сайте вышки не нашли, нет смысла сюда заходить
                    if (hseNameUniversity.isNotEmpty()) {
                        // Если таблица существует
                        if (universityPage.select("table#analis_dop > tbody").size > 0) {
                            val availabilityHostel = universityPage
                                .select("table#analis_dop > tbody > tr")[49]
                                .select("td")[3]
                                .text()
                                .replace(" ", "")
                                .toInt()

                            var hostel = false

                            if (availabilityHostel != 0)
                                hostel = true

                            val averageAllStudentsEGE = universityPage
                                .select("table#analis_dop > tbody > tr")[5]
                                .select("td")[3]
                                .text()
                                .replace(",", ".")
                                .replace(" ", "")
                                .toDouble()

                            val dolyaOfflineEducation = universityPage
                                .select("table#analis_dop > tbody > tr")[6]
                                .select("td")[3]
                                .text()
                                .replace(",", ".")
                                .replace(" ", "")
                                .toDouble()

                            //println("$averageAllStudentsEGE $dolyaOfflineEducation")

                            val napdeTable = universityPage.select("table[class=napde] > tbody")
                            //println(napdeTable)

                            if (napdeTable.size > 0) {
                                val averageBudgetEGE = napdeTable[0]
                                    .select("tr")[1]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()

                                val averageBudgetWithoutSpecialRightsEGE = napdeTable[0]
                                    .select("tr")[2]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()

                                val averagedMinimalEGE = napdeTable[0]
                                    .select("tr")[4]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()

                                val countVserosBVI = napdeTable[0].select("tr")[5].select("td")[3]
                                    .text().replace(" ", "").toInt()

                                val countOlimpBVI = napdeTable[0].select("tr")[6].select("td")[3]
                                    .text().replace(" ", "").toInt()

                                val countCelevoiPriem = napdeTable[0].select("tr")[7].select("td")[3]
                                    .text().replace(" ", "").toInt()

                                val dolyaCelevoiPriem = napdeTable[0]
                                    .select("tr")[8]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()

                                val ydelniyVesInostrancyWithoutSNG = napdeTable[2]
                                    .select("tr")[1]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()

                                val ydelniyVesInostrancySNG = napdeTable[2]
                                    .select("tr")[2]
                                    .select("td")[3]
                                    .text()
                                    .replace(",", ".")
                                    .replace(" ", "")
                                    .toDouble()


                                val tableYGSN = universityPage.select("table#analis_reg > tbody > tr").iterator()

                                // Попускаем первый элемент
                                val firstElement = tableYGSN.next().select("td").text()

                                // В предыдущих годах таблица была другой, необходимо пропустить строки
                                if (firstElement.contains("по ОКСО")) { // Минобрнауки России от 12.09.2013
                                    while (tableYGSN.hasNext() && !tableYGSN.next().select("td").text().contains("Минобрнауки России от 12.09.2013")) {}
                                }

                                var total = 0

                                // Список списков куда сохраняем triple для каждого УГСН
                                val listOfDataAboutYGSN = mutableListOf<MutableList<Triple<Int, Double, Double>>>()

                                // Идем по списку УГСН вуза
                                while (tableYGSN.hasNext()) {
                                    val element = tableYGSN.next().select("td")

                                    var correctNumbersBudgetStudents = 0
                                    var correctAverageScoreBudgetEGE = 0.0

                                    val ygsn = element[0].text()
                                    val codeYGSN = ygsn.take(2)

                                    // Ищем в какие группы входит УГСН
                                    val tableGroupDolyaYGSN: MutableMap<String, Double> = mutableMapOf()

                                    for (group in parsedDolyaYGSNMutableMap) {
                                        if (group.value.containsKey(codeYGSN)) {
                                            val nameGroup = group.key
                                            val dolyaYGSN = group.value.get(codeYGSN)

                                            tableGroupDolyaYGSN.put(nameGroup, dolyaYGSN!!)
                                        }
                                    }

                                    // Получаем кол-во людей на бюджете и средний балл по УГСН
                                    val dataAboutYGSN: MutableList<Triple<Int, Double, Double>> = mutableListOf()

                                    for (item in tableGroupDolyaYGSN) {
                                        // Получаем и сохраняем информацию об УГСН (с сайта вышки)
                                        val triple = selectUniversityYGSNInfo(hseNameUniversity, year - 1, item.key, item.value)

                                        if (triple != null)
                                            dataAboutYGSN.add(triple)
                                    }

                                    // Вычисляем кол-во людей на бюджете и средний балл по УГСН
                                    // Если что-то нашлось - иначе не считаем
                                    if (dataAboutYGSN.isNotEmpty()) {

                                        // Сохраняем
                                        listOfDataAboutYGSN.add(dataAboutYGSN)

                                        for (triple in dataAboutYGSN) {
                                            // Кол-во студентов этого УГСН на текущей итерации
                                            val numbersBudgetStudents = (triple.first * triple.third).toInt()

                                            // Считаем общее кол-во студентов этого УГСН
                                            correctNumbersBudgetStudents += numbersBudgetStudents

                                            // Средний балл для этого УГСН (еще нужно разделить на кол-во студентов в этом УГСН)
                                            correctAverageScoreBudgetEGE += triple.second * numbersBudgetStudents

                                            // Считаем общее кол-во студентов по вузу за год
                                            total += correctNumbersBudgetStudents
                                        }

                                        // Посчитали средний балл
                                        correctAverageScoreBudgetEGE /= correctNumbersBudgetStudents

                                        // Округляем до 2 знаков
                                        if (!correctAverageScoreBudgetEGE.isNaN()) {
                                            correctAverageScoreBudgetEGE = 100 * correctAverageScoreBudgetEGE.roundToInt() / 100.0

                                            val contingent = element[1]
                                                .text()
                                                .replace(" ", "")
                                                .replace(",", ".")
                                                .toDouble()

                                            val dolyaContingenta = element[2].text()
                                                .replace(" ", "")
                                                .replace(",", ".")
                                                .replace("%", "")
                                                .toDouble()


                                            // Первые 2 цифры названия УГСН - это его ид в нашей БД
                                            val ygsnId = ygsn.take(2).toInt()

                                            val universityYGSNMIREAData = UniversityYGSNMIREAData(ygsnId = ygsnId,
                                                year = year - 1, contingentStudents = contingent,
                                                dolyaContingenta = dolyaContingenta,
                                                numbersBudgetStudents = correctNumbersBudgetStudents,
                                                averageScoreBudgetEGE = correctAverageScoreBudgetEGE)

                                            listUniversityYGSNMIREAData.add(universityYGSNMIREAData)
                                        }
                                    }
                                }

                                // Если хоть 1 УГСН был пересчитан
                                if (listUniversityYGSNMIREAData.isNotEmpty()) {

                                    // Масштабируем кол-во людей на всех УГСН вуза

                                    // Получаем кол-во людей на всех УГСН вуза за этот год
                                    val numbersBudgetByHSE = selectUniversityNumberBudget(hseNameUniversity, year - 1)

                                    if (numbersBudgetByHSE != 0) {

                                        var totalScaledNumbersBudgetUniversity = 0

                                        // Если кол-во подсчитанных людей не совпадает с ожидаемым
                                        if (total != numbersBudgetByHSE) {
                                            val scaleCoefficient = total / numbersBudgetByHSE.toDouble()
                                            var indexTriple = 0

                                            // Идем по всем УГСН вуза
                                            for (ygsnData in listUniversityYGSNMIREAData) {
                                                println("Средний балл до масштабирования: ${ygsnData.averageScoreBudgetEGE}")

                                                var totalScaledNumbersBudgetStudents = 0
                                                var scaledAverageScoreBudgetEGE = 0.0

                                                // По всем долям куда входит данный УГСН
                                                for (triple in listOfDataAboutYGSN[indexTriple]) {

                                                    // Кол-во студентов этого УГСН на текущей итерации
                                                    val scaledNumbersBudgetStudents = ((triple.first * triple.third) / scaleCoefficient).toInt()

                                                    // Считаем общее кол-во студентов этого УГСН
                                                    totalScaledNumbersBudgetStudents += scaledNumbersBudgetStudents

                                                    // Масштабированный средний балл
                                                    scaledAverageScoreBudgetEGE += triple.second * scaledNumbersBudgetStudents

                                                    // Считаем масштабированное кол-во студентов
                                                    totalScaledNumbersBudgetUniversity += totalScaledNumbersBudgetStudents
                                                }

                                                // Посчитали средний балл
                                                scaledAverageScoreBudgetEGE /= totalScaledNumbersBudgetStudents

                                                // Округляем до 2 знаков
                                                if (!scaledAverageScoreBudgetEGE.isNaN()) {
                                                    //scaledAverageScoreBudgetEGE = 100 * scaledAverageScoreBudgetEGE.roundToInt() / 100.0

                                                    println("Средний балл после масштабирования: $scaledAverageScoreBudgetEGE \n")

                                                    ygsnData.apply {
                                                        numbersBudgetStudents = totalScaledNumbersBudgetStudents
                                                        averageScoreBudgetEGE = scaledAverageScoreBudgetEGE
                                                    }
                                                }

                                                indexTriple += 1
                                            }
                                        }

                                        println("Число студентов до масштабирования: $total \n" +
                                                "Число студентов после масштабирования: $totalScaledNumbersBudgetUniversity \n" +
                                                "Эталонное значение: $numbersBudgetByHSE \n\n")

                                        val universityData = UniversityData(
                                            name = nameUniversity,
                                            region = region,
                                            hostel = hostel,
                                            yearOfData = year - 1,
                                            averageAllStudentsEGE = averageAllStudentsEGE,
                                            dolyaOfflineEducation = dolyaOfflineEducation,
                                            averageBudgetEGE = averageBudgetEGE,
                                            averageBudgetWithoutSpecialRightsEGE = averageBudgetWithoutSpecialRightsEGE,
                                            averagedMinimalEGE = averagedMinimalEGE,
                                            countVserosBVI = countVserosBVI,
                                            countOlimpBVI = countOlimpBVI,
                                            countCelevoiPriem = countCelevoiPriem,
                                            dolyaCelevoiPriem = dolyaCelevoiPriem,
                                            ydelniyVesInostrancyWithoutSNG = ydelniyVesInostrancyWithoutSNG,
                                            ydelniyVesInostrancySNG = ydelniyVesInostrancySNG,
                                            dataSource = "MIREA"
                                        )

                                        println(universityData)

                                        val insertedIdUniversity = insertUniversity(universityData)

                                        for (ygsn in listUniversityYGSNMIREAData) {
                                            ygsn.universityId = insertedIdUniversity
                                        }

                                        insertUniversityYGSNMIREA(listUniversityYGSNMIREAData)

                                        countAddedUniversities++
                                    }

                                } else {
                                    println("Список УГСН пуст, вуз пропускается")
                                }
                            }
                        }
                    } else {
                        println("Соответствие вузу на сайте вышки не найдено, вуз пропускается")
                    }
                }
            }
        }
    }
}