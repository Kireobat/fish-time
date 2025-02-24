package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class UserController {
    @GetMapping("/users")
    fun getUsers(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Number?,
        @RequestParam(name = "searchQuery", required = false) name: String?,
        ) {

    }

    @PostMapping("/users/create")
    fun createUser(
        @RequestBody createUserDto: CreateUserDto
    ) {

    }
}