package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="users_map_roles")
data class UserMapRoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usersMapRolesSeq")
    @SequenceGenerator(name = "usersMapRolesSeq", sequenceName = "users_map_roles_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @ManyToOne
    @JoinColumn(name="user_id")
    val user: UserEntity = UserEntity(),
    @ManyToOne
    @JoinColumn(name="role_id")
    val role: RoleEntity = RoleEntity(),
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now(),
    @Column(name="created_by")
    var createdBy: Int? = null,
    @Column(name="modified_time")
    val modifiedTime: ZonedDateTime? = null,
    @Column(name="modified_by")
    val modifiedBy: Int? = null,
)