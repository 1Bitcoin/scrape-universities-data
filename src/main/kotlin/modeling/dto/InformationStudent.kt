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
    var choice: ConcurrentHashMap<Int, CopyOnWriteArrayList<ChoiceStudent>> = ConcurrentHashMap()

    // Результат, куда подаем оригинал
    var resultChoice: ResultChoice? = null

    // Информация о текущем оригинале заявления
    var currentOriginalChoiceStudent: ResultChoice? = null

    fun setResult(result: ResultChoice) {
        resultChoice = result
    }

    fun setCurrentOriginal(result: ResultChoice) {
        currentOriginalChoiceStudent = result
    }

    fun getCurrentOriginal(): ResultChoice? {
        return currentOriginalChoiceStudent
    }

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

    fun revokeRequest(currentUniversityId: Int, choseStudent: ChoiceStudent) {
        choice[currentUniversityId]!!.remove(choseStudent)

        if (choice[currentUniversityId]!!.size == 0) {
            decreaseCountUniversities()
        }
    }

    fun changeState(currentUniversityId: Int, currentYGSNId: Int, newState: State) {
        choice[currentUniversityId]!!.find { it.ygsnId == currentYGSNId }
            .let {
                if (it != null) {
                    it.state = newState
                }
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