package main.kotlin.dao

import main.kotlin.dao.UniversityYGSNMIREA.references
import org.jetbrains.exposed.sql.Table

object YGSNEGE : Table("ygsn_ege") {
    var id = integer("id").autoIncrement().primaryKey()
    var egeId = integer("ege_id") references EGE.id
    var ygsnId = integer("ygsn_id") references ygsn.id

    var isRequired = bool("is_required")
}