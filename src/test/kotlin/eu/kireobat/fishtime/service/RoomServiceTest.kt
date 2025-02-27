package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.api.dto.CreateRoomDto
import eu.kireobat.fishtime.api.dto.UpdateRoomDto
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.RoomRepo
import eu.kireobat.fishtime.persistence.repo.UserRepo
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource

class RoomServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var roomRepo: RoomRepo
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var roomService: RoomService
    
    private lateinit var testDataLoaderUtil: TestDataLoaderUtil
    
    private lateinit var systemUser: UserEntity
    private lateinit var regularUser: UserEntity
    private lateinit var testRoom: RoomEntity

    @BeforeEach
    fun setup() {
        testDataLoaderUtil = TestDataLoaderUtil()
        
        // Clean and reload test data before each test
        testDataLoaderUtil.cleanAllTestData(dataSource)
        testDataLoaderUtil.insertSystemUser(dataSource)
        testDataLoaderUtil.insertUsers(dataSource)
        testDataLoaderUtil.insertRoles(dataSource)
        testDataLoaderUtil.insertUsersMapRoles(dataSource)
        testDataLoaderUtil.insertRooms(dataSource)
        testDataLoaderUtil.syncSequences(dataSource)
        
        // Get pre-defined entities from database
        systemUser = userRepo.findById("0").get() // SYSTEM user with ID 0
        regularUser = userRepo.findById("10").get() // A regular user
        testRoom = roomRepo.findById("10").get() // Room with ID 10
    }

    @Test
    @Transactional
    fun `should create room successfully`() {
        // Given
        val createRoomDto = CreateRoomDto(
            name = "New Test Room",
            address = "123 Test Street",
            capacity = 20
        )

        // When
        val createdRoom = roomService.createRoom(createRoomDto, systemUser)
        
        // Then
        assertNotNull(createdRoom)
        assertEquals("New Test Room", createdRoom.name)
        assertEquals("123 Test Street", createdRoom.address)
        assertEquals(20, createdRoom.capacity)
        
        // Verify it was stored in database
        val savedRoom = roomRepo.findById(createdRoom.id.toString())
        assertTrue(savedRoom.isPresent)
    }
    
    @Test
    fun `should find room by id`() {
        // When
        val room = roomService.findRoomById(testRoom.id)
        
        // Then
        assertTrue(room.isPresent)
        assertEquals(testRoom.id, room.get().id)
        assertEquals(testRoom.name, room.get().name)
    }
    
    @Test
    @Transactional
    fun `should update room when user has permission`() {
        // Given
        val updateRoomDto = UpdateRoomDto(
            id = testRoom.id,
            name = "Updated Room Name",
            address = "456 Updated Street",
            capacity = 30,
            active = false
        )
        
        // When
        val updatedRoom = roomService.updateRoom(testRoom, systemUser, updateRoomDto)
        
        // Then
        assertNotNull(updatedRoom)
        assertEquals("Updated Room Name", updatedRoom.name)
        assertEquals("456 Updated Street", updatedRoom.address)
        assertEquals(30, updatedRoom.capacity)
        assertFalse(updatedRoom.active)
        
        // Verify it was stored in database
        val savedRoom = roomRepo.findById(testRoom.id.toString())
        assertTrue(savedRoom.isPresent)
        assertEquals("Updated Room Name", savedRoom.get().name)
    }
    
    @Test
    @Transactional
    fun `should throw exception when updating room without permission`() {
        // Given
        val updateRoomDto = UpdateRoomDto(
            id = testRoom.id,
            name = "Updated Room Name",
            address = "456 Updated Street",
            capacity = 30,
            active = false
        )
        
        // When/Then
        assertThrows(ResponseStatusException::class.java) {
            roomService.updateRoom(testRoom, regularUser, updateRoomDto)
        }
    }
}