package modeling

import modeling.dto.InformationStudent
import modeling.dto.InformationUniversity
import org.jetbrains.database.student.selectEGE
import ru.batch.executor.MyQueryExecutor

class ModelingHelper(limitStudent: Boolean) {

    // Ключ - ид ВУЗа, значение - инфа о ВУЗе
    lateinit var informationUniversityMap2020: LinkedHashMap<Int, InformationUniversity>
    lateinit var informationUniversityMap2019: LinkedHashMap<Int, InformationUniversity>

    // Список с полной информацией о студентах
    lateinit var informationStudent: MutableList<InformationStudent>

    // Соответствия ид УГСН - множество ид ЕГЭ
    val mapEGE: MutableMap<Int, MutableSet<Int>> = fillMapEGE()

    private var executor: MyQueryExecutor = MyQueryExecutor()

    init {
        enrichUniversityDataSet()
        enrichStudentDataSet(limitStudent)
    }

    fun enrichUniversityDataSet() {
        informationUniversityMap2020 = executor.selectInformationUniversities(2020)

        sortInformationUniversity()
        prepareInformationUniversity()
    }

    private fun sortInformationUniversity() {
        println("Сортировка вузов по среднему баллу всех студентов")

        informationUniversityMap2020 = informationUniversityMap2020
            .toList()
            .sortedByDescending { (key, value) -> value.universityData.averageAllStudentsEGE }
            .toMap() as LinkedHashMap<Int, InformationUniversity>
    }

    fun enrichStudentDataSet(limit: Boolean) {
        informationStudent = executor.selectInformationStudent(limit)
    }

    private fun prepareInformationUniversity() {
        println("Вычисление списка ЕГЭ для каждого УГСН")

        for (informationUniversity in informationUniversityMap2020.values) {
            informationUniversity.calculateAcceptEGESet(mapEGE)
        }
    }

    private fun fillMapEGE(): MutableMap<Int, MutableSet<Int>> {
        val map: MutableMap<Int, MutableSet<Int>> = mutableMapOf()

        // id УГСН в таблице
        for (id in 1..58) {
            map[id] = selectEGE(id)
        }
        return map
    }
}