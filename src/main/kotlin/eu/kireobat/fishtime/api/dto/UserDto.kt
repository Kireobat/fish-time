package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.ZonedDateTime

data class UserDto (
    val id: Int,
    val username: String,
    val email: String,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val createdTime: ZonedDateTime,
)