package eu.kireobat.fishtime.api.dto

data class CreateRoomDto (
    val name: String,
    val capacity: Int,
    val address: String,
    val active: Boolean = true
)