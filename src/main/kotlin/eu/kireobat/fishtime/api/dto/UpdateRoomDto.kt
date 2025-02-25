package eu.kireobat.fishtime.api.dto

data class UpdateRoomDto (
    val id: Int,
    val name: String?,
    val capacity: Int?,
    val address: String?,
    val active: Boolean?,
)