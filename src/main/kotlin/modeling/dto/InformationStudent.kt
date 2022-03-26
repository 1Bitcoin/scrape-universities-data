package modeling.dto

import dto.student.StudentData

class InformationStudent(currentStudentData: StudentData, currentYGSNList: MutableList<Int>,
                         currentEGEList: MutableList<EGEResult>) {
    // Информация о студенте
    val studentData = currentStudentData

    // Список интересующих УГСН
    val ygsnList = currentYGSNList

    // Список сданных егэ с баллами
    val egeList = currentEGEList

    // Кол-во ВУЗов, в которое поданы заявления
    private var countRequests = 0

    // Список с информацией куда и какое заявление подал студент
    private var choice: MutableList<ChoiceStudent> = mutableListOf()


    fun addRequest(currentUniversityId: Int, currentYGSNId: Int, currentState: State) {
        if (choice.add(ChoiceStudent(currentUniversityId, currentYGSNId, currentState))) {
            increaseCountRequests()
        }
    }

    fun revokeRequest(currentUniversityId: Int, currentYGSNId: Int) {
        val requestForDelete = choice.find { it.universityId == currentUniversityId && it.ygsnId == currentYGSNId }
        if (choice.remove(requestForDelete)) {
            decreaseCountRequests()
        }
    }

    fun getRequestsList(): MutableList<ChoiceStudent> {
        return choice
    }

    fun getCountRequests(): Int {
        return countRequests
    }

    private fun increaseCountRequests() {
        countRequests++
    }

    private fun decreaseCountRequests() {
        countRequests--
    }
}