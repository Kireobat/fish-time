package eu.kireobat.fishtime.api.dto

import java.time.ZonedDateTime

data class MeetingDto (
    val id: Int,
    val title: String,
    val description: String?,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val room: RoomDto,
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?
)