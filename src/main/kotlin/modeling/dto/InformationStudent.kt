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
    private var choice: MutableList<ChoiceStudent> = mutableListOf()


    fun addRequest(choseStudent: ChoiceStudent) {
        var count = 0

        choice.add(choseStudent)

        // Смотрим были ли уже заявления в данный универ
        for (item in choice) {
            if (item.universityId == choseStudent.universityId) {
                count++
            }
        }

        if (count == 1) {
            increaseCountRequests()
        }
    }

    fun revokeRequest(choseStudent: ChoiceStudent) {
        var count = 0

        choice.remove(choseStudent)

        // Смотрим были ли уже заявления в данный универ
        for (item in choice) {
            if (item.universityId == choseStudent.universityId) {
                count++
            }
        }

        if (count == 0) {
            decreaseCountRequests()
        }
    }

    fun getChoicesStudent(): MutableList<ChoiceStudent> {

        // Возвращаем копию массива - ее мы используем для итерации, а удалять элементы будем из исходного массива
        return mutableListOf<ChoiceStudent>().apply { addAll(choice) }
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