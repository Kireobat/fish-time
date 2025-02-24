package eu.kireobat.fishtime.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime

@Schema(description = "Generic response")
data class FishTimeResponseDto(
    @Schema(description = "was request successful", example = "false")
    var success: Boolean,
    @Schema(description = "timestamp UTC", example = "2025-01-01T00:00:00Z")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss.SSSX")
    var timestamp: ZonedDateTime,
    @Schema(description = "HTTP status of response", example = "401")
    var status: HttpStatus,
    @Schema(description = "message to give additional info", example = "You must be logged in for this action")
    var message: String
)
