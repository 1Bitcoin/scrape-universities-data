package main.kotlin.dao

import org.jetbrains.exposed.sql.Table

object NameUniversitiesMIREA : Table("nameuniversitiesmirea") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 400)
    var generalname = varchar("generalname", 400)
}