package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.TestContainerConfiguration
import eu.kireobat.fishtime.TestDataLoaderUtil
import eu.kireobat.fishtime.api.dto.CreateUserDto
import eu.kireobat.fishtime.api.dto.UpdateUserDto
import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserRepo
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import javax.sql.DataSource

class UserServiceTest: TestContainerConfiguration() {

    @Autowired
    private lateinit var dataSource: DataSource
    
    @Autowired
    private lateinit var userRepo: UserRepo
    
    @Autowired
    private lateinit var userService: UserService
    
    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder
    
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
        
        // Get pre-defined entities from database
        systemUser = userRepo.findById("0").get() // SYSTEM user with ID 0
        regularUser = userRepo.findById("10").get() // A regular user
    }

    @Test
    fun `should find user by id`() {
        // When
        val user = userService.findById(systemUser.id)
        
        // Then
        assertTrue(user.isPresent)
        assertEquals(systemUser.id, user.get().id)
        assertEquals(systemUser.username, user.get().username)
    }
    
    @Test
    fun `should return empty optional when user doesn't exist`() {
        // When
        val user = userService.findById(999)
        
        // Then
        assertTrue(user.isEmpty)
    }
    
    @Test
    fun `should register new user with password`() {
        // Given
        val createUserDto = CreateUserDto(
            username = "newuser",
            email = "new@example.com",
            password = "password123"
        )
        
        // When
        val createdUser = userService.registerUserByPassword(createUserDto, systemUser)
        
        // Then
        assertNotNull(createdUser)
        assertEquals("newuser", createdUser.username)
        assertEquals("new@example.com", createdUser.email)
        assertNotNull(createdUser.passwordHash)
        
        // Verify the password was encoded
        assertTrue(passwordEncoder.matches("password123", createdUser.passwordHash))
        
        // Verify it was stored in database
        val savedUser = userRepo.findById(createdUser.id.toString())
        assertTrue(savedUser.isPresent)
    }
    
    @Test
    fun `should get users page`() {
        // When
        val usersPage = userService.getUsers(PageRequest.of(0, 10), null, null)
        
        // Then
        assertTrue(usersPage.page.isNotEmpty())
        assertTrue(usersPage.totalItems > 0)
    }
    
    @Test
    fun `should update user when user has permission`() {
        // Given
        val updateUserDto = UpdateUserDto(
            id = regularUser.id,
            username = "updateduser",
            email = null,
            password = null
        )
        
        // When
        val updatedUser = userService.updateUser( regularUser, systemUser, updateUserDto)
        
        // Then
        assertNotNull(updatedUser)
        assertEquals("updateduser", updatedUser.username)
        //assertEquals("updated@example.com", updatedUser.email)
        
        // Verify it was stored in database
        val savedUser = userRepo.findById(regularUser.id.toString())
        assertTrue(savedUser.isPresent)
        assertEquals("updateduser", savedUser.get().username)
    }
}