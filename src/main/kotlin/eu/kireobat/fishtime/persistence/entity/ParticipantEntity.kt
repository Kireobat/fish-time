package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="participants")
data class ParticipantEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "participantsSeq")
    @SequenceGenerator(name = "participantsSeq", sequenceName = "participants_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @ManyToOne
    @JoinColumn(name="user_id")
    val user: UserEntity = UserEntity(),
    @ManyToOne
    @JoinColumn(name="meeting_id")
    val meeting: MeetingEntity = MeetingEntity(),
    @Column(name="status")
    val status: String = "",
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="created_by")
    var createdBy: Int? = null,
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @Column(name="modified_by")
    val modifiedBy: Int? = null,
)