package eu.kireobat.fishtime.api.dto

data class UpdateUserDto(
    val id: Int,
    val username: String?,
    val email: String?,
    val password: String?
)
