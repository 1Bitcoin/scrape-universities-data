package main.kotlin

import dto.contoller.Generating
import dto.contoller.Modelling
import main.kotlin.datasource.setUniversityDataSource
import main.kotlin.datasource.setUniversityYGSNDataSource
import main.kotlin.dto.ModellerLog
import main.kotlin.generation.student.Generator
import main.kotlin.modeling.Modeller
import main.kotlin.ru.batch.executor.MyQueryExecutor
import org.jetbrains.exposed.sql.Database
import org.postgresql.core.QueryExecutor
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.client.RestTemplate
import java.io.File
import java.net.URI


class Modeller() {

    val port = 8080
    val portDB = 5432

    val dataAboutUniversity = setUniversityDataSource()
    val dataAboutUniversityYGSN = setUniversityYGSNDataSource()

    //scrapeUniversityHSE(dataAboutUniversity)

    //startModeling(100, 2020)

    //generateStudents()

    //scrapeUniversityMIREA(dataAboutUniversity)

    //scrapeUniversityYGSN(dataAboutUniversityYGSN)

    fun generateStudents(generatingDTO: Generating) {
        Database.connect(
            "jdbc:postgresql://db:$portDB/postgres",
            driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
        )

        val restTemplate = RestTemplate()

        val baseUrl = "http://localhost:$port/logs"
        val uri = URI(baseUrl)

        val generator = Generator(generatingDTO, port)
        generator.generateStudent()

        val generateMessage = "Генерация агентов окончена"
        restTemplate.postForEntity(uri, ModellerLog(generateMessage), String::class.java)
    }

    fun startModeling(modellerDTO: Modelling, logToggle: Int) {
        Database.connect(
            "jdbc:postgresql://db:$portDB/postgres",
            driver = "org.postgresql.Driver", user = "postgres", password = "qwerty"
        )

        val restTemplate = RestTemplate()

        val baseUrl = "http://localhost:$port/logs"
        val uri = URI(baseUrl)

        val bufferedWriter = File("D:\\logs.txt").bufferedWriter()

        val startMessage = "Получение информации о абитуриентах и университетах"

        if (logToggle != 0)
            restTemplate.postForEntity(uri, ModellerLog(startMessage), String::class.java)

        val modeller = Modeller(modellerDTO, bufferedWriter, logToggle, port)

        modeller.modeling()

        bufferedWriter.close()
    }

    fun deleteStudents() {
        val restTemplate = RestTemplate()

        val baseUrl = "http://localhost:$port/logs"
        val uri = URI(baseUrl)

        val generateMessage = "Удаление агентов началось"
        restTemplate.postForEntity(uri, ModellerLog(generateMessage), String::class.java)

        val executor = MyQueryExecutor()
        executor.deleteStudents()

        val generateMessage1 = "Агенты были удалены"
        restTemplate.postForEntity(uri, ModellerLog(generateMessage1), String::class.java)
    }
}


