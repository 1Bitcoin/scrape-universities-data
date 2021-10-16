import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup

fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/scrape", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty")

    val universitiesNameForScrape= mutableListOf("Моск. гос. техн. ун-т. им. Н.Э. Баумана",
        "Национальный исследовательский ун-т. \"Высшая школа экономики\", г. Москва",
        "Моск. физико-техн. ин-т.", "Национальный исследовательский ядерный ун-т. \"МИФИ\", г. Москва",
        "Моск. гос. ун-т. им. М.В. Ломоносова")

    var dataAboutUniversity = DataAboutUniversity(
        mapOf(
            2020 to Pair("https://ege.hse.ru/rating/2020/84025292/all/", "https://ege.hse.ru/rating/2020/84025315/all/"),

            2019 to Pair("https://ege.hse.ru/rating/2019/81031971/all/?rlist=&ptype=0&vuz-abiturients-budget-order=ge&vuz-abiturients-budget-val=10",
            "https://ege.hse.ru/rating/2019/81050684/all/?rlist=&ptype=0&vuz-abiturients-paid-order=ge&vuz-abiturients-paid-val=10"),

            2018 to Pair("https://ege.hse.ru/rating/2018/75767740/all/", "https://ege.hse.ru/rating/2018/77479751/all/")),

        mutableListOf(
            Pair("Моск. гос. техн. ун-т. им. Н.Э. Баумана", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=147"),
            Pair("Национальный исследовательский ун-т. \"Высшая школа экономики\", г. Москва", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=1766"),
            Pair("Моск. физико-техн. ин-т.", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=161"),
            Pair("Национальный исследовательский ядерный ун-т. \"МИФИ\", г. Москва", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=165"),
            Pair("Моск. гос. ун-т. им. М.В. Ломоносова", "https://monitoring.miccedu.ru/iam/2021/_vpo/inst.php?id=1725")
        ),
            universitiesNameForScrape)

    //scrapeYGSN(universitiesNameForScrape)

    scrapeUniversity(dataAboutUniversity)

}

fun scrapeUniversity(dataAboutUniversity: DataAboutUniversity) {
//    for (elem in dataAboutUniversity.dataOfYear) {
//        var year = elem.key
//        var budgetURL = elem.value.first
//        var paidURL = elem.value.second
//
//        val doc = Jsoup.connect(url).get()
//        val row = doc.select("table#transparence_t > tbody > tr")
//    }

    val url = dataAboutUniversity.dataOfYear[2020]?.first

    val doc = Jsoup.connect(url!!).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val speciality = elem.select("td")[0].text()
        val currentUniversityName = elem.select("td")[1].text()

        if (elem.select("td")[0].text() in dataAboutUniversity.universitiesNameForScrape) {
            println("name: ${elem.select("td")[0].text()}")
            println("averageScoreBudgetEGE: ${elem.select("td")[1].text().toDouble()}")
            println("growthDeclineAverageScoreBudgetEGE: ${elem.select("td")[2].text().toDouble()}")
            println("numbersBudgetStudents: ${elem.select("td")[3].text().toInt()}")
            println("numbersStudentWithoutExam: ${elem.select("td")[4].text().toInt()}")

            if (elem.select("td")[5].text() == "Да") {
                println("averageScoreEGEWithoutIndividualAchievements: ${false}\n")
            } else {
                println("averageScoreEGEWithoutIndividualAchievements: ${true}\n")
            }

            try {
                transaction {
                    addLogger(StdOutSqlLogger)

                    University.insert {
                        it[name] = speciality
                    }
                }
            } catch (exception: Exception) {
                println(exception.message)
            }
        }
    }
}

fun scrapeYGSN(universitiesNameToFind: MutableList<String>) {
    val url = "https://ege.hse.ru/rating/2020/84025342/all/"
    var set = mutableSetOf<String>()

    val doc = Jsoup.connect(url).get()
    val row = doc.select("table#transparence_t > tbody > tr")

    for (elem in row) {
        val speciality = elem.select("td")[0].text()
        val currentUniversityName = elem.select("td")[1].text()

        if (currentUniversityName in universitiesNameToFind)
            set.add(speciality.toString())
    }

    try {
        transaction {
            addLogger(StdOutSqlLogger)

            for (speciality in set) {
                ygsn.insert {
                    it[name] = speciality
                }
            }
        }

    } catch (exception: Exception) {
        println(exception.message)
    }
}
