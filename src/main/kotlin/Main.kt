import datasource.setUniversityDataSource
import datasource.setUniversityYGSNDataSource
import org.jetbrains.exposed.sql.Database
import scrape.data.universities.mirea.scrapeUniversityMIREA


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty"
    )

    //scrapeUniversityHSE(dataAboutUniversity)

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

//    for (jsonObject in jsonArray) {
//        val a = jsonObject.asJsonObject.get("ygsnName").toString()
//        println(a.replace("\"", "").equals("Музыкальное искусство"))
//    }
}


