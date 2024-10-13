package com.mvnh.plugins

import com.mvnh.database.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/platude",
        user = "postgres",
        password = System.getenv("POSTGRES_PASSWORD") ?: throw IllegalStateException("POSTGRES_PASSWORD environment variable not set"),
    )

    transaction {
        SchemaUtils.create(UsersTable)
    }
}
