import datasource.setUniversityDataSource
import datasource.setUniversityYGSNDataSource
import org.jetbrains.exposed.sql.Database
import parser.parseCSVFileWithDolyaYGSN
import scrape.data.universities.mirea.scrapeUniversityMIREA
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

    val parsedDolyaYGSNMutableMap = parseCSVFileWithDolyaYGSN()

    for (item in parsedDolyaYGSNMutableMap) {
        println(item)
    }

    scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)
}


