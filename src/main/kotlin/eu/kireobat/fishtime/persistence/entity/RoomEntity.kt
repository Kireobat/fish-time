package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="rooms")
data class RoomEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roomsSeq")
    @SequenceGenerator(name = "roomsSeq", sequenceName = "rooms_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @Column(name="name")
    val name: String = "",
    @Column(name="capacity")
    val capacity: Int = 0,
    @Column(name="address")
    val address: String = "",
    @Column(name="active")
    val active: Boolean = true,
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
)