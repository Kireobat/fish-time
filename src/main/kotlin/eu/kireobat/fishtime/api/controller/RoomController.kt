package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.*
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.service.AuthService
import eu.kireobat.fishtime.service.RoomService
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
@RequestMapping("/api/v1/")
class RoomController(private val roomService: RoomService, private val userService: UserService, private val authService: AuthService) {
    @GetMapping("/rooms")
    fun getRooms(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Int?,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "minCapacity", required = false) minCapacity: Int?,
        @RequestParam(name = "address", required = false) address: String?,
        @RequestParam(name = "activeOnly", required = true) activeOnly: Boolean = true,
        ): ResponseEntity<FishTimePageDto<RoomDto>> {
        return ResponseEntity.ok(roomService.getRooms(pageable,id,name,minCapacity,address,activeOnly))
    }
    @PostMapping("/rooms/create")
    fun createRoom(
        authentication: Authentication?,
        @RequestBody createRoomDto: CreateRoomDto
    ): ResponseEntity<RoomDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        if(!authService.hasSufficientRolePermissions(userEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        return ResponseEntity.ok(roomService.createRoom(createRoomDto,userEntity).toRoomDto())

    }

    @DeleteMapping("/rooms/delete")
    fun deleteRoom(
        authentication: Authentication?,
        @RequestParam(name = "id", required = true) id: Int
    ): ResponseEntity<FishTimeResponseDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        if(!authService.hasSufficientRolePermissions(userEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        roomService.deleteRoom(id)

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Room deleted"))
    }

    @PatchMapping("/rooms/patch")
    fun patchRoom(
        authentication: Authentication?,
        @RequestBody updateRoomDto: UpdateRoomDto
    ): ResponseEntity<RoomDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        if(!authService.hasSufficientRolePermissions(userEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        return ResponseEntity.ok(roomService.updateRoom(updateRoomDto, userEntity))
    }
}