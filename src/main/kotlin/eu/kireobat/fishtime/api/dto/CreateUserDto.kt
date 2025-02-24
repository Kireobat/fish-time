package eu.kireobat.fishtime.api.dto

data class CreateUserDto (
    val username: String,
    val password: String,
    val email: String
)