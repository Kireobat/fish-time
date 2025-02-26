package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class ParticipantDto (
    val id: Int,
    val userId: Int,
    val meetingId: Int,
    val status: String,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val createdTime: ZonedDateTime,
    val createdBy: UserDto,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val modifiedTime: ZonedDateTime?,
    val modifiedBy: UserDto?,
)