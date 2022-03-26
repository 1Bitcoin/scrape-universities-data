package modeling.dto

import dto.UniversityData
import dto.UniversityYGSNMIREAData

class InformationUniversity(currentUniversityData: UniversityData, currentYGSNList: MutableList<UniversityYGSNMIREAData>) {
    // Информация об универе
    val universityData = currentUniversityData

    // Информация о всех УГСН универа
    val ygsnList = currentYGSNList

    // Конкурсный список, в котором будут находиться заявления абитуриентов
    private val competitiveList: MutableList<Statement> = mutableListOf()

    fun submitRequest(currentStudentId: Int, currentYGSNId: Int, currentScore: Double, currentState: State) {
        competitiveList.add(Statement(currentStudentId, currentYGSNId, currentScore, currentState))

        // Неэффективно - чтобы списки были упорядочены по сумме баллов сортируем их
        competitiveList.sortByDescending { it.score }
    }

    fun changeState(currentStudentId: Int, newState: State) {
        competitiveList.find { it.studentId == currentStudentId }
            .let {
                if (it != null) {
                    it.state = newState
                }
            }
    }

    fun revokeRequest(currentStudentId: Int) {
        val studentForDelete = competitiveList.find { it.studentId == currentStudentId }
        competitiveList.remove(studentForDelete)
    }

    fun getRegion(): String {
        return universityData.region
    }

    fun calculateAcceptEGESet(mapEGE: MutableMap<Int, MutableSet<Int>>) {
        for (item in ygsnList) {
            item.acceptEGESet = mapEGE.get(item.ygsnId)!!
        }
    }
}