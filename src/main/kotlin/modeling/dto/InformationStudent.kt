package modeling.dto

import dto.student.StudentData
import java.util.Collections.addAll
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

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
    public var choice: ConcurrentHashMap<Int, CopyOnWriteArrayList<ChoiceStudent>> = ConcurrentHashMap()

    fun addRequest(currentUniversityId: Int, choseStudent: ChoiceStudent, listIterator: MutableListIterator<ChoiceStudent>? = null) {
        // Если такой ключ уже есть в мапе, то надо проверить кол-во элементов в value
        // под этим ключом
        if (choice.containsKey(currentUniversityId)) {
            if (choice[currentUniversityId]!!.size == 0) {
                increaseCountUniversities()
            }

        } else {
            choice[currentUniversityId] = CopyOnWriteArrayList(mutableListOf())

            increaseCountUniversities()
        }

        choice[currentUniversityId]!!.add(choseStudent)
    }

    fun revokeRequest(iterator: MutableIterator<ChoiceStudent>, currentUniversityId: Int, choseStudent: ChoiceStudent) {
        choice[currentUniversityId]!!.remove(choseStudent)

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