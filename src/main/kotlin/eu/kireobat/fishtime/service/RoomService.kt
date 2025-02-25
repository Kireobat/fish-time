package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.CreateRoomDto
import eu.kireobat.fishtime.api.dto.FishTimePageDto
import eu.kireobat.fishtime.api.dto.RoomDto
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.RoomRepo
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RoomService(private val roomRepo: RoomRepo) {
    fun createRoom(createRoomDto: CreateRoomDto, userEntity: UserEntity): RoomEntity {

        val errorList = mutableListOf<String>()

        if (createRoomDto.name.isBlank()) {
            errorList.add("name can't be empty")
        } else if (roomRepo.findByName(createRoomDto.name).isPresent) {
            errorList.add("room already exists")
        }

        if (createRoomDto.capacity > 9000) {
            errorList.add("serious requests only please")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }


        return roomRepo.saveAndFlush(RoomEntity(
            name = createRoomDto.name,
            capacity = createRoomDto.capacity,
            address = createRoomDto.address,
            active = createRoomDto.active,
            createdBy = userEntity
        ))
    }

    fun getRooms(pageable: Pageable, id: Int?, name: String?, minCapacity: Int?, address: String?, activeOnly: Boolean = true): FishTimePageDto<RoomDto> {

        val rooms = roomRepo.findRooms(pageable, id, name, minCapacity, address, activeOnly)

        return FishTimePageDto(
            rooms.content.map{entity -> entity.toRoomDto()},
            rooms.totalElements,
            pageable.pageNumber,
            pageable.pageSize
        )
    }
}