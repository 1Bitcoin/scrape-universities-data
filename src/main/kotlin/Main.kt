import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup

fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/scrape", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty")


    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    //val setYGSN = scrapeYGSN(dataAboutUniversity.universitiesNameForScrape, dataAboutUniversityYGSN)
    //scrapeUniversity(dataAboutUniversity)

    scrapeUniversityYGSN(dataAboutUniversityYGSN, dataAboutUniversity.universitiesNameForScrape, getAllYGSN())
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

fun scrapeUniversity(dataAboutUniversity: DataAboutUniversity) {
    val mutableListUniversitiesData: MutableList<UniversityData> = mutableListOf()

    for (universityName in dataAboutUniversity.universitiesNameForScrape) {
        for (elem in dataAboutUniversity.dataOfYear) {
            mutableListUniversitiesData.add(UniversityData(name = universityName, yearOfData = elem.key))
        }
    }

    for (elem in dataAboutUniversity.dataOfYear) {
        val year = elem.key
        val budgetURL = elem.value.first
        val paidURL = elem.value.second

        getBudgetUniversityData(budgetURL, dataAboutUniversity.universitiesNameForScrape,
            mutableListUniversitiesData, year)

        getPaidUniversityData(paidURL, dataAboutUniversity.universitiesNameForScrape,
            mutableListUniversitiesData, year)

    }

    getPersonalityUniversityData(dataAboutUniversity.monitoring, mutableListUniversitiesData)

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (university in mutableListUniversitiesData) {
                University.insert {
                    it[name] = university.name
                    it[yearOfData] = university.yearOfData
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
                }
            }
        }
    } catch (exception: Exception) {
        println(exception.message)
    }
}

fun getPersonalityUniversityData(monitoring: MutableList<Pair<String, String>>,
                                 mutableListUniversitiesData: MutableList<UniversityData>) {

    for (pair in monitoring) {
        val doc = Jsoup.connect(pair.second).get()
        val row = doc.select("table#result > tbody > tr").drop(1)

        for ((namingFlag, elem) in row.withIndex()) {
            val state2018 = elem.select("td")[2].text().replace(" ", "")
                .replace(",", ".").toDouble()

            val state2019 = elem.select("td")[3].text().replace(" ", "")
                .replace(",", ".").toDouble()

            val state2020 = elem.select("td")[4].text().replace(" ", "")
                .replace(",", ".").toDouble()

            setStateUniversity(listOf(state2018, state2019, state2020), mutableListUniversitiesData, pair.first,
                namingFlag)
        }
    }
}

fun setStateUniversity(stateList: List<Double>, mutableListUniversitiesData: MutableList<UniversityData>,
                       nameUniversity: String, namingFlag: Int) {

    for ((count, year) in listOf(2018, 2019, 2020).withIndex()) {
        mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
            when (namingFlag) {
                0 -> it.researchActivities = stateList[count]
                1 -> it.internationalActivity = stateList[count]
                2 -> it.financialAndEconomicActivities = stateList[count]
                3 -> it.salaryPPP = stateList[count]
                4 -> it.additionalIndicator = stateList[count]
            }
        }
    }
}

fun getBudgetUniversityData(url: String, universitiesNameForScrape: MutableList<String>,
                    mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {

    val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[0].text()
        if (nameUniversity in universitiesNameForScrape) {
            val averageScoreBudgetEGE = elem.select("td")[1].text().toDouble()
            val growthDeclineAverageScoreBudgetEGE = elem.select("td")[2].text().toDouble()
            val numbersBudgetStudents = elem.select("td")[3].text().toInt()
            val numbersStudentWithoutExam = elem.select("td")[4].text().toInt()
            val averageScoreEGEWithoutIndividualAchievements: Boolean = elem.select("td")[5].text() != "Да"

            mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
                it.averageScoreBudgetEGE = averageScoreBudgetEGE
                it.growthDeclineAverageScoreBudgetEGE = growthDeclineAverageScoreBudgetEGE
                it.numbersBudgetStudents = numbersBudgetStudents
                it.numbersStudentWithoutExam = numbersStudentWithoutExam
                it.averageScoreEGEWithoutIndividualAchievements = averageScoreEGEWithoutIndividualAchievements
            }
        }
    }
}

fun getPaidUniversityData(url: String, universitiesNameForScrape: MutableList<String>,
                            mutableListUniversitiesData: MutableList<UniversityData>, year: Int) {

    val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val nameUniversity = elem.select("td")[0].text()
        if (nameUniversity in universitiesNameForScrape) {
            val averageScorePaidEGE = elem.select("td")[1].text().toDouble()
            val growthDeclineAverageScorePaidEGE = elem.select("td")[2].text().toDouble()
            val numbersPaidStudents = elem.select("td")[3].text().toInt()

            mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
                it.averageScorePaidEGE = averageScorePaidEGE
                it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE
                it.numbersPaidStudents = numbersPaidStudents
            }
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

fun setUniversityDataSource(): DataAboutUniversity {
    return DataAboutUniversity(
        mapOf(
            2020 to Pair(
                "https://ege.hse.ru/rating/2020/84025292/all/",
                "https://ege.hse.ru/rating/2020/84025315/all/"
            ),

            2019 to Pair(
                "https://ege.hse.ru/rating/2019/81031971/all/?rlist=&ptype=0&vuz-abiturients-budget-order=ge&vuz-abiturients-budget-val=10",
                "https://ege.hse.ru/rating/2019/81050684/all/?rlist=&ptype=0&vuz-abiturients-paid-order=ge&vuz-abiturients-paid-val=10"
            ),

            2018 to Pair("https://ege.hse.ru/rating/2018/75767740/all/", "https://ege.hse.ru/rating/2018/77479751/all/")
        ),

        mutableListOf(
            Pair(
                "Моск. гос. техн. ун-т. им. Н.Э. Баумана",
                "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=147"
            ),
            Pair(
                "Национальный исследовательский ун-т. \"Высшая школа экономики\", г. Москва",
                "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=1766"
            ),
            Pair("Моск. физико-техн. ин-т.", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=161"),
            Pair(
                "Национальный исследовательский ядерный ун-т. \"МИФИ\", г. Москва",
                "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=165"
            ),
            Pair("Моск. гос. ун-т. им. М.В. Ломоносова", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=1725")
        ),

        mutableListOf(
            "Моск. гос. техн. ун-т. им. Н.Э. Баумана",
            "Национальный исследовательский ун-т. \"Высшая школа экономики\", г. Москва",
            "Моск. физико-техн. ин-т.", "Национальный исследовательский ядерный ун-т. \"МИФИ\", г. Москва",
            "Моск. гос. ун-т. им. М.В. Ломоносова"
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
