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


fun main(command: String, logToggle: Int) {
    Database.connect(
        "jdbc:postgresql://localhost:5432/postgres",
        driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
    )

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    when (command) {
        "modelling" -> startModeling(700000, 2019, logToggle)
        "generating" -> generateStudents()
    }

    //scrapeUniversityHSE(dataAboutUniversity)

    //startModeling(100, 2020)

    //generateStudents()

    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)
}

fun generateStudents() {
    val restTemplate = RestTemplate()

    val baseUrl = "http://localhost:8080/logs"
    val uri = URI(baseUrl)

    val generator = Generator()
    generator.generateStudent()

    val generateMessage = "Генерация агентов окончена"
    restTemplate.postForEntity(uri, ModellerLog(generateMessage), String::class.java)
}

fun startModeling(limitStudents: Int, modelingYear: Int, logToggle: Int) {
    val restTemplate = RestTemplate()

    val baseUrl = "http://localhost:8080/logs"
    val uri = URI(baseUrl)

    val bufferedWriter = File("D:\\logs.txt").bufferedWriter()

    val startMessage = "Получение информации о студентах и университетах"

    if (logToggle != 0)
        restTemplate.postForEntity(uri, ModellerLog(startMessage), String::class.java)

    val modeller = Modeller(limitStudent = limitStudents, modelingYear, bufferedWriter, logToggle)

    modeller.modeling()

    bufferedWriter.close()
}


