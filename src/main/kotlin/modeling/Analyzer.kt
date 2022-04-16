package modeling

import modeling.dto.ChoiceStudent
import modeling.dto.InformationUniversity
import modeling.dto.State
import modeling.dto.result.UniversityTotalResult
import modeling.dto.result.YGSNTotalResult
import java.io.BufferedWriter
import kotlin.math.round

class Analyzer() {
    val totalResultModelling = mutableListOf<UniversityTotalResult>()

    fun analyzeResults(universities: LinkedHashMap<Int, InformationUniversity>, writer: BufferedWriter) {
        for (university in universities.values) {
            val universityId = university.universityData.universityId
            var minScoreOfUniversity = 100.0
            var maxScoreOfUniversity = 0.0

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

                        // Уменьшаем кол-во доступных бюджетных мест
                        informationYGSN.ygsnData.numbersBudgetStudents - totalSize

                        // Считаем средний балл по УГСН
                        resultList.forEach { averageScore += it.score }
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
            var averageScoreUniversity = 0.0

            resultYGSNList.forEach { averageScoreUniversity += it.averageScore }
            averageScoreUniversity /= resultYGSNList.size
            averageScoreUniversity = round(averageScoreUniversity)

            totalResultModelling.add(UniversityTotalResult(universityId, averageScoreUniversity, minScoreOfUniversity,
                maxScoreOfUniversity, resultYGSNList))
        }

        for (resultUniversity in totalResultModelling) {
            val messageUniversity = "\nИд университета: ${resultUniversity.universityId} " +
                    "| Средний балл университета: ${resultUniversity.averageAllBudgetScoreUniversity} " +
                    "| Минимальный балл по университету: ${resultUniversity.minScore} " +
                    "| Максимальный балл по университету ${resultUniversity.maxScore}\n"

            println(messageUniversity)
            writer.write(messageUniversity)

            val messageResultYGSN = "Результаты по УГСН:\n"
            println(messageResultYGSN)
            writer.write(messageResultYGSN)


            for (resultYGSN in resultUniversity.resultYGSNList) {
                val messageYGSN = "Ид УГСН: ${resultYGSN.ygsnId} " +
                        "| Средний балл по УГСН: ${resultYGSN.averageScore} " +
                        "| Минимальный балл по УГСН: ${resultYGSN.minScore} " +
                        "| Максимальный балл по УГСН: ${resultYGSN.maxScore} " +
                        "| Кол-во поступивших абитриуентов: ${resultYGSN.countStudents}\n"

                println(messageYGSN)
                writer.write(messageYGSN)

            }
            println()
        }
    }
}