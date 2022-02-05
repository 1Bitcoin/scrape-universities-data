import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.File
import java.sql.DriverManager


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty"
    )

    val connection = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/postgres",
        "postgres",
        "qwerty"
    )

        //scrapeUniversityHSE(dataAboutUniversity)

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    connection.use { conn ->
        val prepareStatement = connection.prepareStatement("select * from university")

        var resultSet = prepareStatement.executeQuery()

        resultSet.use {
            while (it.next())
            println(it.getString("name"))

        }
    }

    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)
}

//fun mySelect(mutableListUniversitiesData: MutableList<UniversityData>) {
//    transaction {
//        addLogger(StdOutSqlLogger)
//
//        val universities = University.select { (University.dataSource eq "MIREA") and (University.yearOfData eq 2020) }
//
//        for (i in universities) {
//            mutableListUniversitiesData.add(
//                UniversityData(
//                    name = i[University.name],
//                    yearOfData = i[University.yearOfData]
//                )
//            )
//        }
//    }
//}

fun getAllYGSN(): Set<String> {
    val set = mutableSetOf<String>()
    transaction {
        addLogger(StdOutSqlLogger)

        for (ygsnRow in ygsn.selectAll()) {
            set.add(ygsnRow[ygsn.name])
        }
    }
    return set
}

fun deleteAllYGSN() {
    val set = mutableSetOf<String>()
    transaction {
        addLogger(StdOutSqlLogger)
        ygsn.deleteAll()
    }
}

fun scrapeUniversityMIREA(dataAboutUniversity: DataAboutUniversity) {
    val mutableListUniversitiesData: MutableList<UniversityData> = mutableListOf()

    getPersonalityUniversityData(dataAboutUniversity.monitoring, mutableListUniversitiesData)

    // Сохранили
    insertUniversities(mutableListUniversitiesData)

    // Ищем и сопоставляем текущие названия с найденными в интернете
    //val matchedNames = findGeneralNameUniversities(mutableListUniversitiesData)

    // Сохраняем полученные сопоставления
    //insertNameUniversitiesMIREA(matchedNames)
}

fun scrapeUniversityHSE(dataAboutUniversity: DataAboutUniversity) {
    val mutableListUniversitiesData: MutableList<UniversityData> = mutableListOf()

    for (elem in dataAboutUniversity.dataOfYear) {
        val year = elem.key
        val budgetURL = elem.value.first
        val paidURL = elem.value.second
//
//        getBudgetUniversityData(budgetURL, mutableListUniversitiesData, year)
//        getPaidUniversityData(paidURL, mutableListUniversitiesData, year)

    }
    // Сохранили
    //insertUniversities(mutableListUniversitiesData)

    // Ищем и сопоставляем текущие названия с найденными в интернете
    //val matchedNames = findGeneralNameUniversities(mutableListUniversitiesData)

    // Сохраняем полученные сопоставления
    //insertNameUniversitiesHSE(matchedNames)
}

fun findGeneralNameUniversities(mutableListUniversitiesData: MutableList<UniversityData>): MutableList<NameUniversitiesData> {

    val answer: MutableList<NameUniversitiesData> = mutableListOf()
    var generalName: String

    // Теперь надо взять уникальные уники (в списки были одинаковые, но с разных yearOfData)
    for (university in mutableListUniversitiesData.filter { it.yearOfData == 2020 }) {
        val url = "https://yandex.ru/search/?text=" + university.name

        val doc = Jsoup
            .connect(url)
            .userAgent("Mozilla")
            .timeout(5000).get()

        generalName = doc.select("div.OrgHeader > h2").text()
        val temp = NameUniversitiesData(university.name, generalName)
        answer.add(temp)

        println(temp)
    }

    return answer
}

fun getPersonalityUniversityData(
    monitoring: MutableList<String>,
    mutableListUniversitiesData: MutableList<UniversityData>
) {
    var yearOfMonitoring = arrayListOf(2016, 2017, 2018, 2019, 2020, 2021)

    // Идем по округам
    for (district in monitoring) {
        val districtPage = Jsoup.connect(district).get()
        val universitiesURL = districtPage.select("table[class=an] > tbody")

        // Идем по универам
        for (item in universitiesURL.select("tr")) {
            val id = item.select("td")[1].select("a").attr("href")

            // Идем по годам универа
            var countAddedUniversities = 0
            for (year in yearOfMonitoring) {
                val url = "https://monitoring.miccedu.ru/iam/$year/_vpo/$id"

                println(url)

                val universityPage = Jsoup.connect(url).get()

                if (universityPage.select("table#info > tbody > tr").size > 0) {
                    val region = universityPage
                        .select("table#info > tbody > tr")[1]
                        .select("td")[1]
                        .select("a")
                        .text()

                    val nameUniversity = universityPage.select("table#info > tbody > tr > td")[1]
                        .text()
                        .replace('"', ' ')

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

                        println("$averageAllStudentsEGE $dolyaOfflineEducation")

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
                            val listOfYGSN = mutableListOf<String>()

                            // skit first element
                            val firstElement = tableYGSN.next().select("td").text()

                            // В предыдущих годах таблица была другой, необходимо пропустить строки
                            if (firstElement.contains("по ОКСО")) { // Минобрнауки России от 12.09.2013
                                while (tableYGSN.hasNext() && !tableYGSN.next().select("td").text().contains("Минобрнауки России от 12.09.2013")) {}
                            }

                            var json = "{ "
                            while (tableYGSN.hasNext()) {
                                val element = tableYGSN.next().select("td")

                                val ygsn = element[0].text().drop(11)
                                val contingent = element[1].text().replace(" ", "").replace(",", ".").toDouble()

                                val dolyaContingenta = element[2].text()
                                    .replace(" ", "")
                                    .replace(",", ".")
                                    .replace("%", "")
                                    .toDouble()

                                println("$contingent $dolyaContingenta")
                                json += "$ygsn: { contingentStudents: $contingent, dolyaContingenta: $dolyaContingenta }, "
                            }

                            var resultJson = json.dropLast(2)
                            resultJson += " }"

                            listOfYGSN.add(resultJson)

                            println(listOfYGSN)

                            // change year
                            val university = UniversityData(
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
                                jsonYGSN = listOfYGSN.toString(),
                                dataSource = "MIREA"
                            )

                            mutableListUniversitiesData.add(university)
                            countAddedUniversities++

                            // Проставляем прошлым годам актуальное название данного универа (актуальное - это которое в 2021)
                            if (year == 2021) {
                                for (i in 2..countAddedUniversities) {
                                    mutableListUniversitiesData[mutableListUniversitiesData.size - i].name = nameUniversity
                                }
                            }
                        }
                    }
                }
            }

            for (i in 1..countAddedUniversities) {
                println(mutableListUniversitiesData[mutableListUniversitiesData.size - i])
            }
        }
    }
}

//fun getBudgetUniversityData(url: String, mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {
//
//    val file = File(url)
//    val doc: Document = Jsoup.parse(file, null)
//
//    val row = doc.select("table#transparence_t > tbody > tr")
//
//    for (elem in row) {
//        val nameUniversity = elem.select("td")[0].text().replace('"', ' ')
//        val averageScoreBudgetEGE = elem.select("td")[1].text().toDouble()
//        val growthDeclineAverageScoreBudgetEGE: Double? = if (elem.select("td")[2].text().isNotEmpty()) {
//            elem.select("td")[2].text().toDouble()
//        } else {
//            0.0
//        }
//
//        val numbersBudgetStudents = elem.select("td")[3].text().toInt()
//        val numbersStudentWithoutExam = elem.select("td")[4].text().toInt()
//        val averageScoreEGEWithoutIndividualAchievements: Boolean = elem.select("td")[5].text() != "Да"
//
//        mutableListUniversitiesData.add(
//            UniversityData(
//                name = nameUniversity,
//                yearOfData = year,
//                averageScoreBudgetEGE = averageScoreBudgetEGE,
//                growthDeclineAverageScoreBudgetEGE = growthDeclineAverageScoreBudgetEGE!!,
//                numbersBudgetStudents = numbersBudgetStudents,
//                numbersStudentWithoutExam = numbersStudentWithoutExam,
//                averageScoreEGEWithoutIndividualAchievements = averageScoreEGEWithoutIndividualAchievements,
//                dataSource = "HSE"
//            )
//        )
//    }
//}

//fun getPaidUniversityData(url: String, mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {
//
//    val file = File(url)
//    val doc: Document = Jsoup.parse(file, null)
//
//    //val doc = Jsoup.connect(url).get()
//    val row = doc.select("table#transparence_t > tbody > tr")
//
//    for (elem in row) {
//        val nameUniversity = elem.select("td")[0].text().replace('"', ' ')
//
//        val averageScorePaidEGE = elem.select("td")[1].text().toDouble()
//        //val growthDeclineAverageScorePaidEGE = elem.select("td")[2].text().toDouble()
//
//        val growthDeclineAverageScorePaidEGE: Double? = if (elem.select("td")[2].text().isNotEmpty()) {
//            elem.select("td")[2].text().toDouble()
//        } else {
//            0.0
//        }
//
//        val numbersPaidStudents = elem.select("td")[3].text().toInt()
//
//        mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
//            it.averageScorePaidEGE = averageScorePaidEGE
//            it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE!!
//            it.numbersPaidStudents = numbersPaidStudents
//        }
//    }
//}

fun scrapeUniversityYGSN(dataAboutUniversityYGSN: DataAboutUniversityYGSN) {

    val mutableListUniversityYGSNData: MutableList<UniversityYGSNData> = mutableListOf()

    for (elem in dataAboutUniversityYGSN.dataOfYear) {
        val year = elem.key
        val budgetURL = elem.value.first
        val paidURL = elem.value.second

        getBudgetUniversityYGSNData(budgetURL, mutableListUniversityYGSNData, year)

        getPaidUniversityYGSNData(paidURL, mutableListUniversityYGSNData, year)
    }

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (ygsn in mutableListUniversityYGSNData) {
                UniversityYGSN.insert {
                    it[yearOfData] = ygsn.yearOfData
                    it[universityName] = ygsn.universityName
                    it[ygsnName] = ygsn.ygsnName
                    it[averageScoreBudgetEGE] = ygsn.averageScoreBudgetEGE
                    it[averageScorePaidEGE] = ygsn.averageScorePaidEGE
                    it[numbersBudgetStudents] = ygsn.numbersBudgetStudents
                    it[numbersPaidStudents] = ygsn.numbersPaidStudents
//                    it[growthDeclineAverageScoreBudgetEGE] = ygsn.growthDeclineAverageScoreBudgetEGE
//                    it[growthDeclineAverageScorePaidEGE] = ygsn.growthDeclineAverageScorePaidEGE
//                    it[numbersStudentWithoutExam] = ygsn.numbersStudentWithoutExam
//                    it[averageScoreEGEWithoutIndividualAchievements] = ygsn.averageScoreEGEWithoutIndividualAchievements
//                    it[costEducation] = ygsn.costEducation
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun getPaidUniversityYGSNData(url: String, mutableListUniversityYGSNData: MutableList<UniversityYGSNData>, year: Int) {

    val file = File(url)
    val doc: Document = Jsoup.parse(file, null)

    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        try {
            val nameUniversity = elem.select("td")[1].text()

            val nameYGSN = elem.select("td")[0].text()
            val averageScorePaidEGE = elem.select("td")[2].text().toDouble()

//            val growthDeclineAverageScorePaidEGE: Double? = if (elem.select("td")[3].text().isNotEmpty()) {
//                elem.select("td")[3].text().toDouble()
//            } else {
//                null
//            }

            val numbersPaidStudents = elem.select("td")[4].text().toInt()

//            val costEducation: Double? = if (elem.select("td")[5].text() == "нет данных") {
//                null
//            } else {
//                elem.select("td")[5].text().toDouble()
//            }

//            val averageScoreEGEWithoutIndividualAchievements: Boolean = elem.select("td")[8].text() == "Да"

            mutableListUniversityYGSNData.find {
                it.ygsnName == nameYGSN && it.yearOfData == year &&
                        it.universityName == nameUniversity
            }?.let {

                it.averageScorePaidEGE = averageScorePaidEGE
                it.numbersPaidStudents = numbersPaidStudents
//                it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE
//                it.costEducation = costEducation
//                it.averageScoreEGEWithoutIndividualAchievements = averageScoreEGEWithoutIndividualAchievements
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getBudgetUniversityYGSNData(url: String, mutableListUniversityYGSNData: MutableList<UniversityYGSNData>, year: Int) {

    val file = File(url)
    val doc: Document = Jsoup.parse(file, null)

    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        try {
            val nameUniversity = elem.select("td")[1].text()

            val nameYGSN = elem.select("td")[0].text()

            val averageScoreBudgetEGE: Double? = if (elem.select("td")[2].text().isNotEmpty()) {
                elem.select("td")[2].text().toDouble()
            } else {
                null
            }

//            val growthDeclineAverageScoreBudgetEGE: Double? = if (elem.select("td")[3].text().isNotEmpty()) {
//                elem.select("td")[3].text().toDouble()
//            } else {
//                null
//            }

            val numbersBudgetStudents: Int? = if (elem.select("td")[4].text().isNotEmpty()) {
                elem.select("td")[4].text().toInt()
            } else {
                null
            }

//            val numbersStudentWithoutExam: Int? = if (elem.select("td")[5].text().isNotEmpty()) {
//                elem.select("td")[5].text().toInt()
//            } else {
//                null
//            }

            mutableListUniversityYGSNData.add(
                UniversityYGSNData(
                    yearOfData = year,
                    universityName = nameUniversity,
                    ygsnName = nameYGSN,
                    averageScoreBudgetEGE = averageScoreBudgetEGE,
                    numbersBudgetStudents = numbersBudgetStudents,
//                    growthDeclineAverageScoreBudgetEGE = growthDeclineAverageScoreBudgetEGE,
//                    numbersStudentWithoutExam = numbersStudentWithoutExam
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun insertUniversities(mutableListUniversitiesData: MutableList<UniversityData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (university in mutableListUniversitiesData) {
                University.insert {
                    it[yearOfData] = university.yearOfData
                    it[name] = university.name
                    it[region] = university.region
                    it[hostel] = university.hostel
                    it[averageAllStudentsEGE] = university.averageAllStudentsEGE
                    it[dolyaOfflineEducation] = university.dolyaOfflineEducation
                    it[averagedMinimalEGE] = university.averagedMinimalEGE
                    it[averageBudgetEGE] = university.averageBudgetEGE
                    it[countVserosBVI] = university.countVserosBVI
                    it[countOlimpBVI] = university.countOlimpBVI
                    it[countCelevoiPriem] = university.countCelevoiPriem
                    it[dolyaCelevoiPriem] = university.dolyaCelevoiPriem
                    it[ydelniyVesInostrancyWithoutSNG] = university.ydelniyVesInostrancyWithoutSNG
                    it[ydelniyVesInostrancySNG] = university.ydelniyVesInostrancySNG
                    it[averageBudgetWithoutSpecialRightsEGE] = university.averageBudgetWithoutSpecialRightsEGE
                    it[jsonYGSN] = university.jsonYGSN
                    it[dataSource] = university.dataSource
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun insertNameUniversitiesHSE(mutableListNameUniversitiesData: MutableList<NameUniversitiesData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (nameUniversitiesHSE in mutableListNameUniversitiesData) {
                NameUniversitiesHSE.insert {
                    it[name] = nameUniversitiesHSE.name
                    it[generalname] = nameUniversitiesHSE.generalname
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun insertNameUniversitiesMIREA(mutableListNameUniversitiesData: MutableList<NameUniversitiesData>) {
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (nameUniversitiesMIREA in mutableListNameUniversitiesData) {
                NameUniversitiesMIREA.insert {
                    it[name] = nameUniversitiesMIREA.name
                    it[generalname] = nameUniversitiesMIREA.generalname
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun setUniversityDataSource(): DataAboutUniversity {
    return DataAboutUniversity(
        mapOf(
            2020 to Pair("hse-universities/БюджетВУЗ2020.html", "hse-universities/ПлаткаВУЗ2020.html"),
            2019 to Pair("hse-universities/БюджетВУЗ2019.html", "hse-universities/ПлаткаВУЗ2019.html"),
            2018 to Pair("hse-universities/БюджетВУЗ2018.html", "hse-universities/ПлаткаВУЗ2018.html")
        ),

        mutableListOf(
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=1",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=2",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=4",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=3",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=25",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=5",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=6",
            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=7"
        )
    )
}

fun setUniversityYGSNDataSource(): DataAboutUniversityYGSN {
    return DataAboutUniversityYGSN(
        mapOf(
            2020 to Pair(
                "hse-ygsn/БюджетУГСН2020.html",
                "hse-ygsn/ПлаткаУГСН2020.html"
            ),

            2019 to Pair(
                "hse-ygsn/БюджетУГСН2019.html",
                "hse-ygsn/ПлаткаУГСН2019.html"
            ),

            2018 to Pair(
                "hse-ygsn/БюджетУГСН2018.html",
                "hse-ygsn/ПлаткаУГСН2018.html")
        )
    )
}
