package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.api.dto.FishTimePageDto
import eu.kireobat.fishtime.api.dto.FishTimeResponseDto
import eu.kireobat.fishtime.api.dto.UserDto
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.fishtime.service.*
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import kotlin.jvm.optionals.getOrElse

@RestController
@RequestMapping("/api/v1/")
class UserController(
    private val userService: UserService,
    private val authService: AuthService,
    private val roleService: RoleService,
    private val roomService: RoomService,
    private val meetingService: MeetingService,
    private val participantService: ParticipantService,
    private val userMapRoleService: UserMapRoleService
) {
    @GetMapping("/users")
    fun getUsers(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Int?,
        @RequestParam(name = "searchQuery", required = false) searchQuery: String?,
        ): ResponseEntity<FishTimePageDto<UserDto>> {
        return ResponseEntity.ok(userService.getUsers(pageable,id,searchQuery))
    }

    @PostMapping("/users/create")
    fun createUser(
        authentication: Authentication?,
        @RequestBody createUserDtoList: List<CreateUserDto>
    ): ResponseEntity<FishTimeResponseDto> {

        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        if(!authService.hasSufficientRolePermissions(userEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        createUserDtoList.forEachIndexed { index, createUserDto ->
            createUserDto.validate(index)
        }

        for (createUserDto in createUserDtoList) {
            userService.registerUserByPassword(createUserDto, userEntity)
        }



        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(),HttpStatus.CREATED,"User(s) created"))
    }

    @DeleteMapping("/users/delete")
    fun deleteUser(
        authentication: Authentication?,
        @RequestParam("id") id: Int,
        @RequestParam(name = "dataWipe", required = true) dataWipe: Boolean = false
    ): ResponseEntity<FishTimeResponseDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        val deleteUserEntity = userService.findById(id).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) || authUserEntity.id == deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        userMapRoleService.deleteMappingsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        participantService.deleteParticipantsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        meetingService.deleteMeetingsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        roomService.deleteRoomsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        roleService.deleteRolesByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)

        userService.deleteUser(id)

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Room deleted"))
    }
}