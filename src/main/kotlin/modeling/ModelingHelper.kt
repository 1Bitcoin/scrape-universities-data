package modeling

import modeling.dto.InformationStudent
import modeling.dto.InformationUniversity
import org.jetbrains.database.student.selectEGE
import ru.batch.executor.MyQueryExecutor

class ModelingHelper(limitStudent: Boolean) {

    // Ключ - регион нахождения ВУЗа, значение - массив с информацией о вузах в данном регионе
    // Возможно лучше переделать, чтобы мапа хранила ид универа, а студент хранит список ChoiceStudent
    lateinit var informationUniversityMap2020: MutableMap<String, MutableList<InformationUniversity>>
    lateinit var informationUniversityMap2019: MutableMap<String, MutableList<InformationUniversity>>

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
        informationUniversityMap2019 = executor.selectInformationUniversities(2019)

        sortInformationUniversity()
        prepareInformationUniversity()
    }

    private fun sortInformationUniversity() {
        println("Сортировка вузов по среднему баллу всех студентов")

        for (map in informationUniversityMap2020) {
            map.value.sortByDescending  { it.universityData.averageAllStudentsEGE }
        }

        for (map in informationUniversityMap2019) {
            map.value.sortByDescending  { it.universityData.averageAllStudentsEGE }
        }
    }

    fun enrichStudentDataSet(limit: Boolean) {
        informationStudent = executor.selectInformationStudent(limit)
    }

    private fun prepareInformationUniversity() {
        println("Вычисление списка ЕГЭ для каждого УГСН")
        for (region in informationUniversityMap2020) {
            for (university in region.value)
                university.calculateAcceptEGESet(mapEGE)
        }

        for (region in informationUniversityMap2019) {
            for (university in region.value)
                university.calculateAcceptEGESet(mapEGE)
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