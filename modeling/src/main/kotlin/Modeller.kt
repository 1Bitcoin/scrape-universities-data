package main.kotlin

import main.kotlin.datasource.setUniversityDataSource
import main.kotlin.datasource.setUniversityYGSNDataSource
import main.kotlin.dto.ModellerLog
import main.kotlin.generation.student.Generator
import main.kotlin.modeling.Modeller
import org.jetbrains.exposed.sql.Database
import org.springframework.web.client.RestTemplate
import java.io.File
import java.net.URI


fun main() {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
    )

    //scrapeUniversityHSE(dataAboutUniversity)

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    startModeling(100, 2020)

    //generateStudents()

    //val generator = Generator().generateStudent()

//    val helper = ModelingHelper()

//    helper.enrichStudentDataSet(limit = false)
//
//    for (item in helper.informationStudent) {
//        println(item.studentData)
//        println(item.ygsnList)
//        for (egeItem in item.egeList)
//            println("egeId: ${egeItem.egeId} score: ${egeItem.score}")
//        println()
//    }

//    val generator = Generator()
//    generator.generateStudent()

//    val executor = MyQueryExecutor()
//    executor.selectFullInformationStudent()

//    helper.enrichUniversityDataSet()
//
//    for (item in helper.informationUniversityMap2020) {
//        println("Регион: ${item.key}")
//        for (university in item.value)
//            println("Вузы: ${university.universityData}")
//        println()
//    }

    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

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

fun generateStudents() {
    val generator = Generator()
    generator.generateStudent()
}

fun startModeling(limitStudents: Int, modelingYear: Int) {
    val restTemplate = RestTemplate()

    val baseUrl = "http://localhost:8081/logs"
    val uri = URI(baseUrl)

    val bufferedWriter = File("D:\\logs.txt").bufferedWriter()

    val startMessage = "Получение информации о студентах и университетах"
    restTemplate.postForEntity(uri, ModellerLog(startMessage), String::class.java)

    val modeller = Modeller(limitStudent = limitStudents, modelingYear, bufferedWriter)

    modeller.modeling()

    bufferedWriter.close()
}


