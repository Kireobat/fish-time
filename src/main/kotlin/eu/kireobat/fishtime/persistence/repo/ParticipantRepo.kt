package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ParticipantRepo: JpaRepository<ParticipantEntity, String> {
    fun findByUserIdAndMeetingId(userId: Int, meetingId: Int): Optional<ParticipantEntity>
    fun deleteByUserIdAndMeetingId(userId: Int, meetingId: Int)
    fun deleteAllByMeetingId(meetingId: Int)
    fun deleteAllByCreatedById(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE ParticipantEntity e SET e.createdBy = :newCreatedBy WHERE e.createdBy = :oldCreatedBy")
    fun updateCreatedByForParticipants(oldCreatedBy: UserEntity, newCreatedBy: UserEntity)
}