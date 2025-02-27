package eu.kireobat.fishtime.persistence.entity

import eu.kireobat.fishtime.api.dto.RoomDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="rooms")
data class RoomEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomsSeq")
    @SequenceGenerator(name = "roomsSeq", sequenceName = "rooms_seq", allocationSize = 1)
    @Column(name="id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @Column(name="capacity")
    var capacity: Int = 0,
    @Column(name="address")
    var address: String = "",
    @Column(name="active")
    var active: Boolean = true,
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="created_by")
    var createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime? = null,
    @ManyToOne
    @JoinColumn(name="modified_by")
    var modifiedBy: UserEntity? = null,
) {
    fun toRoomDto(): RoomDto {
        return RoomDto(
            id = this.id,
            name = this.name,
            capacity = this.capacity,
            address = this.address,
            active = this.active,
            createdTime = this.createdTime,
            modifiedTime = this.modifiedTime,
            createdBy = this.createdBy.toUserDto(),
            modifiedBy = this.modifiedBy?.toUserDto(),
        )
    }
}