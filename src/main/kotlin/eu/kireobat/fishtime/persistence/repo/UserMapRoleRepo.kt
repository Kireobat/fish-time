package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.entity.UserMapRoleEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserMapRoleRepo: JpaRepository<UserMapRoleEntity, String> {
    fun findAllByUserId(userId: Int): List<UserMapRoleEntity>
    fun findAllByCreatedBy(id: Int): List<UserMapRoleEntity>
    @Transactional
    fun deleteAllByCreatedBy(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE UserMapRoleEntity e SET e.createdBy = :newCreatedBy WHERE e.createdBy = :oldCreatedBy")
    fun updateCreatedByForMappings(oldCreatedBy: Int, newCreatedBy: Int)
}