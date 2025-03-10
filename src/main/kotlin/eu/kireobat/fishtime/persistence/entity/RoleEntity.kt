package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="roles")
data class RoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolesSeq")
    @SequenceGenerator(name = "rolesSeq", sequenceName = "roles_seq", allocationSize = 1)
    @Column(name="id")
    var id: Int = 0,
    @Column(name="name")
    var name: String = "",
    @Column(name="description")
    val description: String? = null,
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @ManyToOne
    @JoinColumn(name="created_by")
    var createdBy: UserEntity = UserEntity(),
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @ManyToOne
    @JoinColumn(name="modified_by")
    val modifiedBy: UserEntity? = null,
)