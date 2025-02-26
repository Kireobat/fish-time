package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.RoleEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RoleRepo: JpaRepository<RoleEntity, String> {
    fun deleteAllByCreatedById(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE RoleEntity r SET r.createdBy = :newCreatedBy WHERE r.createdBy = :oldCreatedBy")
    fun updateCreatedByForRoles(oldCreatedBy: UserEntity, newCreatedBy: UserEntity)
}