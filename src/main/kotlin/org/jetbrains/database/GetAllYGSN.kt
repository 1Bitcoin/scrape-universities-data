package org.jetbrains.database

import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun getAllYGSN(): Set<String> {
    val set = mutableSetOf<String>()
    transaction {
        addLogger(StdOutSqlLogger)

        for (ygsnRow in ygsn.selectAll()) {
            set.add(ygsnRow[ygsn.name])
        }
    }
    return set
}