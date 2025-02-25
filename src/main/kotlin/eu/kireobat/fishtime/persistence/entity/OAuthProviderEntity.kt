package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="oauth_providers")
data class OAuthProviderEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "oauthProvidersSeq")
    @SequenceGenerator(name = "oauthProvidersSeq", sequenceName = "oauth_providers_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="name")
    val name: String = "",
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @JoinColumn(name="created_by")
    @ManyToOne
    var createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @ManyToOne
    @JoinColumn(name="modified_by")
    val modifiedBy: UserEntity = UserEntity(),
    @Column(name="authorization_url")
    val authorizationUrl: String = "",
    @Column(name="token_url")
    var tokenUrl: String = ""
)