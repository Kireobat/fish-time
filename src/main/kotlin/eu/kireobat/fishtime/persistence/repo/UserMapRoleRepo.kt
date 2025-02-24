package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.UserMapRoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserMapRoleRepo: JpaRepository<UserMapRoleEntity, String> {
}