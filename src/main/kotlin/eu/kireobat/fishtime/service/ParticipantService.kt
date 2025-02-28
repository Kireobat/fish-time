package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.ParticipantRepo
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Service
class ParticipantService(
    private val participantRepo: ParticipantRepo,
    private val authService: AuthService,
    private val userService: UserService
) {

    fun addParticipant(meetingEntity: MeetingEntity, participantUserEntity: UserEntity, status: String, authUserEntity: UserEntity): ParticipantEntity {

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && meetingEntity.createdBy.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        if (participantRepo.findByUserIdAndMeetingId(participantUserEntity.id, participantUserEntity.id).isPresent) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Already a participant")
        }

        return participantRepo.saveAndFlush(ParticipantEntity(
            user = participantUserEntity,
            meetingId = meetingEntity.id,
            status = status,
            createdBy = authUserEntity,
            createdTime = ZonedDateTime.now()
        ))
    }

    fun removeParticipant(meetingEntity: MeetingEntity, participantUserEntity: UserEntity, authUserEntity: UserEntity) {

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && meetingEntity.createdBy.id != authUserEntity.id && participantUserEntity.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        participantRepo.deleteByUserIdAndMeetingId(participantUserEntity.id, meetingEntity.id)
    }

    @Transactional
    fun removeAllParticipantsFromMeeting(meetingEntity: MeetingEntity, authUserEntity: UserEntity) {

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && meetingEntity.createdBy.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        participantRepo.deleteAllByMeetingId(meetingEntity.id)
    }

    fun deleteParticipantsByCreatedBy(deleteUserEntity: UserEntity, authUserEntity: UserEntity, dataWipe:Boolean) {
        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (dataWipe) {
            participantRepo.deleteAllByCreatedById(deleteUserEntity.id)
        } else {
            participantRepo.updateCreatedByForParticipants(deleteUserEntity, userService.findById(1).get())
        }
    }
}