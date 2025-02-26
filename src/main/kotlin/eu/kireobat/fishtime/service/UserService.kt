package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.*
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserRepo
import eu.kireobat.fishtime.persistence.spesification.UserSpecifications
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepo: UserRepo,
    private val authService: AuthService
) {

    fun registerUserByPassword(createUserDto: CreateUserDto, createdBy: UserEntity?): UserEntity {

        val errorList = mutableListOf<String>()
        if (userRepo.findByEmail(createUserDto.email).isPresent) {
            errorList.add("email is already registered")
        }
        if (userRepo.findByUsername(createUserDto.username).isPresent) {
            errorList.add("username is taken")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        val hashedPassword = passwordEncoder.encode(createUserDto.password)

        val tempSave = userRepo.saveAndFlush(UserEntity(
            username = createUserDto.username,
            passwordHash = hashedPassword,
            email = createUserDto.email,
        ))
        if (createdBy == null) {
            tempSave.createdBy = tempSave.id
        } else {
            tempSave.createdBy = createdBy.id
        }


        return userRepo.saveAndFlush(tempSave)
    }

    fun validateLogin(loginDto: LoginDto) {
        val storedUser = userRepo.findByEmail(loginDto.email).getOrElse {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password")
        }

        val match = passwordEncoder.matches(loginDto.password, storedUser.passwordHash)
        if (!match) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email or password")
        }
    }

    fun findOrRegisterByAuthentication(authentication: Authentication): UserEntity {

        return when (val principal = authentication.principal) {
            // email/pass users must have email
            is CustomUserDetails -> userRepo.findByEmail(principal.getEmail()!!).orElseGet {
                userRepo.save(UserEntity(
                    username = principal.getUsername(),
                    passwordHash = principal.getPassword(),
                    email = principal.getEmail()
                ))
            }
            is OAuth2User -> {
                val oAuth2User = authentication.principal as OAuth2User
                val id = oAuth2User.attributes["id"].toString().toInt()
                userRepo.findByOauthId(id).orElseGet {
                    userRepo.save(UserEntity(
                        username = oAuth2User.attributes["login"].toString(),
                        email = oAuth2User.attributes["email"].toString(),
                        oauthId = id
                    ))
                }
            }
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to parse authentication. (Not local or github)")
        }
    }

    fun findByEmail(email: String): Optional<UserEntity> {
        return userRepo.findByEmail(email)
    }

    fun findById(id: Int): Optional<UserEntity> {
        return userRepo.findById(id.toString())
    }

    fun getUsers(pageable: Pageable, id: Int?, searchQuery: String?): FishTimePageDto<UserDto> {
        val spec = Specification.where(UserSpecifications.withId(id))
            .and(UserSpecifications.withSearchQuery(searchQuery))

        val users = userRepo.findAll(spec, pageable)

        return FishTimePageDto(
            users.content.map { entity -> entity.toUserDto() },
            users.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }

    fun deleteUser(deleteUserEntity: UserEntity, authUserEntity: UserEntity) {

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        userRepo.deleteById(deleteUserEntity.id.toString())
    }

    fun updateUser(updateUserEntity: UserEntity, authUserEntity: UserEntity, updateUserDto: UpdateUserDto): UserEntity {

        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != updateUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val errorList = mutableListOf<String>()

        if (updateUserDto.email != null) {
            errorList.add("changing email is currently unsupported")
        }

        if (updateUserDto.password != null) {
            errorList.add("changing password is currently unsupported")
        }

        if (updateUserDto.username != null) {
            if (updateUserDto.username.isBlank()) {
                errorList.add("username cannot be blank")
            } else if (userRepo.findByUsername(updateUserDto.username).isPresent) {
                errorList.add("username taken")
            }
        }


        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        updateUserEntity.username = updateUserDto.username ?: updateUserEntity.username
        updateUserEntity.email = updateUserDto.email ?: updateUserEntity.email
        updateUserEntity.passwordHash = updateUserDto.password ?: updateUserEntity.passwordHash
        updateUserEntity.modifiedBy = authUserEntity.id
        updateUserEntity.modifiedTime = ZonedDateTime.now()

        return userRepo.saveAndFlush(updateUserEntity)
    }
}