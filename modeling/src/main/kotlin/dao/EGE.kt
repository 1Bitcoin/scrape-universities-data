package main.kotlin.dao

import org.jetbrains.exposed.sql.Table

object EGE : Table("ege") {
    var id = integer("id").autoIncrement().primaryKey()
    var name = varchar("name", 400)
}