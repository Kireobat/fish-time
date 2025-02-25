package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.api.dto.LoginDto
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserRepo
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(
    private val passwordEncoder: PasswordEncoder,
    private val userRepo: UserRepo
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
            is CustomUserDetails -> userRepo.findByEmail(principal.getEmail()).orElseGet {
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
}