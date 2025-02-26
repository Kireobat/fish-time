package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.*
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

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        createUserDtoList.forEachIndexed { index, createUserDto ->
            createUserDto.validate(index)
        }

        for (createUserDto in createUserDtoList) {
            userService.registerUserByPassword(createUserDto, authUserEntity)
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

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        userMapRoleService.deleteMappingsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        participantService.deleteParticipantsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        meetingService.deleteMeetingsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        roomService.deleteRoomsByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)
        roleService.deleteRolesByCreatedBy(deleteUserEntity, authUserEntity, dataWipe)

        userService.deleteUser(deleteUserEntity, authUserEntity)

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(), HttpStatus.OK, "Room deleted"))
    }

    @PatchMapping("/users/patch")
    fun updateUser(
        authentication: Authentication?,
        @RequestBody updateUserDto: UpdateUserDto
    ): ResponseEntity<UserDto> {
        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        val updateUserEntity = userService.findById(updateUserDto.id).getOrElse {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != updateUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        return ResponseEntity.ok(userService.updateUser(updateUserEntity, authUserEntity, updateUserDto).toUserDto())
    }
}