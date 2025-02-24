package eu.kireobat.fishtime.api.controller

import eu.kireobat.fishtime.api.dto.CreateRoomDto
import eu.kireobat.fishtime.api.dto.RoomDto
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_PAGE_SIZE_INT
import eu.kireobat.fishtime.common.Constants.Companion.DEFAULT_SORT_NO_DIRECTION
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/")
class RoomController {
    @GetMapping("/rooms")
    fun getRooms(
        @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE_INT, sort  = [DEFAULT_SORT_NO_DIRECTION]) pageable: Pageable,
        @RequestParam(name = "id", required = false) id: Number?,
        @RequestParam(name = "name", required = false) name: String?,
        @RequestParam(name = "minCapacity", required = false) minCapacity: Number?,
        @RequestParam(name = "address", required = false) address: String?,
        @RequestParam(name = "activeOnly", required = false) activeOnly: Boolean? = false,
        ) {

    }
    @PostMapping("/rooms/create")
    fun createRoom(
        @RequestBody createRoomDto: CreateRoomDto
    ) {

    }

    @DeleteMapping("/rooms/delete")
    fun deleteRoom(
        @RequestParam(name = "id", required = true) id: Number
    ) {

    }

    @PatchMapping("/rooms/patch")
    fun patchRoom(
        @RequestBody roomDto: RoomDto
    ) {

    }
}