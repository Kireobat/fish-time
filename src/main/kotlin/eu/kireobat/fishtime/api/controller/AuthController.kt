package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.api.dto.FishTimeResponseDto
import eu.kireobat.fishtime.api.dto.LoginDto
import eu.kireobat.fishtime.api.dto.UserDto
import eu.kireobat.fishtime.service.AuthService
import eu.kireobat.fishtime.service.UserService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@RestController
@RequestMapping("/api/v1")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginDto: LoginDto,
              request: HttpServletRequest,
    ): ResponseEntity<FishTimeResponseDto> {

        userService.validateLogin(loginDto)

        val authToken = UsernamePasswordAuthenticationToken(loginDto.email, loginDto.password)
        val authentication = authenticationManager.authenticate(authToken)
        SecurityContextHolder.getContext().authentication = authentication

        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        )
        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(), HttpStatus.OK,"Logged in"))
    }

    @PostMapping("/register")
    fun register(
        @RequestBody createUserDto: CreateUserDto,
        request: HttpServletRequest,
    ): ResponseEntity<FishTimeResponseDto> {
        createUserDto.validate(null)
        userService.registerUserByPassword(createUserDto, null)

        val authToken = UsernamePasswordAuthenticationToken(createUserDto.email, createUserDto.password)
        val authentication = authenticationManager.authenticate(authToken)
        SecurityContextHolder.getContext().authentication = authentication

        request.getSession(true).setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            SecurityContextHolder.getContext()
        )

        return ResponseEntity.ok(FishTimeResponseDto(true, ZonedDateTime.now(),HttpStatus.CREATED,"Account registered"))
    }

    @GetMapping("/profile")
    fun getProfile(authentication: Authentication?): ResponseEntity<UserDto> {

        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val userEntity = userService.findOrRegisterByAuthentication(authentication)

        val userDto = UserDto(
            id = userEntity.id,
            username = userEntity.username,
            email = userEntity.email,
            createdTime = userEntity.createdTime,
        )

        return ResponseEntity.ok(userDto)
    }

    @GetMapping("/isAdmin")
    fun getAdminStatus(authentication: Authentication?): ResponseEntity<Boolean> {

        if (authentication == null) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        }

        val authUserEntity = userService.findOrRegisterByAuthentication(authentication)

        if(authService.hasSufficientRolePermissions(authUserEntity, listOf(0))) {
            return ResponseEntity.ok(true)
        }

        return ResponseEntity.ok(false)
    }
}