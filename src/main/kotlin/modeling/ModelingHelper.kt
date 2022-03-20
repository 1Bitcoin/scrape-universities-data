package modeling

import modeling.dto.InformationUniversity
import org.jetbrains.database.student.selectEGE
import ru.batch.executor.MyQueryExecutor

class ModelingHelper {

    // Списки с полной информацией о ВУЗах за определенный год
    lateinit var informationUniversityList2020: MutableList<InformationUniversity>
    lateinit var informationUniversityList2019: MutableList<InformationUniversity>

    // Соответствия ид УГСН - множество ид ЕГЭ
    private val mapEGE: MutableMap<Int, MutableSet<Int>> = fillMapEGE()

    private var executor = MyQueryExecutor()

    fun enrichDataSet() {
        informationUniversityList2020 = executor.selectInformationUniversities(2020)
        informationUniversityList2019 = executor.selectInformationUniversities(2019)
    }

    fun prepareInformationUniversity() {
        println("Вычисление списка ЕГЭ для каждого УГСН")
        for (item in informationUniversityList2020) {
            item.calculateAcceptEGESet(mapEGE)
        }

        for (item in informationUniversityList2019) {
            item.calculateAcceptEGESet(mapEGE)
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