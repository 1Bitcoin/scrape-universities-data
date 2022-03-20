import dao.Student
import datasource.setUniversityDataSource
import datasource.setUniversityYGSNDataSource
import dto.UniversityYGSNMIREAData
import dto.student.StudentData
import generation.student.Generator
import modeling.ModelingHelper
import org.jetbrains.database.insertNameUniversitiesMIREA
import org.jetbrains.database.insertUniversityYGSNMIREA
import org.jetbrains.exposed.sql.Database
import scrape.data.universities.mirea.scrapeUniversityMIREA
import java.sql.DriverManager


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres?loggerLevel=TRACE&loggerFile=pgjdbc.log&reWriteBatchedInserts=true",
        driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
    )

    //scrapeUniversityHSE(dataAboutUniversity)

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    val helper = ModelingHelper()

    helper.enrichDataSet()
    helper.prepareInformationUniversity()

    for (item in helper.informationUniversityList2020) {
        println(item.universityData)
        println(item.ygsnList)
    }


    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

    //val generator = Generator().generateStudent()

//    val test = mutableListOf<StudentData>()
//
//    for (i in 1..50000) {
//        test.add(StudentData())
//    }
//
//    println("list done")
//
//    val ans = batchInsertStudent(test)


//    for (an in ans) {
//        println(an[Student.id])
//    }
}


