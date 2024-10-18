package com.mvnh.auth.dao

import com.mvnh.auth.tables.UserRolesTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserRoleDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserRoleDao>(UserRolesTable)

    var name by UserRolesTable.name
    var description by UserRolesTable.description
}