package eu.kireobat.fishtime.persistence.entity

import eu.kireobat.fishtime.api.dto.UserDto
import jakarta.persistence.*
import java.io.Serializable
import java.time.ZonedDateTime

@Entity
@Table(name="users")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersSeq")
    @SequenceGenerator(name = "usersSeq", sequenceName = "users_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="username")
    var username: String = "",
    @Column(name="password_hash")
    var passwordHash: String? = null,
    @Column(name="email")
    var email: String? = null,
    @ManyToOne
    @JoinColumn(name="oauth_provider")
    val oauthProvider: OAuthProviderEntity? = null,
    @Column(name="oauth_id")
    val oauthId: Int? = null,
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="created_by")
    var createdBy: Int? = null,
    @Column(name="modified_time")
    var modifiedTime: ZonedDateTime? = null,
    @Column(name="modified_by")
    var modifiedBy: Int? = null,
): Serializable {
    fun toUserDto(): UserDto {
        return UserDto(
            id = this.id,
            username = this.username,
            email = this.email,
            createdTime = this.createdTime,
        )
    }
}