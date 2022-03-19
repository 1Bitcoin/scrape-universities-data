import datasource.setUniversityDataSource
import datasource.setUniversityYGSNDataSource
import dto.UniversityYGSNMIREAData
import generation.student.Generator
import org.jetbrains.database.insertNameUniversitiesMIREA
import org.jetbrains.database.insertUniversityYGSNMIREA
import org.jetbrains.exposed.sql.Database
import scrape.data.universities.mirea.scrapeUniversityMIREA


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres", driver = "org.postgresql.Driver",
        user = "postgres", password = "qwerty"
    )

    //scrapeUniversityHSE(dataAboutUniversity)

//    val dataAboutUniversity = setUniversityDataSource()
//    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()
//
//
//    scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

    val generator = Generator().generateStudent()
}


