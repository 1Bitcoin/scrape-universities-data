package main.kotlin.scrape.data.universities.general.name

import main.kotlin.dto.NameUniversitiesData
import main.kotlin.dto.UniversityData
import org.jsoup.Jsoup

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