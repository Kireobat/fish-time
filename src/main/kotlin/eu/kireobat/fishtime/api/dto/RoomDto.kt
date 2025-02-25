package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import java.time.ZonedDateTime

data class RoomDto (
    val id: Int,
    val name: String,
    val capacity: Int,
    val address: String,
    val active: Boolean,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val createdTime: ZonedDateTime,
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    val modifiedTime: ZonedDateTime?,
    val createdBy: UserDto,
    val modifiedBy: UserDto?,
) {
    fun toRoomEntity(): RoomEntity {
        return RoomEntity(
            id = this.id,
            name = this.name,
            capacity = this.capacity,
            address = this.address,
            active = this.active,
            createdTime = this.createdTime,
            createdBy = this.createdBy.toUserEntity(),
            modifiedTime = this.modifiedTime,
            modifiedBy = this.modifiedBy?.toUserEntity()
        )
    }
}