package com.example.forestry.data.models

import java.util.UUID

data class User(
    val id: UUID,
    val firstname: String,
    val lastname: String,
    val email: String,
    val type: String
)
