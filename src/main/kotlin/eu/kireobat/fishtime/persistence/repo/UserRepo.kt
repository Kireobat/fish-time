package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepo: JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {
    fun findByEmail(email: String): Optional<UserEntity>
    fun findByUsername(username: String): Optional<UserEntity>
    fun findByOauthId(oauthId: Int): Optional<UserEntity>
}