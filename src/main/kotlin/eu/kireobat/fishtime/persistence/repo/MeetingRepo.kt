package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
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
    @Query("UPDATE MeetingEntity r SET r.createdBy = :newCreatedBy WHERE r.createdBy = :oldCreatedBy")
    fun updateCreatedByForMeetings(oldCreatedBy: Int, newCreatedBy: Int)
}