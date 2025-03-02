package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.api.dto.CreateRoomDto
import eu.kireobat.fishtime.api.dto.FishTimePageDto
import eu.kireobat.fishtime.api.dto.RoomDto
import eu.kireobat.fishtime.api.dto.UpdateRoomDto
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.RoomRepo
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import java.util.*

@Service
class RoomService(
    private val roomRepo: RoomRepo,
    private val authService: AuthService,
    private val userService: UserService
) {
    fun createRoom(createRoomDto: CreateRoomDto, authUserEntity: UserEntity): RoomEntity {

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0))) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

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
            createdBy = authUserEntity
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

    fun deleteRoom(id: Int) {
        roomRepo.deleteById(id.toString())
    }

    fun deleteRoomsByCreatedBy(deleteUserEntity: UserEntity, authUserEntity: UserEntity, dataWipe:Boolean) {
        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (dataWipe) {
            roomRepo.deleteAllByCreatedById(deleteUserEntity.id)
        } else {
            roomRepo.updateCreatedByForRooms(deleteUserEntity, userService.findById(1).get())
        }
    }

    fun updateRoom(roomEntity: RoomEntity, authUserEntity: UserEntity, updateRoomDto: UpdateRoomDto): RoomDto {

        if (!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && roomEntity.createdBy.id != authUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val errorList = mutableListOf<String>()

        if (updateRoomDto.name != null) {
            if (updateRoomDto.name.isBlank()) {
                errorList.add("name can't be empty")
            } else if (roomRepo.findByName(updateRoomDto.name).isPresent && roomRepo.findByName(updateRoomDto.name).get().id != updateRoomDto.id)  {
                errorList.add("room already exists")
            }
        }
        if (updateRoomDto.capacity != null && updateRoomDto.capacity > 9000) {
            errorList.add("serious requests only please")
        }

        if (updateRoomDto.name == null &&
            updateRoomDto.capacity == null &&
            updateRoomDto.address == null &&
            updateRoomDto.active == null) {
            errorList.add("bro, why are you sending an empty edit request?")
        }

        if (errorList.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, errorList.toString())
        }

        roomEntity.name = updateRoomDto.name ?: roomEntity.name
        roomEntity.capacity = updateRoomDto.capacity ?: roomEntity.capacity
        roomEntity.address = updateRoomDto.address ?: roomEntity.address
        roomEntity.active = updateRoomDto.active ?: roomEntity.active
        roomEntity.modifiedBy = authUserEntity
        roomEntity.modifiedTime = ZonedDateTime.now()

        return roomRepo.saveAndFlush(roomEntity).toRoomDto()
    }

    fun findRoomById(id: Int): Optional<RoomEntity> {
        return roomRepo.findById(id.toString())
    }
}