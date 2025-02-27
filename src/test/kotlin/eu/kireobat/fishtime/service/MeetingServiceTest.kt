package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.api.dto.CreateMeetingDto
import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.MeetingRepo
import eu.kireobat.fishtime.persistence.repo.RoomRepo
import eu.kireobat.fishtime.persistence.repo.UserRepo
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime
import javax.sql.DataSource

class MeetingServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var meetingRepo: MeetingRepo
    
    @Autowired
    private lateinit var roomRepo: RoomRepo
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var meetingService: MeetingService
    
    private lateinit var testDataLoaderUtil: TestDataLoaderUtil
    
    private lateinit var systemUser: UserEntity
    private lateinit var regularUser: UserEntity
    private lateinit var testRoom: RoomEntity
    private lateinit var testMeeting: MeetingEntity
    
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
        testDataLoaderUtil.insertMeetings(dataSource)
        testDataLoaderUtil.syncSequences(dataSource)
        
        // Get pre-defined entities from database
        systemUser = userRepo.findById("0").get() // SYSTEM user with ID 0
        regularUser = userRepo.findById("10").get() // A regular user
        testRoom = roomRepo.findById("10").get() // Room with ID 10
        testMeeting = meetingRepo.findById("10").get() // First meeting
    }

    @Test
    @Transactional
    fun `should create meeting successfully`() {
        // Given
        val startTime = ZonedDateTime.now().plusHours(1)
        val endTime = ZonedDateTime.now().plusHours(2)
        val createMeetingDto = CreateMeetingDto(
            title = "New Test Meeting",
            description = "This is a test meeting",
            roomId = testRoom.id,
            startTime = startTime,
            endTime = endTime
        )
        
        // When
        val createdMeeting = meetingService.createMeeting(createMeetingDto, systemUser)
        
        // Then
        assertNotNull(createdMeeting)
        assertEquals("New Test Meeting", createdMeeting.title)
        assertEquals("This is a test meeting", createdMeeting.description)
        assertEquals(testRoom.id, createdMeeting.room.id)
        
        // Verify it was stored in database
        val savedMeeting = meetingRepo.findById(createdMeeting.id.toString())
        assertTrue(savedMeeting.isPresent)
    }
    
    @Test
    fun `should find meeting by id`() {
        // When
        val meeting = meetingService.findMeetingById(testMeeting.id)
        
        // Then
        assertTrue(meeting.isPresent)
        assertEquals(testMeeting.id, meeting.get().id)
        assertEquals(testMeeting.title, meeting.get().title)
    }
    
    @Test
    @Transactional
    fun `should get meetings page`() {
        // When
        val meetingPage = meetingService.getMeetings(PageRequest.of(0, 10), null, null, null, null, null, null, null)
        
        // Then
        assertTrue(meetingPage.page.isNotEmpty())
        assertTrue(meetingPage.totalItems > 0)
    }
    @Test
        @Transactional
        fun `should prevent regular user from double booking a room`() {
            // Given
            val startTime = ZonedDateTime.now().plusHours(1)
            val endTime = ZonedDateTime.now().plusHours(2)
            
            // Create first meeting
            val firstMeetingDto = CreateMeetingDto(
                title = "First Meeting", 
                description = "This is the first meeting",
                roomId = testRoom.id,
                startTime = startTime,
                endTime = endTime
            )
            meetingService.createMeeting(firstMeetingDto, systemUser)
            
            // Try to create an overlapping meeting as regular user
            val overlappingMeetingDto = CreateMeetingDto(
                title = "Overlapping Meeting", 
                description = "This should fail for regular users",
                roomId = testRoom.id,
                startTime = startTime.plusMinutes(30),
                endTime = endTime.plusMinutes(30)
            )
            
            // When/Then
            val exception = assertThrows(ResponseStatusException::class.java) {
                meetingService.createMeeting(overlappingMeetingDto, regularUser)
            }
            assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
            assertTrue(exception.reason?.contains("Room is already booked") == true)
        }

        @Test
        @Transactional
        fun `should allow admin to double book a room`() {
            // Given
            val startTime = ZonedDateTime.now().plusHours(1)
            val endTime = ZonedDateTime.now().plusHours(2)
            
            // Create first meeting
            val firstMeetingDto = CreateMeetingDto(
                title = "First Meeting", 
                description = "This is the first meeting",
                roomId = testRoom.id,
                startTime = startTime,
                endTime = endTime
            )
            meetingService.createMeeting(firstMeetingDto, regularUser)
            
            // Try to create an overlapping meeting as admin user
            val overlappingMeetingDto = CreateMeetingDto(
                title = "Admin Overlapping Meeting", 
                description = "This should succeed for admin users",
                roomId = testRoom.id,
                startTime = startTime.plusMinutes(30),
                endTime = endTime.plusMinutes(30)
            )
            
            // When - this should not throw an exception
            val createdMeeting = meetingService.createMeeting(overlappingMeetingDto, systemUser)
            
            // Then
            assertNotNull(createdMeeting)
            assertEquals("Admin Overlapping Meeting", createdMeeting.title)
            assertEquals(testRoom.id, createdMeeting.room.id)
        }

        @Test
        @Transactional
        fun `should detect various overlap scenarios for regular users`() {
            // Given
            val baseStart = ZonedDateTime.now().plusHours(1)
            val baseEnd = ZonedDateTime.now().plusHours(3)
            
            // Create a baseline meeting
            val baseMeetingDto = CreateMeetingDto(
                title = "Base Meeting",
                description = "2-hour meeting",
                roomId = testRoom.id,
                startTime = baseStart,
                endTime = baseEnd
            )
            meetingService.createMeeting(baseMeetingDto, systemUser)
            
            // Test different overlap scenarios
            val overlapScenarios = listOf(
                Pair("Complete overlap", Pair(baseStart.plusMinutes(30), baseEnd.minusMinutes(30))),
                Pair("Partial overlap at start", Pair(baseStart.minusHours(1), baseStart.plusHours(1))),
                Pair("Partial overlap at end", Pair(baseEnd.minusHours(1), baseEnd.plusHours(1))),
                Pair("Surrounding overlap", Pair(baseStart.minusHours(1), baseEnd.plusHours(1)))
            )
            
            for ((scenarioName, times) in overlapScenarios) {
                val overlappingDto = CreateMeetingDto(
                    title = "Overlapping Meeting - $scenarioName",
                    description = "This should fail",
                    roomId = testRoom.id,
                    startTime = times.first,
                    endTime = times.second
                )
                
                val exception = assertThrows(ResponseStatusException::class.java) {
                    meetingService.createMeeting(overlappingDto, regularUser)
                }
                assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode, "Failed on scenario: $scenarioName")
            }
        }

        @Test
        @Transactional
        fun `should allow booking adjacent time slots`() {
            // Given
            val firstMeetingStart = ZonedDateTime.now().plusHours(1)
            val firstMeetingEnd = ZonedDateTime.now().plusHours(2)
            
            // Create first meeting
            val firstMeetingDto = CreateMeetingDto(
                title = "First Meeting",
                description = "This is the first meeting",
                roomId = testRoom.id,
                startTime = firstMeetingStart,
                endTime = firstMeetingEnd
            )
            meetingService.createMeeting(firstMeetingDto, regularUser)
            
            // Try to create adjacent meetings (should succeed)
            val beforeMeetingDto = CreateMeetingDto(
                title = "Before Meeting",
                description = "Meeting right before first meeting",
                roomId = testRoom.id,
                startTime = firstMeetingStart.minusHours(1),
                endTime = firstMeetingStart
            )
            
            val afterMeetingDto = CreateMeetingDto(
                title = "After Meeting",
                description = "Meeting right after first meeting",
                roomId = testRoom.id,
                startTime = firstMeetingEnd,
                endTime = firstMeetingEnd.plusHours(1)
            )
            
            // When
            val beforeMeeting = meetingService.createMeeting(beforeMeetingDto, regularUser)
            val afterMeeting = meetingService.createMeeting(afterMeetingDto, regularUser)
            
            // Then
            assertNotNull(beforeMeeting)
            assertNotNull(afterMeeting)
            assertEquals("Before Meeting", beforeMeeting.title)
            assertEquals("After Meeting", afterMeeting.title)
        }
}