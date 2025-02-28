package eu.kireobat.fishtime.persistence.entity

import eu.kireobat.fishtime.api.dto.ParticipantDto
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
    var user: UserEntity = UserEntity(),
    @JoinColumn(name="meeting_id")
    var meetingId: Int = 0,
    @Column(name="status")
    val status: String = "",
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="created_by")
    var createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @ManyToOne
    @JoinColumn(name="modified_by")
    val modifiedBy: UserEntity? = null,
) {
    fun toParticipantDto() : ParticipantDto {
        return ParticipantDto(
            id = this.id,
            user = this.user.toUserDto(),
            meetingId = this.meetingId,
            status = this.status,
            createdTime = this.createdTime,
            createdBy = this.createdBy.toUserDto(),
            modifiedTime = this.modifiedTime,
            modifiedBy = this.modifiedBy?.toUserDto(),
        )
    }
}