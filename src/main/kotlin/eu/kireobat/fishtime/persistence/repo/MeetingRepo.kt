package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface MeetingRepo: JpaRepository<MeetingEntity, String>, JpaSpecificationExecutor<MeetingEntity> {
    fun deleteAllByCreatedById(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE MeetingEntity e SET e.createdBy = :newCreatedBy WHERE e.createdBy = :oldCreatedBy")
    fun updateCreatedByForMeetings(oldCreatedBy: UserEntity, newCreatedBy: UserEntity)
}