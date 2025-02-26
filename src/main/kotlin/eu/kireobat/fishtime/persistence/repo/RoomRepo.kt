package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.RoomEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomRepo: JpaRepository<RoomEntity, String> {
    fun findByName(name: String): Optional<RoomEntity>
    fun deleteAllByCreatedById(id: Int)

    @Modifying
    @Transactional
    @Query("UPDATE RoomEntity r SET r.createdBy = :newCreatedBy WHERE r.createdBy = :oldCreatedBy")
    fun updateCreatedByForRooms(oldCreatedBy: Int, newCreatedBy: Int)

    @Query(
        value = """
        SELECT * FROM fish_time.rooms r 
        WHERE 
            (r.id = :id OR :id IS NULL) AND 
            (LOWER(r.name) LIKE COALESCE(CONCAT('%', LOWER(:name), '%'), '%')) AND 
            (r.capacity >= :minCapacity OR :minCapacity IS NULL) AND 
            (LOWER(r.address) LIKE COALESCE(CONCAT('%', LOWER(:address), '%'), '%')) AND 
            (:activeOnly = false OR r.active = :activeOnly)
    """,
        countQuery = """
        SELECT COUNT(*) FROM fish_time.rooms r 
        WHERE 
            (r.id = :id OR :id IS NULL) AND 
            (LOWER(r.name) LIKE COALESCE(CONCAT('%', LOWER(:name), '%'), '%')) AND 
            (r.capacity >= :minCapacity OR :minCapacity IS NULL) AND 
            (LOWER(r.address) LIKE COALESCE(CONCAT('%', LOWER(:address), '%'), '%')) AND 
            (:activeOnly = false OR r.active = :activeOnly)
    """,
        nativeQuery = true
    )
    fun findRooms(
        pageable: Pageable,
        @Param("id") id: Int?,
        @Param("name") name: String?,
        @Param("minCapacity") minCapacity: Int?,
        @Param("address") address: String?,
        @Param("activeOnly") activeOnly: Boolean
    ): Page<RoomEntity>
}