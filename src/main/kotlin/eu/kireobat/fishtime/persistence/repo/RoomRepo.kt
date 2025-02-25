package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.RoomEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoomRepo: JpaRepository<RoomEntity, String> {
    fun findByName(name: String): Optional<RoomEntity>

    @Query(
        value = """
        SELECT * FROM fish_time.rooms r 
        WHERE 
            (r.id = :id OR :id IS NULL) AND 
            (r.name LIKE COALESCE(CONCAT('%', :name, '%'), '%')) AND 
            (r.capacity >= :minCapacity OR :minCapacity IS NULL) AND 
            (r.address LIKE COALESCE(CONCAT('%', :address, '%'), '%')) AND 
            (:activeOnly = false OR r.active = :activeOnly)
    """,
        countQuery = """
        SELECT COUNT(*) FROM fish_time.rooms r 
        WHERE 
            (r.id = :id OR :id IS NULL) AND 
            (r.name LIKE COALESCE(CONCAT('%', :name, '%'), '%')) AND 
            (r.capacity >= :minCapacity OR :minCapacity IS NULL) AND 
            (r.address LIKE COALESCE(CONCAT('%', :address, '%'), '%')) AND 
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