import org.jetbrains.exposed.sql.Table

object University : Table("university") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 400)
    var region = varchar("region", 400)
    var yearOfData = integer("yearofdata")
    var hostel = bool("hostel")

    var averageAllStudentsEGE = double("averageallstudentsege")
    var dolyaOfflineEducation = double("dolyaofflineeducation")

    var averagedMinimalEGE = double("averagedminimalege")
    var averageBudgetEGE = double("averagebudgetege")

    var countVserosBVI = integer("countvserosbvi")
    var countOlimpBVI = integer("countolimpbvi")
    var countCelevoiPriem = integer("countcelevoipriem")

    var dolyaCelevoiPriem = double("dolyacelevoipriem")

    var ydelniyVesInostrancyWithoutSNG = double("ydelniyvesinostrancywithoutsng")
    var ydelniyVesInostrancySNG = double("ydelniyvesinostrancysng")

    var averageBudgetWithoutSpecialRightsEGE = double("averagebudgetwithoutspecialrightsege")

    var jsonYGSN = varchar("jsonygsn", 1000000)
    var dataSource = varchar("datasource", 10)
}

data class UniversityData(
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

    var jsonYGSN: String,

    var dataSource: String = ""
)