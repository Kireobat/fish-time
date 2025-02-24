package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="oauth_providers")
data class OAuthProviderEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomsSeq")
    @SequenceGenerator(name = "roomsSeq", sequenceName = "rooms_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="name")
    val name: String = "",
    @Column(name="url")
    val url: String = ""
)