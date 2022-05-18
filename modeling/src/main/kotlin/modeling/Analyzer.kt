package main.kotlin.modeling

import main.kotlin.dto.ModellerLog
import main.kotlin.modeling.dto.InformationUniversity
import main.kotlin.modeling.dto.State
import main.kotlin.modeling.dto.result.UniversityTotalResult
import main.kotlin.modeling.dto.result.YGSNTotalResult
import main.kotlin.ru.batch.executor.MyQueryExecutor
import org.springframework.web.client.RestTemplate
import java.io.BufferedWriter
import java.net.URI
import kotlin.math.round

class Analyzer(year: Int) {
    val restTemplate = RestTemplate()
    val baseUrl = "http://localhost:8080/logs"
    val uri = URI(baseUrl)

    var yearAnalyze = year

    val totalResultModelling = mutableListOf<UniversityTotalResult>()

    private var executor: MyQueryExecutor = MyQueryExecutor()

    var statisticsCountStudent = 0

    fun analyzeResults(universities: LinkedHashMap<Int, InformationUniversity>, writer: BufferedWriter) {
        for (university in universities.values) {
            val universityId = university.universityData.universityId

            var minScoreOfUniversity = 100.0
            var maxScoreOfUniversity = 0.0

            var averageScoreUniversity = 0.0
            var countStudents = 0

            val resultYGSNList = mutableListOf<YGSNTotalResult>()

            val informationYGSNCollection = university.getInformationYGSNMap().values

            for (informationYGSN in informationYGSNCollection) {

                val ygsnId = informationYGSN.ygsnData.ygsnId
                var minScoreYGSN = 0.0
                var maxScoreYGSN = 0.0
                var averageScore = 0.0

                // Зачисляем студентов на свободные бюджетные места

                // Конкурсный список данного УГСН
                val competitiveList = informationYGSN.competitiveList

                // Если на УГСН были заявления
                if (competitiveList.isNotEmpty()) {
                    val budgetNumber = informationYGSN.ygsnData.numbersBudgetStudents

                    val resultList = competitiveList
                        .filter { it.state == State.ORIGINAL }
                        .takeLast(budgetNumber)

                    // Если есть кого зачислять (есть оригиналы заявлений)
                    if (resultList.isNotEmpty()) {
                        // Число зачисленных студентов на данный УГСН
                        val totalSize = resultList.size

                        // Считаем число студентов по всему универу для расчета среднего балла
                        countStudents += totalSize

                        // Уменьшаем кол-во доступных бюджетных мест
                        informationYGSN.ygsnData.numbersBudgetStudents - totalSize

                        // Очищаем конкурсный список
                        informationYGSN.competitiveList = mutableListOf()

                        // Считаем средний балл по УГСН и заодно считаем общее кол-во студентов, зачисленных в универ
                        resultList.forEach { averageScore += it.score; averageScoreUniversity += it.score }
                        averageScore /= totalSize
                        averageScore = round(averageScore)

                        minScoreYGSN = resultList.first().score
                        maxScoreYGSN = resultList.last().score

                        // Обновляем статистику по универу
                        if (minScoreOfUniversity > minScoreYGSN) {
                            minScoreOfUniversity = minScoreYGSN
                        }

                        if (maxScoreOfUniversity < maxScoreYGSN) {
                            maxScoreOfUniversity = maxScoreYGSN
                        }

                        minScoreYGSN = round((minScoreYGSN))
                        maxScoreYGSN = round((maxScoreYGSN))

                        resultYGSNList.add(YGSNTotalResult(ygsnId, averageScore, minScoreYGSN, maxScoreYGSN, totalSize))
                    }
                }
            }

            // Считаем средний балл по универу
            averageScoreUniversity /= countStudents
            averageScoreUniversity = round(averageScoreUniversity)

            totalResultModelling.add(
                UniversityTotalResult(universityId, averageScoreUniversity, minScoreOfUniversity,
                maxScoreOfUniversity, resultYGSNList, countStudents)
            )

            statisticsCountStudent += countStudents
        }

        for (resultUniversity in totalResultModelling) {
            val universityId = resultUniversity.universityId

            val universityInformation = universities[universityId]!!
            val universityName = universityInformation.universityData.name
            val countStudents = resultUniversity.countStudents
            val prevAverageAllStudentsEGEUniversity = universityInformation.universityData.averageAllStudentsEGE

            if (countStudents != 0) {
                val messageUniversity = "ID университета: ${resultUniversity.universityId} " +
                        "| Название: $universityName " +
                        "| Полученный средний балл: ${resultUniversity.averageAllBudgetScoreUniversity} " +
                        "| Прошлый средний балл: $prevAverageAllStudentsEGEUniversity " +
                        "| Изменение среднего балла: ${resultUniversity.averageAllBudgetScoreUniversity - prevAverageAllStudentsEGEUniversity} " +
                        "| Минимальный балл: ${resultUniversity.minScore} " +
                        "| Максимальный балл ${resultUniversity.maxScore} " +
                        "| Кол-во поступивших: $countStudents\n"

                println(messageUniversity)
                writer.write(messageUniversity)
                restTemplate.postForEntity(uri, ModellerLog(messageUniversity), String::class.java)

                val messageResultYGSN = "Результаты по УГСН:\n"
                println(messageResultYGSN)
                writer.write(messageResultYGSN)
                restTemplate.postForEntity(uri, ModellerLog(messageResultYGSN), String::class.java)

                for (resultYGSN in resultUniversity.resultYGSNList) {
                    val ygsnId = resultYGSN.ygsnId
                    val prevAverageAllStudentsEGEYGSN = universityInformation.getInformationYGSNMap()[ygsnId]!!.ygsnData.averageScoreBudgetEGE

                    val messageYGSN = "ID УГСН: ${resultYGSN.ygsnId} " +
                            "| Полученный средний балл: ${resultYGSN.averageScore} " +
                            "| Прошлый средний балл: $prevAverageAllStudentsEGEYGSN " +
                            "| Изменение среднего балла: ${resultYGSN.averageScore - prevAverageAllStudentsEGEYGSN} " +
                            "| Минимальный балл: ${resultYGSN.minScore} " +
                            "| Максимальный балл: ${resultYGSN.maxScore} " +
                            "| Кол-во поступивших: ${resultYGSN.countStudents}\n"

                    println(messageYGSN)
                    writer.write(messageYGSN)
                    restTemplate.postForEntity(uri, ModellerLog(messageYGSN), String::class.java)


                }
                println()
                restTemplate.postForEntity(uri, ModellerLog(""), String::class.java)
            }
        }
        println("Итого зачислено $statisticsCountStudent студентов")
    }
}