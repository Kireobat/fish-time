package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.*
import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.MeetingRepo
import eu.kireobat.fishtime.persistence.spesification.MeetingSpecifications
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class MeetingService(
    private val meetingRepo: MeetingRepo,
    private val roomService: RoomService,
    private val authService: AuthService,
    private val participantService: ParticipantService,
) {
    fun createMeeting(createMeetingDto: CreateMeetingDto, userEntity: UserEntity): MeetingEntity {

        val errorList = mutableListOf<String>()

        if (createMeetingDto.title.isBlank()) {
            errorList.add("title can't be empty")
        }

        val roomEntity = roomService.getRoomById(createMeetingDto.roomId)

        if (roomEntity.isEmpty) {
            errorList.add("room does not exist")
        }

        if (createMeetingDto.endTime < createMeetingDto.startTime) {
            errorList.add("endTime can't be less than startTime")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        return meetingRepo.saveAndFlush(MeetingEntity(
            title = createMeetingDto.title,
            description = createMeetingDto.description,
            startTime = createMeetingDto.startTime,
            endTime = createMeetingDto.endTime,
            createdTime = ZonedDateTime.now(),
            createdBy = userEntity,
            room = roomEntity.get()
        ))
    }

    fun deleteMeeting(id: Int, userEntity: UserEntity) {

        val meetingEntity = findMeetingById(id).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "meeting not found")
        }

        if(!authService.hasSufficientRolePermissions(userEntity, listOf(0)) || meetingEntity.createdBy.id != userEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        participantService.removeAllParticipantsFromMeeting(meetingEntity,userEntity)

        meetingRepo.deleteById(id.toString())
    }

    fun getMeetings(pageable: Pageable, id: Int?, searchQuery: String?, startTime: ZonedDateTime?, endTime: ZonedDateTime?, roomId: Int?, createdBy: Int?, participants: List<Int>?): FishTimePageDto<MeetingDto> {
        val spec = Specification.where(MeetingSpecifications.withId(id))
            .and(MeetingSpecifications.withSearchQuery(searchQuery))
            .and(MeetingSpecifications.withRoomId(roomId))
            .and(MeetingSpecifications.withTimeRange(startTime, endTime))
            .and(MeetingSpecifications.withCreatedBy(createdBy))
            .and(MeetingSpecifications.withParticipants(participants))

        val meetings = meetingRepo.findAll(spec, pageable)

        return FishTimePageDto(
            meetings.content.map { entity -> entity.toMeetingDto() },
            meetings.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }

    fun findMeetingById(id: Int): Optional<MeetingEntity> {
        return meetingRepo.findById(id.toString())
    }

    fun updateMeeting(updatedMeetingDto: UpdateMeetingDto, userEntity: UserEntity): MeetingEntity {
        val errorList = mutableListOf<String>()

        val meetingEntity = meetingRepo.findById(updatedMeetingDto.id.toString()).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "meeting not found")
        }

        if (!authService.hasSufficientRolePermissions(userEntity, listOf(0)) || meetingEntity.createdBy.id != userEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        if (updatedMeetingDto.title != null && updatedMeetingDto.title.isBlank()) {
            errorList.add("name can't be empty")
        }
        if (updatedMeetingDto.description != null && updatedMeetingDto.description.isBlank()) {
            errorList.add("description can't be empty")
        }

        if ((updatedMeetingDto.endTime != null && updatedMeetingDto.startTime != null && updatedMeetingDto.endTime < updatedMeetingDto.startTime) ||
            (updatedMeetingDto.endTime != null && updatedMeetingDto.startTime == null && updatedMeetingDto.endTime < meetingEntity.startTime) ||
            (updatedMeetingDto.endTime == null && updatedMeetingDto.startTime != null && updatedMeetingDto.startTime > meetingEntity.endTime)
            ) {
            errorList.add("endTime can't be less than startTime")
        }

        if (updatedMeetingDto.title == null &&
            updatedMeetingDto.description == null &&
            updatedMeetingDto.startTime == null &&
            updatedMeetingDto.endTime == null &&
            updatedMeetingDto.roomId == null) {
            errorList.add("bro, why are you sending an empty edit request?")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        if (updatedMeetingDto.roomId != null) {
            val roomEntity = roomService.getRoomById(updatedMeetingDto.roomId).getOrElse {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "room with id ${updatedMeetingDto.roomId} not found")
            }

            meetingEntity.room = roomEntity
        }

        meetingEntity.title = updatedMeetingDto.title ?: meetingEntity.title
        meetingEntity.description = updatedMeetingDto.description ?: meetingEntity.description
        meetingEntity.startTime = updatedMeetingDto.startTime ?: meetingEntity.startTime
        meetingEntity.endTime = updatedMeetingDto.endTime ?: meetingEntity.endTime
        meetingEntity.modifiedBy = userEntity
        meetingEntity.modifiedTime = ZonedDateTime.now()

        return meetingRepo.saveAndFlush(meetingEntity)
    }
}