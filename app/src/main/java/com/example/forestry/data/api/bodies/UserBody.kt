package com.example.forestry.data.api.bodies

import com.example.forestry.data.models.User
import com.google.gson.annotations.SerializedName
import java.util.UUID

class UserBody(
    val id: UUID,
    val firstname: String,
    val lastname: String,
    val email: String,
    @SerializedName("password_hash")
    val passwordHash: String,
    val type: String
) {
    fun toModel(): User {
        return User(id, firstname, lastname, email, type)
    }
}