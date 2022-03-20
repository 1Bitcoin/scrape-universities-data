package dto

data class UniversityYGSNMIREAData(
    var universityId: Int = 0,
    var ygsnId: Int = 0,
    var year: Int = 2020,

    var contingentStudents: Double = 0.0,
    var dolyaContingenta: Double = 0.0,
    var numbersBudgetStudents: Int = 0,
    var averageScoreBudgetEGE: Double = 0.0,

    var acceptEGESet: MutableSet<Int> = mutableSetOf()
)