package eu.kireobat.fishtime.config.security

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest

class CustomAuthorizationRequestResolver(
    private val clientRegistrationRepository: ClientRegistrationRepository,
    authorizationRequestBaseUri: String = "/oauth2/authorization"
) : OAuth2AuthorizationRequestResolver {
    private val defaultResolver = DefaultOAuth2AuthorizationRequestResolver(
        clientRegistrationRepository,
        authorizationRequestBaseUri
    )

    override fun resolve(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val preferredRedirect = request.getParameter("preferredRedirect")
        if (request.session.getAttribute("preferredRedirect") == null) {
            request.session.setAttribute("preferredRedirect", preferredRedirect)
        }

        val authRequest = defaultResolver.resolve(request)
        return authRequest?.let {
            OAuth2AuthorizationRequest.from(it)
                .additionalParameters(mapOf("custom_state" to preferredRedirect))
                .build()
        }
    }

    override fun resolve(request: HttpServletRequest, clientRegistrationId: String): OAuth2AuthorizationRequest? {
        return defaultResolver.resolve(request, clientRegistrationId)?.let {
            OAuth2AuthorizationRequest.from(it)
                .additionalParameters(mapOf("custom_state" to request.getParameter("preferredRedirect")))
                .build()
        }
    }
}