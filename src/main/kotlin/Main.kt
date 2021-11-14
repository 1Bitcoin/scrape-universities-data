import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty")

//
    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    //val setYGSN = scrapeYGSN(dataAboutUniversity.universitiesNameForScrape, dataAboutUniversityYGSN)

    //scrapeUniversityHSE(dataAboutUniversity)
    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN, dataAboutUniversity.universitiesNameForScrape, getAllYGSN())
}

fun mySelect(mutableListUniversitiesData: MutableList<UniversityData>) {
    transaction {
        addLogger(StdOutSqlLogger)

        val universities = University.select { (University.dataSource eq "MIREA") and (University.yearOfData eq 2020) }

        for (i in universities){
            mutableListUniversitiesData.add(UniversityData(name = i[University.name], yearOfData = i[University.yearOfData]))
        }
    }
}

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
    val matchedNames = findGeneralNameUniversities(mutableListUniversitiesData)

    // Сохраняем полученные сопоставления
    insertNameUniversitiesMIREA(matchedNames)
}

fun scrapeUniversityHSE(dataAboutUniversity: DataAboutUniversity) {
    val mutableListUniversitiesData: MutableList<UniversityData> = mutableListOf()

    for (elem in dataAboutUniversity.dataOfYear) {
        val year = elem.key
        val budgetURL = elem.value.first
        val paidURL = elem.value.second

        getBudgetUniversityData(budgetURL, mutableListUniversitiesData, year)
        getPaidUniversityData(paidURL, mutableListUniversitiesData, year)

    }
    // Сохранили
    insertUniversities(mutableListUniversitiesData)

    // Ищем и сопоставляем текущие названия с найденными в интернете
    val matchedNames = findGeneralNameUniversities(mutableListUniversitiesData)

    // Сохраняем полученные сопоставления
    insertNameUniversitiesHSE(matchedNames)
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

fun getPersonalityUniversityData(monitoring: MutableList<String>,
                                 mutableListUniversitiesData: MutableList<UniversityData>) {

    for (district in monitoring) {
        val districtPage = Jsoup.connect(district).get()
        val universitiesURL = districtPage.select("table[class=an] > tbody")

        for (item in universitiesURL.select("tr")) {
            val id = item.select("td")[1].select("a").attr("href")
            var url = "https://monitoring.miccedu.ru/iam/2021/_vpo/$id"

            val universityPage = Jsoup.connect(url).get()
            val nameUniversity = universityPage.select("table#info > tbody > tr > td")[1]
                .text()
                .replace('"', ' ')

            val table = universityPage.select("table#result > tbody")

            // Если таблица не пустая
            if (table.size > 0) {
                // Идем по годам и сохраняем инфу
                for (year in 2..5) {
                    val university = UniversityData(name = nameUniversity, yearOfData = year + 2016)

                    for (i in 1..5) {
                        val characteristic = table.select("tr")[i].select("td")[year]
                            .text()
                            .substringBefore('|')
                            .replace(" ", "")
                            .replace("—", "-1")
                            .replace(",", ".").toDouble()

                        when (i) {
                            1 -> university.researchActivities = characteristic
                            2 -> university.internationalActivity = characteristic
                            3 -> university.financialAndEconomicActivities = characteristic
                            4 -> university.salaryPPP = characteristic
                            5 -> university.additionalIndicator = characteristic
                        }
                    }

                    university.dataSource = "MIREA"
                    mutableListUniversitiesData.add(university)
                    println(university)
                }
            }
        }
    }
}

fun getBudgetUniversityData(url: String, mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {

    val file = File(url)
    val doc: Document = Jsoup.parse(file, null)

    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[0].text().replace('"', ' ')
        val averageScoreBudgetEGE = elem.select("td")[1].text().toDouble()
        val growthDeclineAverageScoreBudgetEGE: Double? = if (elem.select("td")[2].text().isNotEmpty()) {
            elem.select("td")[2].text().toDouble()
        } else {
            0.0
        }

        val numbersBudgetStudents = elem.select("td")[3].text().toInt()
        val numbersStudentWithoutExam = elem.select("td")[4].text().toInt()
        val averageScoreEGEWithoutIndividualAchievements: Boolean = elem.select("td")[5].text() != "Да"

        mutableListUniversitiesData.add(UniversityData(name = nameUniversity, yearOfData = year,
            averageScoreBudgetEGE = averageScoreBudgetEGE, growthDeclineAverageScoreBudgetEGE = growthDeclineAverageScoreBudgetEGE!!,
            numbersBudgetStudents = numbersBudgetStudents, numbersStudentWithoutExam = numbersStudentWithoutExam,
            averageScoreEGEWithoutIndividualAchievements = averageScoreEGEWithoutIndividualAchievements,
            dataSource = "HSE"))
    }
}

fun getPaidUniversityData(url: String, mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {

    val file = File(url)
    val doc: Document = Jsoup.parse(file, null)

    //val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[0].text().replace('"', ' ')

        val averageScorePaidEGE = elem.select("td")[1].text().toDouble()
        //val growthDeclineAverageScorePaidEGE = elem.select("td")[2].text().toDouble()

        val growthDeclineAverageScorePaidEGE: Double? = if (elem.select("td")[2].text().isNotEmpty()) {
            elem.select("td")[2].text().toDouble()
        } else {
            0.0
        }

        val numbersPaidStudents = elem.select("td")[3].text().toInt()

        mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
            it.averageScorePaidEGE = averageScorePaidEGE
            it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE!!
            it.numbersPaidStudents = numbersPaidStudents
        }
    }
}

fun scrapeYGSN(universitiesNameToFind: MutableList<String>,
               dataAboutUniversityYGSN: DataAboutUniversityYGSN): Set<String> {

    var listOfSet = mutableListOf<Set<String>>()

    for (elem in dataAboutUniversityYGSN.dataOfYear) {
        val url = elem.value.first
        val set = mutableSetOf<String>()

        val doc = Jsoup.connect(url).get()
        val row = doc.select("table#transparence_t > tbody > tr")

        for (elem in row) {
            val speciality = elem.select("td")[0].text()
            val currentUniversityName = elem.select("td")[1].text()

            if (currentUniversityName in universitiesNameToFind)
                set.add(speciality.toString())
        }

        listOfSet.add(set)
    }

    // Хард код для 3 множеств (3 года - 3 страницы анализа - 3 множества)
    val result = listOfSet[0].intersect(listOfSet[1]).intersect(listOfSet[2])

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (speciality in result) {
                ygsn.insert {
                    it[name] = speciality
                }
            }
        }

    } catch (exception: Exception) {
        println(exception.message)
    }

    return result
}

fun scrapeUniversityYGSN(dataAboutUniversityYGSN: DataAboutUniversityYGSN,
                         universitiesNameForScrape: MutableList<String>, setYGSN: Set<String>) {

    val mutableListUniversityYGSNData: MutableList<UniversityYGSNData> = mutableListOf()

//    for (nameYGSN in setYGSN) {
//        for (elem in dataAboutUniversityYGSN.dataOfYear) {
//            mutableListUniversityYGSNData.add(UniversityYGSNData(ygsnName = nameYGSN, yearOfData = elem.key))
//        }
//    }

    for (i in mutableListUniversityYGSNData)
        println("$i")

    for (elem in dataAboutUniversityYGSN.dataOfYear) {
        val year = elem.key
        val budgetURL = elem.value.first
        val paidURL = elem.value.second

        getBudgetUniversityYGSNData(budgetURL, universitiesNameForScrape,
            mutableListUniversityYGSNData, year)

        getPaidUniversityYGSNData(paidURL, universitiesNameForScrape,
            mutableListUniversityYGSNData, year)
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
                    it[growthDeclineAverageScoreBudgetEGE] = ygsn.growthDeclineAverageScoreBudgetEGE
                    it[growthDeclineAverageScorePaidEGE] = ygsn.growthDeclineAverageScorePaidEGE
                    it[numbersBudgetStudents] = ygsn.numbersBudgetStudents
                    it[numbersPaidStudents] = ygsn.numbersPaidStudents
                    it[numbersStudentWithoutExam] = ygsn.numbersStudentWithoutExam
                    it[averageScoreEGEWithoutIndividualAchievements] = ygsn.averageScoreEGEWithoutIndividualAchievements
                    it[costEducation] = ygsn.costEducation
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun getPaidUniversityYGSNData(url: String, universitiesNameForScrape: MutableList<String>,
                              mutableListUniversityYGSNData: MutableList<UniversityYGSNData>, year: Int) {

    val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[1].text()
        if (nameUniversity in universitiesNameForScrape) {

            val nameYGSN = elem.select("td")[0].text()
            val averageScorePaidEGE = elem.select("td")[2].text().toDouble()

            val growthDeclineAverageScorePaidEGE: Double? = if (elem.select("td")[3].text().isNotEmpty()) {
                elem.select("td")[3].text().toDouble()
            } else {
                null
            }

            val numbersPaidStudents = elem.select("td")[4].text().toInt()
            val costEducation: Double? = if (elem.select("td")[5].text() == "нет данных") {
                null
            } else {
                elem.select("td")[5].text().toDouble()
            }

            val averageScoreEGEWithoutIndividualAchievements: Boolean = elem.select("td")[8].text() == "Да"

            mutableListUniversityYGSNData.find { it.ygsnName == nameYGSN && it.yearOfData == year &&
                    it.universityName == nameUniversity }?.let {

                it.averageScorePaidEGE = averageScorePaidEGE
                it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE
                it.numbersPaidStudents = numbersPaidStudents
                it.costEducation = costEducation
                it.averageScoreEGEWithoutIndividualAchievements = averageScoreEGEWithoutIndividualAchievements
            }
        }
    }
}

fun getBudgetUniversityYGSNData(url: String, universitiesNameForScrape: MutableList<String>,
                                mutableListUniversityYGSNData: MutableList<UniversityYGSNData>, year: Int) {

    val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[1].text()
        if (nameUniversity in universitiesNameForScrape) {

            val nameYGSN = elem.select("td")[0].text()
            val averageScoreBudgetEGE = elem.select("td")[2].text().toDouble()

            val growthDeclineAverageScoreBudgetEGE: Double? = if (elem.select("td")[3].text().isNotEmpty()) {
                elem.select("td")[3].text().toDouble()
            } else {
                null
            }

            val numbersBudgetStudents = elem.select("td")[4].text().toInt()
            val numbersStudentWithoutExam = elem.select("td")[5].text().toInt()

            mutableListUniversityYGSNData.add(
                UniversityYGSNData(
                    yearOfData = year, universityName = nameUniversity,
                    ygsnName = nameYGSN, averageScoreBudgetEGE = averageScoreBudgetEGE,
                    growthDeclineAverageScoreBudgetEGE = growthDeclineAverageScoreBudgetEGE,
                    numbersBudgetStudents = numbersBudgetStudents,
                    numbersStudentWithoutExam = numbersStudentWithoutExam)
            )
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
                    it[averageScoreBudgetEGE] = university.averageScoreBudgetEGE
                    it[averageScorePaidEGE] = university.averageScorePaidEGE
                    it[growthDeclineAverageScoreBudgetEGE] = university.growthDeclineAverageScoreBudgetEGE
                    it[growthDeclineAverageScorePaidEGE] = university.growthDeclineAverageScorePaidEGE
                    it[numbersBudgetStudents] = university.numbersBudgetStudents
                    it[numbersPaidStudents] = university.numbersPaidStudents
                    it[numbersStudentWithoutExam] = university.numbersStudentWithoutExam
                    it[averageScoreEGEWithoutIndividualAchievements] = university.averageScoreEGEWithoutIndividualAchievements
                    it[researchActivities] = university.researchActivities
                    it[internationalActivity] = university.internationalActivity
                    it[financialAndEconomicActivities] = university.financialAndEconomicActivities
                    it[salaryPPP] = university.salaryPPP
                    it[additionalIndicator] = university.additionalIndicator
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
            2020 to Pair("БюджетВУЗ2020.html", "ПлаткаВУЗ2020.html"),
            2019 to Pair("БюджетВУЗ2019.html", "ПлаткаВУЗ2019.html"),
            2018 to Pair("БюджетВУЗ2018.html", "ПлаткаВУЗ2018.html")
        ),

        mutableListOf(
            //"https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=1",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=2",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=4",
            //"https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=3",
            //"https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=25",
            //"https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=5",
            //"https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=6",
//            "https://monitoring.miccedu.ru/iam/2021/_vpo/material.php?type=1&id=7"
        )
    )
}

fun setUniversityYGSNDataSource(): DataAboutUniversityYGSN {
    return DataAboutUniversityYGSN(
        mapOf(
            2020 to Pair(
                "https://ege.hse.ru/rating/2020/84025342/all/",
                "https://ege.hse.ru/rating/2020/84025368/all/"
            ),

            2019 to Pair(
                "https://ege.hse.ru/rating/2019/81058583/all/?rlist=&ptype=0&glist=0&vuz-abiturients-budget-order=ge&vuz-abiturients-budget-val=10",
                "https://ege.hse.ru/rating/2019/81058609/all/?rlist=&uplist=&glist=0&vuz-abiturients-paid-order=ge&vuz-abiturients-paid-val=10&price-order=ge&price-val="
            ),

            2018 to Pair("https://ege.hse.ru/rating/2018/75767645/all/", "https://ege.hse.ru/rating/2018/77479782/all/")
        )
    )
}
