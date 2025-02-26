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
    @Column(name="title")
    var title: String = "",
    @Column(name="description")
    var description: String? = null,
    @Column(name="start_time")
    var startTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="end_time")
    var endTime: ZonedDateTime = ZonedDateTime.now(),
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

    @OneToMany(mappedBy = "meetingId", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val participants: MutableList<ParticipantEntity> = mutableListOf()
) {
    fun toMeetingDto(): MeetingDto {
        return MeetingDto(
            id = this.id,
            title = this.title,
            description = this.description,
            startTime = this.startTime,
            endTime = this.endTime,
            room = this.room.toRoomDto(),
            participants = this.participants.map { participantEntity -> participantEntity.toParticipantDto() }.toList(),
            createdTime = this.createdTime,
            createdBy = this.createdBy.toUserDto(),
            modifiedTime = this.modifiedTime,
            modifiedBy = this.modifiedBy?.toUserDto()
        )
    }
}