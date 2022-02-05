package scrape.data.universities.hse

import datasource.vo.DataAboutUniversity
import dto.UniversityData
import org.jetbrains.database.insertNameUniversitiesHSE
import org.jetbrains.database.insertUniversities
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import scrape.data.universities.general.name.findGeneralNameUniversities
import java.io.File

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

//        mutableListUniversitiesData.add(
//            dto.UniversityData(
//                name = nameUniversity,
//                yearOfData = year,
//                dataSource = "HSE"
//            )
//        )
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

//        mutableListUniversitiesData.find { it.name == nameUniversity && it.yearOfData == year }?.let {
//            it.averageScorePaidEGE = averageScorePaidEGE
//            it.growthDeclineAverageScorePaidEGE = growthDeclineAverageScorePaidEGE!!
//            it.numbersPaidStudents = numbersPaidStudents
//        }
    }
}