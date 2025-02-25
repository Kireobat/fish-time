package eu.kireobat.fishtime.persistence.entity

import eu.kireobat.fishtime.api.dto.MeetingDto
import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="meetings")
data class MeetingEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meetingsSeq")
    @SequenceGenerator(name = "meetingsSeq", sequenceName = "meetings_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="name")
    var title: String = "",
    @Column(name="description")
    var description: String? = null,
    @Column(name="start_time")
    val startTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="end_time")
    val endTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="room_id")
    var room: RoomEntity = RoomEntity(),
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="created_by")
    val createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime? = null,
    @ManyToOne
    @JoinColumn(name="modified_by")
    var modifiedBy: UserEntity? = null,
) {
    fun toMeetingDto(): MeetingDto {
        return MeetingDto(
            id = this.id,
            title = this.title,
            description = this.description,
            startTime = this.startTime,
            endTime = this.endTime,
            room = this.room.toRoomDto(),
            createdTime = this.createdTime,
            createdBy = this.createdBy.toUserDto(),
            modifiedTime = this.modifiedTime,
            modifiedBy = this.modifiedBy?.toUserDto()
        )
    }
}