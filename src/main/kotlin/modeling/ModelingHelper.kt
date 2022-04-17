package modeling

import modeling.dto.InformationStudent
import modeling.dto.InformationUniversity
import org.jetbrains.database.student.selectEGE
import ru.batch.executor.MyQueryExecutor

class ModelingHelper(limitStudent: Int, year: Int) {

    // Ключ - ид ВУЗа, значение - инфа о ВУЗе
    lateinit var informationUniversityMap: LinkedHashMap<Int, InformationUniversity>

    // Список с полной информацией о студентах
    lateinit var informationStudent: MutableList<InformationStudent>

    // Соответствия ид УГСН - множество ид ЕГЭ
    val mapEGE: MutableMap<Int, MutableSet<Int>> = fillMapEGE()

    private var executor: MyQueryExecutor = MyQueryExecutor()

    init {
        enrichUniversityDataSet(year)
        enrichStudentDataSet(limitStudent)
    }

    private fun enrichUniversityDataSet(year: Int) {
        informationUniversityMap = executor.selectInformationUniversities(year)

        sortInformationUniversity()
        prepareInformationUniversity()
    }

    private fun sortInformationUniversity() {
        println("Сортировка вузов по среднему баллу всех студентов")

        informationUniversityMap = informationUniversityMap
            .toList()
            .sortedByDescending { (key, value) -> value.universityData.averageAllStudentsEGE }
            .toMap() as LinkedHashMap<Int, InformationUniversity>
    }

    private fun enrichStudentDataSet(limitStudent: Int) {
        informationStudent = executor.selectFullInformationStudent(limitStudent)
    }

    private fun prepareInformationUniversity() {
        println("Вычисление списка ЕГЭ для каждого УГСН")

        for (informationUniversity in informationUniversityMap.values) {
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