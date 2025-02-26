package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class UpdateMeetingDto (
    val id: Int,
    val title: String?,
    val description: String?,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val startTime: ZonedDateTime?,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val endTime: ZonedDateTime?,
    val roomId: Int?,
)