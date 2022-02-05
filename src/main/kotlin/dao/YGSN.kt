import org.jetbrains.exposed.sql.Table

object ygsn : Table("ygsn") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
}

