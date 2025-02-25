package eu.kireobat.fishtime.persistence.entity

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
    val title: String = "",
    @Column(name="description")
    val description: String? = null,
    @ManyToOne
    @JoinColumn(name="room_id")
    val room: RoomEntity = RoomEntity(),
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="created_by")
    val createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @Column(name="modified_by")
    val modifiedBy: UserEntity = UserEntity(),
)