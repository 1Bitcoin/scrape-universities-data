import org.jetbrains.exposed.sql.Table

object NameUniversitiesHSE : Table("nameuniversitieshse") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 400)
    var generalname = varchar("generalname", 400)
}

data class NameUniversitiesData(
    var name: String = "",
    var generalname: String = ""
)