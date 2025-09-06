package rounderall.security.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.navercorp.fixturemonkey.FixtureMonkey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import rounderall.security.application.service.AuthenticationService
import rounderall.security.domain.jwt.Token
import rounderall.security.domain.jwt.TokenRequest
import rounderall.security.domain.jwt.RefreshTokenRequest
import rounderall.security.domain.jwt.TokenValidationResult
import rounderall.security.domain.common.UserId
import rounderall.security.infrastructure.config.FixtureMonkeyConfig

@ActiveProfiles("test")
@WebMvcTest(AuthController::class)
class AuthControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val fixtureMonkey: FixtureMonkey
) : BehaviorSpec({
    
    val authenticationService = mockk<AuthenticationService>()
    
    given("로그인") {
        `when`("유효한 자격증명으로 로그인할 때") {
            then("토큰이 반환되어야 한다") {
                // Given
                val request = TokenRequest("testuser", "password123")
                val token = Token(
                    accessToken = "access_token",
                    refreshToken = "refresh_token",
                    expiresIn = 3600,
                    refreshExpiresIn = 7200,
                    userId = UserId(1L),
                    username = "testuser",
                    roles = listOf("USER"),
                    permissions = listOf("user:read")
                )
                
                every { authenticationService.authenticate(any()) } returns token
                
                // When & Then
                mockMvc.perform(
                    post("/api/security/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.accessToken").value("access_token"))
                    .andExpect(jsonPath("$.username").value("testuser"))
            }
        }
        
        `when`("유효하지 않은 자격증명으로 로그인할 때") {
            then("에러가 반환되어야 한다") {
                // Given
                val request = TokenRequest("invaliduser", "wrongpassword")
                
                every { authenticationService.authenticate(any()) } throws IllegalArgumentException("Invalid credentials")
                
                // When & Then
                mockMvc.perform(
                    post("/api/security/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isBadRequest)
                    .andExpect(jsonPath("$.error").value("Invalid credentials"))
            }
        }
    }
    
    given("토큰 갱신") {
        `when`("유효한 리프레시 토큰으로 갱신할 때") {
            then("새로운 토큰이 반환되어야 한다") {
                // Given
                val request = RefreshTokenRequest("valid_refresh_token")
                val newToken = Token(
                    accessToken = "new_access_token",
                    refreshToken = "new_refresh_token",
                    expiresIn = 3600,
                    refreshExpiresIn = 7200,
                    userId = UserId(1L),
                    username = "testuser",
                    roles = listOf("USER"),
                    permissions = listOf("user:read")
                )
                
                every { authenticationService.refreshToken(any()) } returns newToken
                
                // When & Then
                mockMvc.perform(
                    post("/api/security/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.accessToken").value("new_access_token"))
            }
        }
    }
    
    given("토큰 검증") {
        `when`("유효한 토큰을 검증할 때") {
            then("검증 결과가 반환되어야 한다") {
                // Given
                val validationResult = TokenValidationResult(
                    isValid = true,
                    userId = UserId(1L),
                    username = "testuser",
                    roles = listOf("USER"),
                    permissions = listOf("user:read")
                )
                
                every { authenticationService.validateToken(any()) } returns validationResult
                
                // When & Then
                mockMvc.perform(
                    post("/api/security/auth/validate")
                        .header("Authorization", "Bearer valid_token")
                )
                    .andExpect(status().isOk)
                    .andExpect(jsonPath("$.isValid").value(true))
                    .andExpect(jsonPath("$.username").value("testuser"))
            }
        }
    }
})

@TestConfiguration
class TestConfig {
    
    @Bean
    @Primary
    fun fixtureMonkey(): FixtureMonkey {
        return FixtureMonkey.builder()
            .plugin(com.navercorp.fixturemonkey.kotlin.KotlinPlugin())
            .build()
    }
}
