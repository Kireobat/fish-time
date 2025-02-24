package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepo: JpaRepository<UserEntity, String> {
}