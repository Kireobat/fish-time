package eu.kireobat.fishtime.config.security

import eu.kireobat.fishtime.service.CustomOAuth2UserService
import eu.kireobat.fishtime.service.CustomUserDetailsService
import eu.kireobat.fishtime.service.UserService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val customOAuth2UserService: CustomOAuth2UserService
) {

    @Value("\${environment.frontend.path}")
    lateinit var frontendPath: String
    @Value("\${environment.api.path}")
    lateinit var apiPath: String

    @Bean
    fun securityFilterChain(http: HttpSecurity,
                            clientRegistrationRepository: ClientRegistrationRepository): SecurityFilterChain {
        http {
            authorizeHttpRequests {
                authorize(anyRequest, permitAll)
            }
            oauth2Login {
                authorizationEndpoint {
                    authorizationRequestResolver = CustomAuthorizationRequestResolver(
                        clientRegistrationRepository = clientRegistrationRepository,
                        authorizationRequestBaseUri = "/oauth2/authorization"
                    )
                }
                userInfoEndpoint {
                    userService = customOAuth2UserService
                }
                authenticationSuccessHandler = svelteKitAuthSuccessHandler()
            }
            cors {
                configurationSource = corsConfigurationSource()
            }
            csrf {
                disable()
            }
            sessionManagement {
                sessionFixation { changeSessionId() }
                sessionCreationPolicy = SessionCreationPolicy.ALWAYS // Force session creation
            }

            logout {
                logoutSuccessUrl = frontendPath
                deleteCookies("JSESSIONID")
                invalidateHttpSession = true
            }
            exceptionHandling {
                authenticationEntryPoint = CustomAuthenticationEntryPoint()
            }
        }
        return http.build()
    }
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration().apply {
            allowedOrigins = listOf(apiPath, frontendPath, "https://github.com")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
            allowCredentials = true
        }
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun svelteKitAuthSuccessHandler(): AuthenticationSuccessHandler {
        return CustomSvelteKitAuthSuccessHandler()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(
        http: HttpSecurity,
        passwordEncoder: PasswordEncoder,
        customUserDetailsService: CustomUserDetailsService
    ): AuthenticationManager {
        val authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        authManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder)
        return authManagerBuilder.build()
    }

}