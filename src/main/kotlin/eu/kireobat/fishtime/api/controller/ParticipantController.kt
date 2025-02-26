package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateParticipantDto
import eu.kireobat.fishtime.api.dto.FishTimeResponseDto
import eu.kireobat.fishtime.api.dto.ParticipantDto
import eu.kireobat.fishtime.service.AuthService
import eu.kireobat.fishtime.service.MeetingService
import eu.kireobat.fishtime.service.ParticipantService
import eu.kireobat.fishtime.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/v1")
class ParticipantController(
    private val userService: UserService,
    private val participantService: ParticipantService,
    private val meetingService: MeetingService,
    private val authService: AuthService
) {
    @PostMapping("/participants/create")
    fun addParticipant(
        authentication: Authentication?,
        @RequestBody createParticipantDto: CreateParticipantDto
    ): ResponseEntity<ParticipantDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        val meetingEntity = meetingService.findMeetingById(createParticipantDto.meetingId).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "meeting not found")
        }

        val participantUserEntity = userService.findById(createParticipantDto.userId).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
        }

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) || meetingEntity.createdBy.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        return ResponseEntity.ok(participantService.addParticipant(meetingEntity, participantUserEntity, createParticipantDto.status,authUserEntity).toParticipantDto())
    }

    @DeleteMapping("/participants/delete")
    fun deleteParticipant(authentication: Authentication?,
    @RequestParam(name = "userId", required = true) userId: Int,
    @RequestParam(name = "meetingId", required = true) meetingId: Int): ResponseEntity<FishTimeResponseDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        val meetingEntity = meetingService.findMeetingById(meetingId).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "meeting not found")
        }

        val participantUserEntity = userService.findById(userId).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
        }

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) || meetingEntity.createdBy.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        participantService.removeParticipant(meetingEntity,participantUserEntity,authUserEntity)

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(), HttpStatus.OK,"Participants deleted"))
    }
}