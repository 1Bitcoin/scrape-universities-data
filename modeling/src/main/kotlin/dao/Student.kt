package main.kotlin.dao

import org.jetbrains.exposed.sql.Table

object Student : Table("student") {
    var id = integer("id").autoIncrement().primaryKey()
    var region = varchar("region", 400)
    var change = bool("change_region")
}