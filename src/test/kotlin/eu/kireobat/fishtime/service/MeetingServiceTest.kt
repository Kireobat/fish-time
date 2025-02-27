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
}