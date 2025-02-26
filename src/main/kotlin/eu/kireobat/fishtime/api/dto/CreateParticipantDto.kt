package eu.kireobat.fishtime.api.dto

data class CreateParticipantDto(
    val userId: Int,
    val meetingId: Int,
    val status: String,
)
