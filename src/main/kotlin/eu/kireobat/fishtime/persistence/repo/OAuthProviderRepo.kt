package eu.kireobat.fishtime.persistence.repo

import eu.kireobat.fishtime.persistence.entity.OAuthProviderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OAuthProviderRepo: JpaRepository<OAuthProviderEntity, String>