package org.jetbrains.database.student

import dao.YGSNEGE
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun selectEGE(ygsn_id: Int): MutableSet<Int> {
    val set = mutableSetOf<Int>()
    try {
        transaction {
            addLogger(StdOutSqlLogger)

            YGSNEGE
                .select { YGSNEGE.ygsnId.eq(ygsn_id) }
                .forEach{ set.add(it[YGSNEGE.egeId]) }
        }
    } catch (e: Exception) {
        println(e.message)
    }

    return set
}
