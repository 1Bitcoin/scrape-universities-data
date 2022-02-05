package org.jetbrains.database

import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction

fun deleteAllYGSN() {
    val set = mutableSetOf<String>()
    transaction {
        addLogger(StdOutSqlLogger)
        ygsn.deleteAll()
    }
}
