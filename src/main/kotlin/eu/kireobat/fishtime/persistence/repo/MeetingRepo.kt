package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
interface MeetingRepo: JpaRepository<MeetingEntity, String> {

    @Query(
        value = """
        SELECT * FROM fish_time.meetings m 
        WHERE 
            (m.id = :id OR :id IS NULL) AND 
            (
                (LOWER(m.title) LIKE COALESCE(CONCAT('%', LOWER(:searchQuery), '%'), '%') OR 
                (LOWER(m.description) LIKE COALESCE(CONCAT('%', LOWER(:searchQuery), '%'), '%')
            ) AND
            (m.room_id = :roomId OR :roomId IS NULL) AND
            (m.start_time >= :startTime OR :startTime IS NULL) AND
            (m.end_time <= :endTime OR :endTime IS NULL) AND
            (m.created_by = :createdBy OR :createdBy IS NULL) AND
            (EXISTS (SELECT 1 FROM fish_time.participants p WHERE p.meeting_id = m.id AND p.user_id IN :participants) OR :participants IS NULL)
    """,
        countQuery = """
        SELECT COUNT(*) FROM fish_time.meetings m 
        WHERE 
            (m.id = :id OR :id IS NULL) AND 
            (
                (LOWER(m.title) LIKE COALESCE(CONCAT('%', LOWER(:searchQuery), '%'), '%') OR 
                (LOWER(m.description) LIKE COALESCE(CONCAT('%', LOWER(:searchQuery), '%'), '%')
            ) AND
            (m.room_id = :roomId OR :roomId IS NULL) AND
            (m.start_time >= :startTime OR :startTime IS NULL) AND
            (m.end_time <= :endTime OR :endTime IS NULL) AND
            (m.created_by = :createdBy OR :createdBy IS NULL) AND
            (EXISTS (SELECT 1 FROM fish_time.participants p WHERE p.meeting_id = m.id AND p.user_id IN :participants) OR :participants IS NULL)
    """,
        nativeQuery = true
    )
    fun findMeetings(
        pageable: Pageable,
        @Param("id") id: Int?,
        @Param("searchQuery") searchQuery: String?,
        @Param("startTime") startTime: ZonedDateTime?,
        @Param("endTime") endTime: ZonedDateTime?,
        @Param("roomId") roomId: Int?,
        @Param("createdBy") createdBy: Int?,
        @Param("participants") participants: List<Int>?
    ): Page<MeetingEntity>
}