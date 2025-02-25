package eu.kireobat.fishtime.api.dto

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

data class CreateUserDto (
    val username: String,
    val password: String,
    val email: String
) {
    fun validate(index: Number?) {

        val errorList = mutableListOf<String>()

        val emailRegex: Regex = "^(\\w|\\d|\\.)+@(\\w|\\d)+\\.\\w+\$".toRegex()

        if (username.isBlank()) {
            errorList.add("username must not be blank")
        } else if (username.length > 30) {
            errorList.add("username is too long")
        }
        if (password.isBlank()) {
            errorList.add("password must not be blank")
        } else if (password.length < 8) {
            errorList.add("password must have at least 8 characters")
        }

        if (email.isBlank()) {
            errorList.add("email must not be blank")
        } else if (email.length > 255) {
            errorList.add("email is too long")
        } else if (!email.matches(emailRegex)) {
            errorList.add("email is not valid")
        }

        val errorMessage = if (index != null) {
            "index is $index" + errorList.joinToString(", ")
        } else {
            errorList.joinToString(", ")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,errorMessage)
        }
    }
}