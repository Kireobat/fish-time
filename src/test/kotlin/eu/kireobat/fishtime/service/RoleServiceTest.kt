package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.RoleRepo
import eu.kireobat.fishtime.persistence.repo.UserRepo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource

class RoleServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var roleRepo: RoleRepo
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var roleService: RoleService
    
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
        testDataLoaderUtil.syncSequences(dataSource)
        
        // Get pre-defined users from database
        systemUser = userRepo.findById("0").get() // SYSTEM user with ID 0
        regularUser = userRepo.findById("10").get() // A regular user
    }

    @Test
    fun `should delete roles when user has admin permissions`() {
        // Given
        val initialRoleCount = roleRepo.findAllByCreatedById(regularUser.id).size
        assertTrue(initialRoleCount > 0, "Should have test roles before deletion")
        
        // When
        roleService.deleteRolesByCreatedBy(regularUser, systemUser, true)
        
        // Then
        val remainingRoles = roleRepo.findAllByCreatedById(regularUser.id)
        assertEquals(0, remainingRoles.size, "All roles should be deleted")
    }
    
    @Test
    fun `should throw exception when deleting roles without sufficient permissions`() {
        // When/Then
        val exception = assertThrows(ResponseStatusException::class.java) {
            roleService.deleteRolesByCreatedBy(systemUser, regularUser, true)
        }
        
        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
    }
    
    @Test
    fun `should update created_by when dataWipe is false`() {
        // Given
        val initialRoleCount = roleRepo.findAllByCreatedById(regularUser.id).size
        assertTrue(initialRoleCount > 0, "Should have test roles before reassignment")
        
        // When
        roleService.deleteRolesByCreatedBy(regularUser, systemUser, false)
        
        // Then
        val remainingRoles = roleRepo.findAllByCreatedById(regularUser.id)
        assertEquals(0, remainingRoles.size, "All original user's roles should be reassigned")
        
        // Find the user with ID 1 that roles should be reassigned to
        val systemRoles = roleRepo.findAllByCreatedById(1)
        assertTrue(systemRoles.size >= initialRoleCount, 
            "System user should have received the reassigned roles")
    }
}