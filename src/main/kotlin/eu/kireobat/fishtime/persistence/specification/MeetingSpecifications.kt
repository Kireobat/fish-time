package eu.kireobat.fishtime.persistence.spesification

import eu.kireobat.fishtime.persistence.entity.MeetingEntity
import eu.kireobat.fishtime.persistence.entity.ParticipantEntity
import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.data.jpa.domain.Specification
import java.time.ZonedDateTime

object MeetingSpecifications {

    fun withId(id: Int?): Specification<MeetingEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (id == null) {
                return@Specification null
            }
            criteriaBuilder.equal(root.get<Int>("id"), id)
        }
    }

    fun withSearchQuery(searchQuery: String?): Specification<MeetingEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (searchQuery.isNullOrBlank()) {
                return@Specification null
            }
            val pattern = "%${searchQuery.lowercase()}%"
            val titleLower = criteriaBuilder.lower(root.get("title"))
            val descriptionLower = criteriaBuilder.lower(root.get("description"))

            criteriaBuilder.or(
                criteriaBuilder.like(titleLower, pattern),
                criteriaBuilder.like(descriptionLower, pattern)
            )
        }
    }

    fun withRoomId(roomId: Int?): Specification<MeetingEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (roomId == null) {
                return@Specification null
            }
            criteriaBuilder.equal(root.get<Any>("room").get<Int>("id"), roomId)
        }
    }

    fun withTimeRange(startTime: ZonedDateTime?, endTime: ZonedDateTime?): Specification<MeetingEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (startTime == null || endTime == null) {
                return@Specification null
            }
            // Meeting overlaps with the range if:
            // 1. The meeting starts before the range ends AND
            // 2. The meeting ends after the range starts
            criteriaBuilder.and(
                criteriaBuilder.lessThan(root.get("startTime"), endTime),
                criteriaBuilder.greaterThan(root.get("endTime"), startTime)
            )
        }
    }

    fun withCreatedBy(createdBy: Int?): Specification<MeetingEntity> {
        return Specification { root, _, criteriaBuilder ->
            if (createdBy == null) {
                return@Specification null
            }
            criteriaBuilder.equal(root.get<Int>("createdBy"), createdBy)
        }
    }

    fun withParticipants(participants: List<Int>?): Specification<MeetingEntity> {
        return Specification { root, query, criteriaBuilder ->
            if (participants.isNullOrEmpty()) {
                return@Specification null
            }

            // We need to handle duplicates in count queries
            query!!.distinct(true)

            val subquery = query.subquery(Int::class.java)
            val participantRoot = subquery.from(ParticipantEntity::class.java)

            subquery.select(participantRoot.get<MeetingEntity>("meeting").get("id"))
                .where(
                    criteriaBuilder.equal(
                        participantRoot.get<MeetingEntity>("meeting").get<Int>("id"),
                        root.get<Int>("id")
                    ),
                    participantRoot.get<UserEntity>("user").get<Int>("id").`in`(participants)
                )

            criteriaBuilder.exists(subquery)
        }
    }
}