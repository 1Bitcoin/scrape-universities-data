package scrape.data.ygsn

import dao.UniversityYGSN
import datasource.vo.DataAboutUniversityYGSN
import dto.UniversityYGSNData
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

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

            val numbersPaidStudents = elem.select("td")[4].text().toInt()

            mutableListUniversityYGSNData.find {
                it.ygsnName == nameYGSN && it.yearOfData == year &&
                        it.universityName == nameUniversity
            }?.let {

                it.averageScorePaidEGE = averageScorePaidEGE
                it.numbersPaidStudents = numbersPaidStudents
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

            val numbersBudgetStudents: Int? = if (elem.select("td")[4].text().isNotEmpty()) {
                elem.select("td")[4].text().toInt()
            } else {
                null
            }

            mutableListUniversityYGSNData.add(
                UniversityYGSNData(
                    yearOfData = year,
                    universityName = nameUniversity,
                    ygsnName = nameYGSN,
                    averageScoreBudgetEGE = averageScoreBudgetEGE,
                    numbersBudgetStudents = numbersBudgetStudents,
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}