package com.mvnh.auth.repositories.impl

import com.mvnh.auth.dao.UserDao
import com.mvnh.auth.repositories.UserRoleRepository
import com.mvnh.utils.suspendTransaction
import com.mvnh.auth.tables.UserRolesTable
import com.mvnh.auth.tables.UsersTable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll

class UserRoleRepositoryImpl : UserRoleRepository {
    override suspend fun promoteUserRole(username: String): Boolean = suspendTransaction {
        val user = UserDao.find { UsersTable.username eq username }.firstOrNull()

        requireNotNull(user) { "Account not found" }

        val nextRole = UserRolesTable
            .selectAll().where { UserRolesTable.id greater user.role }
            .orderBy(UserRolesTable.id)
            .firstOrNull()

        require(nextRole != null) { "No higher role available" }

        user.role = nextRole[UserRolesTable.id]
//        user.flush()

        true
    }

    override suspend fun demoteUserRole(username: String): Boolean = suspendTransaction {
        val user = UserDao.find { UsersTable.username eq username }.firstOrNull()

        require(user != null) { "Account not found" }

        val previousRole = UserRolesTable
            .selectAll().where { UserRolesTable.id less user.role }
            .orderBy(UserRolesTable.id, SortOrder.DESC)
            .firstOrNull()

        require(previousRole != null) { "No lower role available" }

        user.role = previousRole[UserRolesTable.id]
//        user.flush()

        true
    }

    override suspend fun getUserRole(username: String): Int = suspendTransaction {
        val user = UserDao.find { UsersTable.username eq username }.firstOrNull()

        require(user != null) { "Account not found" }

        user.role.value
    }
}