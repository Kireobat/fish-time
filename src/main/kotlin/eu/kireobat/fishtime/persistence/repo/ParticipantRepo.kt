package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepo: JpaRepository<ParticipantEntity, String> {
    fun deleteByUserIdAndMeetingId(userId: Int, meetingId: Int)
    fun deleteAllByMeetingId(meetingId: Int)
    fun deleteAllByCreatedById(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE ParticipantEntity r SET r.createdBy = :newCreatedBy WHERE r.createdBy = :oldCreatedBy")
    fun updateCreatedByForParticipants(oldCreatedBy: Int, newCreatedBy: Int)
}