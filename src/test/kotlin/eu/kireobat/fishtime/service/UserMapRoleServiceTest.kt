package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserMapRoleRepo
import eu.kireobat.fishtime.persistence.repo.UserRepo
import eu.kireobat.fishtime.persistence.repo.RoleRepo
import eu.kireobat.fishtime.TestDataLoaderUtil
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource

class UserMapRoleServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var userMapRoleRepo: UserMapRoleRepo
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var userMapRoleService: UserMapRoleService
    
    private lateinit var testDataLoaderUtil: TestDataLoaderUtil
    
    private lateinit var systemUser: UserEntity
    private lateinit var regularUser: UserEntity
    
    @BeforeEach
    fun setup() {
        testDataLoaderUtil = TestDataLoaderUtil()
        
        // Clean and reload test data before each test
        testDataLoaderUtil.cleanAllTestData(dataSource)
        testDataLoaderUtil.insertSystemUser(dataSource)
        testDataLoaderUtil.insertUsers(dataSource)
        testDataLoaderUtil.insertRoles(dataSource)
        testDataLoaderUtil.insertUsersMapRoles(dataSource)
        testDataLoaderUtil.syncSequences(dataSource)
        
        // Get pre-defined users from database
        systemUser = userRepo.findById("0").get() // SYSTEM user with ID 0
        regularUser = userRepo.findById("10").get() // A regular user
    }

    @Test
    fun `should delete mappings when user has sufficient permissions`() {
        // Given
        val initialMappingsCount = userMapRoleRepo.findAllByCreatedBy(systemUser.id).size
        assertTrue(initialMappingsCount > 0, "Should have test mappings before deletion")
        
        // When
        userMapRoleService.deleteMappingsByCreatedBy(regularUser, systemUser, true)
        
        // Then
        val remainingMappings = userMapRoleRepo.findAllByCreatedBy(regularUser.id)
        assertEquals(0, remainingMappings.size, "All mappings should be deleted")
    }

    @Test
    fun `should throw exception when deleting mappings without sufficient permissions`() {
        // Given - regularUser trying to delete another user's mappings
        val otherUser = userRepo.findById("11").orElseGet {
            // Create another test user if not found
            val user = UserEntity().apply { username = "otheruser" }
            userRepo.save(user)
        }
        
        // When/Then
        val exception = assertThrows(ResponseStatusException::class.java) {
            userMapRoleService.deleteMappingsByCreatedBy(systemUser, regularUser, true)
        }
        
        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
    }
    
    @Test
    @Transactional
    fun `should update created_by when dataWipe is false`() {
        // Given
        val initialMappingsCount = userMapRoleRepo.findAllByCreatedBy(regularUser.id).size
        assertTrue(initialMappingsCount > 0, "Should have test mappings before reassignment")
        
        // When
        userMapRoleService.deleteMappingsByCreatedBy(regularUser, systemUser, false)
        
        // Then
        val remainingMappings = userMapRoleRepo.findAllByCreatedBy(regularUser.id)
        assertEquals(0, remainingMappings.size, "All original user's mappings should be reassigned")
        
        // Verify the mappings were reassigned to SYSTEM user
        val systemMappings = userMapRoleRepo.findAllByCreatedBy(systemUser.id)
        assertTrue(systemMappings.size >= initialMappingsCount, 
            "System user should have received the reassigned mappings")
    }
}