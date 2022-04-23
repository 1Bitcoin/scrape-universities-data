package main.kotlin.dto

data class UniversityYGSNData(
    var yearOfData: Int = 0,
    var universityName: String = "",
    var ygsnName: String = "",
    var averageScoreBudgetEGE: Double? = null,
    var averageScorePaidEGE: Double? = null,
    var numbersBudgetStudents: Int? = null,
    var numbersPaidStudents: Int? = null,
)

