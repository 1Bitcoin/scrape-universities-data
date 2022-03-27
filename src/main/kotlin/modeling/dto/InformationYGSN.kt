package modeling.dto

import dto.UniversityYGSNMIREAData

class InformationYGSN(currentYGSN: UniversityYGSNMIREAData) {

    // Конкурсный список, в котором будут находиться заявления абитуриентов
    val competitiveList: MutableList<Statement> = mutableListOf()

    // Информация об УГСН
    val ygsn = currentYGSN

}