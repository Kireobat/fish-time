package eu.kireobat.fishtime.persistence.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name="users_map_roles")
data class UserMapRoleEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "meetingsSeq")
    @SequenceGenerator(name = "meetingsSeq", sequenceName = "meetings_seq", allocationSize = 1)
    @Column(name="id")
    val id: Int = 0,
    @ManyToOne
    @JoinColumn(name="user_id")
    val room: UserEntity = UserEntity(),
    @ManyToOne
    @JoinColumn(name="role_id")
    val role: RoleEntity = RoleEntity(),
    @Column(name="created_time")
    val createdTime: ZonedDateTime = ZonedDateTime.now()
)