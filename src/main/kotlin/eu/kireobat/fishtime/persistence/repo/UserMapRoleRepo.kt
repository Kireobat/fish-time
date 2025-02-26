package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.UserMapRoleEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserMapRoleRepo: JpaRepository<UserMapRoleEntity, String> {
    fun findAllByUserId(userId: Int): List<UserMapRoleEntity>
    fun deleteAllByCreatedBy(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE MeetingEntity r SET r.createdBy = :newCreatedBy WHERE r.createdBy = :oldCreatedBy")
    fun updateCreatedByForMappings(oldCreatedBy: Int, newCreatedBy: Int)
}