package eu.kireobat.fishtime.api.dto

import java.time.ZonedDateTime

data class CreateMeetingDto (
    val title: String,
    val description: String?,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime,
    val roomId: Int,
)