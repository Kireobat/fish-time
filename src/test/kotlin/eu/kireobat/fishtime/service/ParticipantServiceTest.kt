package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.MeetingRepo
import eu.kireobat.fishtime.persistence.repo.ParticipantRepo
import eu.kireobat.fishtime.persistence.repo.UserRepo
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource

class ParticipantServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var participantRepo: ParticipantRepo
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var meetingRepo: MeetingRepo
    
    @Autowired
    private lateinit var participantService: ParticipantService
    
    private lateinit var testDataLoaderUtil: TestDataLoaderUtil
    
    private lateinit var systemUser: UserEntity
    private lateinit var regularUser: UserEntity
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
        testMeeting = meetingRepo.findById("10").get() // First meeting
    }

    @Test
    @Transactional
    fun `should add participant to meeting successfully`() {
        // When
        val participant = participantService.addParticipant(testMeeting, regularUser, "ACCEPTED", systemUser)
        
        // Then
        assertNotNull(participant)
        assertEquals(regularUser.id, participant.user.id)
        assertEquals(testMeeting.id, participant.meetingId)
        
        // Verify it was stored in database
        val savedParticipant = participantRepo.findByUserIdAndMeetingId(regularUser.id, testMeeting.id)
        assertNotNull(savedParticipant)
    }
    
    @Test
    @Transactional
    fun `should throw exception when adding participant that already exists`() {
        // Given
        participantService.addParticipant(testMeeting, regularUser, "ACCEPTED", systemUser)
        
        // When/Then
        assertThrows(ResponseStatusException::class.java) {
            participantService.addParticipant(testMeeting, regularUser, "PENDING", systemUser)
        }
    }
    
    @Test
    fun `should throw exception when adding participant without permission`() {
        // Given a user without sufficient permissions
        val userWithNoPermissions = userRepo.findById("12").orElseGet {
            val user = UserEntity().apply { 
                username = "nopermissions"
            }
            userRepo.save(user)
        }
        
        // When/Then
        assertThrows(ResponseStatusException::class.java) {
            participantService.addParticipant(testMeeting, regularUser, "ACCEPTED", userWithNoPermissions)
        }
    }
}