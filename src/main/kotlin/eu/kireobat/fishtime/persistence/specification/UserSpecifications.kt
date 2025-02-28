package eu.kireobat.fishtime.persistence.spesification

import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.data.jpa.domain.Specification

object UserSpecifications {

    fun withId(id: Int?): Specification<UserEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (id == null) {
                return@Specification null
            }
            criteriaBuilder.equal(root.get<Int>("id"), id)
        }
    }

    fun withSearchQuery(searchQuery: String?): Specification<UserEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (searchQuery.isNullOrBlank()) {
                return@Specification null
            }
            val pattern = "%${searchQuery.lowercase()}%"
            val usernameLower = criteriaBuilder.lower(root.get("username"))
            val emailLower = criteriaBuilder.lower(root.get("email"))

            criteriaBuilder.or(
                criteriaBuilder.like(usernameLower, pattern),
                criteriaBuilder.like(emailLower, pattern)
            )
        }
    }
}