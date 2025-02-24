package eu.kireobat.fishtime.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import java.util.*

class CustomSvelteKitAuthSuccessHandler: SimpleUrlAuthenticationSuccessHandler() {

    @Value("\${environment.frontend.path}")
    lateinit var frontendPath: String
    @Value("\${environment.api.path}")
    lateinit var apiPath: String

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String {
        try {
            val originalState = request.session.getAttribute("preferredRedirect") ?: return frontendPath

            originalState as String

            if (originalState == "${apiPath}/fish-time/swagger-ui/index.html") {
                return originalState
            }

            val paddedState = originalState.padEnd(originalState.length + (4 - originalState.length % 4) % 4, '=')
            val standardBase64 = paddedState
                .replace("-", "+")
                .replace("_", "/")

            val decodedBytes = Base64.getDecoder().decode(standardBase64)
            val decodedState = JSONObject(String(decodedBytes))
            return validateRedirectUri(decodedState.getString("redirectUri"))
        } catch (e: IllegalArgumentException) {
            logger.error("Unable to decode stateParam: ${request.getParameter("state")} Error message: ${e.message}")
            return "${frontendPath}?error=invalid_state"
        }
    }

    private fun validateRedirectUri(uri: String): String {
        return if (uri.startsWith(frontendPath)) {
            uri
        } else {
            logger.warn("Unauthorized redirect uri: $uri")
            "${frontendPath}?error=invalid_redirect"
        }
    }
}
