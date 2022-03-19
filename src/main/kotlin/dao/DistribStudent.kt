package dao

import org.jetbrains.exposed.sql.Table

object DistribStudent : Table("distribution_students") {
    var id = integer("id").autoIncrement().primaryKey()

    var countVYP = integer("count_vyp")
    var countParticipant = integer("count_participant")
    var count100Ball = integer("count_100ball")
    var region = varchar("region", 255)
}