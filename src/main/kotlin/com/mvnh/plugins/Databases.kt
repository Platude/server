package com.mvnh.plugins

import com.mvnh.auth.dao.UserDao
import com.mvnh.auth.dao.UserRoleDao
import com.mvnh.auth.tables.InviteCodesTable
import com.mvnh.auth.tables.TemporaryTokensTable
import com.mvnh.auth.tables.UserRolesTable
import com.mvnh.auth.tables.UsersTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

fun configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5432/platude",
        user = System.getenv("POSTGRES_USER") ?: throw IllegalStateException("POSTGRES_USER environment variable not set"),
        password = System.getenv("POSTGRES_PASSWORD") ?: throw IllegalStateException("POSTGRES_PASSWORD environment variable not set"),
    )

    transaction {
        SchemaUtils.create(
            UsersTable,
            UserRolesTable,
            InviteCodesTable,
            TemporaryTokensTable
        )

        if (UserRoleDao.find { UserRolesTable.name eq "ROLE_DEACTIVATED" }.empty()) {
            UserRoleDao.new {
                name = "ROLE_DEACTIVATED"
                description = "Deactivated user"
            }
        }
        if (UserRoleDao.find { UserRolesTable.name eq "ROLE_USER" }.empty()) {
            UserRoleDao.new {
                name = "ROLE_USER"
                description = "Regular user"
            }
        }
        if (UserRoleDao.find { UserRolesTable.name eq "ROLE_ADMIN" }.empty()) {
            UserRoleDao.new {
                name = "ROLE_ADMIN"
                description = "Administrator"
            }
        }
        if (UserRoleDao.find { UserRolesTable.name eq "ROLE_OWNER" }.empty()) {
            UserRoleDao.new {
                name = "ROLE_OWNER"
                description = "Owner"
            }
        }

        if (UserDao.find { UsersTable.username eq "platude" }.empty()) {
            UserDao.new {
                username = "platude"
                password = BCrypt.hashpw("platude", BCrypt.gensalt())
                role = UserRoleDao.find { UserRolesTable.name eq "ROLE_OWNER" }.first().id
            }
        }
    }
}