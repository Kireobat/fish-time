package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="rooms")
data class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomsSeq")
    @SequenceGenerator(name = "roomsSeq", sequenceName = "rooms_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="username")
    val username: String = "",
    @Column(name="password_hash")
    val passwordHash: String = "",
    @Column(name="email")
    val email: String = "",
    @ManyToOne
    @JoinColumn(name="oauth_provider")
    val oauthProvider: OAuthProviderEntity? = null,
    @Column(name="oauth_id")
    val oauthId: Int? = null,
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
)