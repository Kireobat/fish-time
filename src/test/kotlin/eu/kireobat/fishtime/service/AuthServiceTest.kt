package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserRepo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.sql.DataSource

class AuthServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var authService: AuthService
    
    private lateinit var testDataLoaderUtil: TestDataLoaderUtil
    
    private lateinit var systemUser: UserEntity
    private lateinit var regularUser: UserEntity
    private lateinit var adminUser: UserEntity
    
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
        adminUser = userRepo.findById("11").get() // Admin user with role 1
    }

    @Test
    fun `should return true when user has required role`() {
        // When
        val result = authService.hasSufficientRolePermissions(systemUser, listOf(0))
        
        // Then
        assertTrue(result, "System user should have role 0")
    }
    
    @Test
    fun `should return false when user doesn't have required role`() {
        // When
        val result = authService.hasSufficientRolePermissions(regularUser, listOf(0))
        
        // Then
        assertFalse(result, "Regular user should not have role 0")
    }
    
    @Test
    fun `should return true if user has at least one of multiple required roles`() {
        // When
        val result = authService.hasSufficientRolePermissions(adminUser, listOf(0, 11))
        
        // Then
        assertTrue(result, "Admin user should have at least one of the required roles")
    }
    
    @Test
    fun `should return false when required roles list is empty`() {
        // When
        val result = authService.hasSufficientRolePermissions(systemUser, emptyList())
        
        // Then
        assertFalse(result, "Empty required roles should return false")
    }
}