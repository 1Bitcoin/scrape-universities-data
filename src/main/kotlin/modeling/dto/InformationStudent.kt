package modeling.dto

import dto.student.StudentData
import java.util.Collections.addAll
import java.util.concurrent.ConcurrentHashMap

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
    public var choice: MutableMap<Int, MutableList<ChoiceStudent>> = mutableMapOf()


    fun addRequest(currentUniversityId: Int, choseStudent: ChoiceStudent) {

        // Если такой ключ уже есть в мапе, то надо проверить кол-во элементов в value
        // под этим ключом
        if (choice.containsKey(currentUniversityId)) {
            if (choice[currentUniversityId]!!.size == 0) {
                increaseCountUniversities()
            }

        } else {
            choice[currentUniversityId] = mutableListOf()

            increaseCountUniversities()
        }

        choice[currentUniversityId]!!.add(choseStudent)
    }

    fun revokeRequest(
        iterator: MutableIterator<ChoiceStudent>, currentUniversityId: Int, choseStudent: ChoiceStudent
    ) {
        while (iterator.hasNext()) {
            val elem = iterator.next()

            if (elem.ygsnId == choseStudent.ygsnId && elem.state == choseStudent.state) {
                iterator.remove()
            }
        }

        if (choice[currentUniversityId]!!.size == 0) {
            decreaseCountUniversities()
        }
    }

    fun getChoicesStudent(): MutableMap<Int, MutableList<ChoiceStudent>> {

        // Возвращаем копию мапы - ее мы используем для итерации, а удалять элементы будем из исходной мапы
        return choice.toMutableMap()
    }

    fun getCountUniversities(): Int {
        return countUniversities
    }

    private fun increaseCountUniversities() {
        countUniversities++
    }

    private fun decreaseCountUniversities() {
        countUniversities--
    }
}