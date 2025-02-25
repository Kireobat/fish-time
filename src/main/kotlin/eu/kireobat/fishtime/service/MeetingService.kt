package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.CreateMeetingDto
import eu.kireobat.fishtime.api.dto.FishTimePageDto
import eu.kireobat.fishtime.api.dto.MeetingDto
import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.MeetingRepo
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@Service
class MeetingService(
    private val meetingRepo: MeetingRepo,
    private val roomService: RoomService
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

        val lol = roomEntity.get()

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        return meetingRepo.saveAndFlush(MeetingEntity(
            title = createMeetingDto.title,
            description = createMeetingDto.description,
            startTime = createMeetingDto.startTime,
            endTime = createMeetingDto.endTime,
            room = lol
        ))
    }

    fun deleteMeeting(id: Int) {
        meetingRepo.deleteById(id.toString())
    }

    fun getMeetings(pageable: Pageable, id: Int?, searchQuery: String?, startTime: ZonedDateTime?, endTime: ZonedDateTime?, roomId: Int?, createdBy: Int?, participants: List<Int>?): FishTimePageDto<MeetingDto> {
        val meetings = meetingRepo.findMeetings(pageable, id, searchQuery,startTime,endTime,roomId,createdBy,participants)

        return FishTimePageDto(
            meetings.content.map { entity -> entity.toMeetingDto() },
            meetings.totalElements,
            pageable.pageSize,
            pageable.pageNumber
        )
    }
}