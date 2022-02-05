package scrape.data.universities.mirea

import datasource.vo.DataAboutUniversity
import dto.UniversityData
import org.jetbrains.database.insertUniversities
import org.jsoup.Jsoup

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

                            //    val testJson = "{ Музыкальное искусство: { contingentStudents: 302.0, dolyaContingenta: 100.0 } }"
                            //    val answer = JSONObject("""{"name":"test name", "age":25}""")

                            //    val json = "{ [\"Музыкальное искусство\": [ { \"contingentStudents\": 302.0 }, {\"dolyaContingenta\": 100.0 } ] ] }"
                            //    val jsonObject = JsonParser().parse(json).asJsonArray

                            //    val testJson = "[{ \"ygsnName\": \"Музыкальное искусство\", \"contingentStudents\": \"302.0\", \"dolyaContingenta\": \"100.0\"} ]"
                            //
                            //    val jsonArray= JsonParser().parse(testJson).asJsonArray
                            //
                            //    for (jsonObject in jsonArray) {
                            //        val a = jsonObject.asJsonObject.get("ygsnName").toString()
                            //        println(a.replace("\"", "").equals("Музыкальное искусство"))
                            //    }

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