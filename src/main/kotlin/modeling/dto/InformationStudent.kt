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
    private var countUniversities = 0

    // Список с информацией куда и какое заявление подал студент
    private var choice: MutableList<Pair<Int, InformationUniversity>> = mutableListOf()


    fun addRequest(university: InformationUniversity, ygsnId: Int) {
        var count = 0

        choice.add(ygsnId to university)

        // Смотрим были ли уже заявления в данный универ
        for (item in choice) {
            if (item.second.universityData.universityId == university.universityData.universityId) {
                count++
            }
        }

        if (count == 1) {
            increaseCountRequests()
        }
    }

    fun revokeRequest(university: InformationUniversity, ygsnId: Int) {
        var count = 0

        choice.remove(ygsnId to university)

        // Смотрим были ли уже заявления в данный универ
        for (item in choice) {
            if (item.second.universityData.universityId == university.universityData.universityId) {
                count++
            }
        }

        if (count == 0) {
            decreaseCountRequests()
        }
    }

    fun getInformationUniversitiesPair(): MutableList<Pair<Int, InformationUniversity>> {
        return choice
    }

    fun getCountUniversities(): Int {
        return countUniversities
    }

    private fun increaseCountRequests() {
        countUniversities++
    }

    private fun decreaseCountRequests() {
        countUniversities--
    }
}