package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingRepo: JpaRepository<MeetingEntity, String> {
}