package eu.kireobat.fishtime.api.dto

import java.time.ZonedDateTime

data class RoomDto (
    val id: Int,
    val name: String,
    val capacity: Int,
    val address: String,
    val active: Boolean,
    val createdTime: ZonedDateTime,
    val modifiedTime: ZonedDateTime?
)