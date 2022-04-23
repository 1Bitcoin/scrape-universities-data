package main.kotlin.modeling.dto

import main.kotlin.dto.UniversityData
import main.kotlin.dto.UniversityYGSNMIREAData

class InformationUniversity(currentUniversityData: UniversityData, currentYGSNList: MutableList<UniversityYGSNMIREAData>) {
    // Информация об универе
    val universityData = currentUniversityData

    // Информация о всех УГСН универа и их конкурсные списки, ключ - УГСН
    private val informationYGSNMap: MutableMap<Int, InformationYGSN> = fillInformationYGSNMap(currentYGSNList)

    fun submitRequest(currentStudentId: Int, currentYGSNId: Int, currentScore: Double, currentState: State, currentPutDate: String) {
        val informationYGSN = informationYGSNMap[currentYGSNId]

        // Получаем конкурсный список этого УГСН
        val competitiveList = informationYGSN!!.competitiveList
        competitiveList.add(Statement(currentStudentId, currentScore, currentState, currentPutDate))

        // Неэффективно - чтобы списки были упорядочены по сумме баллов сортируем их
        competitiveList.sortBy { it.score }
    }

    fun getInformationYGSNMap(): MutableMap<Int, InformationYGSN> {
        return informationYGSNMap
    }

    fun changeState(currentStudentId: Int, currentYGSNId: Int, newState: State, currentUpdateDate: String) {
        val informationYGSN = informationYGSNMap[currentYGSNId]

        // Получаем конкурсный список этого УГСН
        val competitiveList = informationYGSN!!.competitiveList

        competitiveList.find { it.studentId == currentStudentId }
            .let {
                if (it != null) {
                    it.state = newState
                    it.dateUpdateRequest = currentUpdateDate
                }
            }
    }

    fun revokeRequest(currentStudentId: Int, currentYGSNId: Int) {
        val informationYGSN = informationYGSNMap[currentYGSNId]

        // Получаем конкурсный список этого УГСН
        val competitiveList = informationYGSN!!.competitiveList

        val studentForDelete = competitiveList.find { it.studentId == currentStudentId }
        competitiveList.remove(studentForDelete)

        // Неэффективно - чтобы списки были упорядочены по сумме баллов сортируем их
        competitiveList.sortBy { it.score }
    }

    fun getRegion(): String {
        return universityData.region
    }

    fun calculateAcceptEGESet(mapEGE: MutableMap<Int, MutableSet<Int>>) {
        for (item in informationYGSNMap.values) {
            item.ygsnData.acceptEGESet = mapEGE[item.ygsnData.ygsnId]!!
        }
    }

    private fun fillInformationYGSNMap(currentYGSNList: MutableList<UniversityYGSNMIREAData>): MutableMap<Int, InformationYGSN> {
        val informationYGSNMap: MutableMap<Int, InformationYGSN> = mutableMapOf()

        for (item in currentYGSNList) {
            informationYGSNMap[item.ygsnId] = InformationYGSN(item)
        }

        return informationYGSNMap
    }
}
