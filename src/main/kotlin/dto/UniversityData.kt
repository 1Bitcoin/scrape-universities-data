package dto

data class UniversityData(
    var universityId: Int = -1,
    var name: String = "",
    var region: String = "",
    var yearOfData: Int = 0,
    var hostel: Boolean = false,

    var averageAllStudentsEGE: Double = 0.0,

    var dolyaOfflineEducation: Double = 0.0,

    var averagedMinimalEGE: Double = 0.0,
    var averageBudgetEGE: Double = 0.0,

    var countVserosBVI: Int = 0,
    var countOlimpBVI: Int = 0,
    var countCelevoiPriem: Int = 0,

    var dolyaCelevoiPriem: Double = 0.0,

    var ydelniyVesInostrancyWithoutSNG: Double = 0.0,
    var ydelniyVesInostrancySNG: Double = 0.0,

    var averageBudgetWithoutSpecialRightsEGE: Double = 0.0,

    var dataSource: String = ""
)