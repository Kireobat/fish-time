package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import eu.kireobat.fishtime.persistence.entity.UserEntity
import java.time.ZonedDateTime

data class UserDto (
    val id: Int,
    val username: String,
    val email: String,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val createdTime: ZonedDateTime,
) {
    fun toUserEntity(): UserEntity {
        return UserEntity(
            id = this.id,
            username = this.username,
            email = this.username,
            createdTime = this.createdTime
        )
    }
}