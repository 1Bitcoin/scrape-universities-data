package modeling.dto

import dto.UniversityYGSNMIREAData

class InformationYGSN(currentYGSN: UniversityYGSNMIREAData) {

    // Информация об УГСН
    val ygsnData = currentYGSN

    // Конкурсный список, в котором будут находиться заявления абитуриентов
    val competitiveList: MutableList<Statement> = mutableListOf()

}