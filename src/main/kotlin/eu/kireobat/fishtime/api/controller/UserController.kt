package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.api.dto.FishTimeResponseDto
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import eu.kireobat.fishtime.service.AuthService
import eu.kireobat.fishtime.service.UserService
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/v1/")
class UserController(
    private val userService: UserService,
    private val authService: AuthService
) {
    @GetMapping("/users")
    fun getUsers(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Number?,
        @RequestParam(name = "searchQuery", required = false) name: String?,
        ) {

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

        authService.checkPermissions(userEntity, listOf(0))

        createUserDtoList.forEachIndexed { index, createUserDto ->
            createUserDto.validate(index)
        }

        for (createUserDto in createUserDtoList) {
            userService.registerUserByPassword(createUserDto, userEntity)
        }



        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(),HttpStatus.CREATED,"User(s) created"))
    }
}