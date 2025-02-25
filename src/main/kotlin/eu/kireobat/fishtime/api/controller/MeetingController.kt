package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateMeetingDto
import eu.kireobat.fishtime.api.dto.FishTimePageDto
import eu.kireobat.fishtime.api.dto.FishTimeResponseDto
import eu.kireobat.fishtime.api.dto.MeetingDto
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.fishtime.service.AuthService
import eu.kireobat.fishtime.service.MeetingService
import eu.kireobat.fishtime.service.UserService
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/v1")
class MeetingController(
    private val userService: UserService,
    private val meetingService: MeetingService,
    private val authService: AuthService
) {

    @GetMapping("/meetings")
    fun getMeetings(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Int?,
        @RequestParam(name = "searchQuery", required = false) searchQuery: String?,
        @RequestParam(name = "startTime", required = false) startTime: ZonedDateTime?,
        @RequestParam(name = "endTime", required = false) endTime: ZonedDateTime?,
        @RequestParam(name = "roomId", required = false) roomId: Int?,
        @RequestParam(name = "createdBy", required = false) createdBy: Int?,
        @RequestParam(name = "participants", required = false) participants: List<Int>?
    ): ResponseEntity<FishTimePageDto<MeetingDto>> {
        return ResponseEntity.ok(meetingService.getMeetings(pageable, id, searchQuery, startTime, endTime, roomId, createdBy, participants))
    }

    @PostMapping("/meetings/create")
    fun createMeeting(
        authentication: Authentication?,
        @RequestBody createMeetingDto: CreateMeetingDto
    ): ResponseEntity<MeetingDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        return ResponseEntity.ok(meetingService.createMeeting(createMeetingDto,userEntity).toMeetingDto())
    }

    @DeleteMapping("/meetings/delete")
    fun deleteMeeting(
        authentication: Authentication?,
        @RequestParam(name = "id", required = true) id: Int
    ): ResponseEntity<FishTimeResponseDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        authService.checkPermissions(userEntity, listOf(0))

        meetingService.deleteMeeting(id)

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(),HttpStatus.OK,"Meeting deleted"))
    }

    @PatchMapping("/meetings/edit")
    fun editMeeting() {

    }
}