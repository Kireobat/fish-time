package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipantRepo: JpaRepository<ParticipantEntity, String> {
}