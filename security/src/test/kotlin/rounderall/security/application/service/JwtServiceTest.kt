package rounderall.security.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import rounderall.security.domain.common.UserId

class JwtServiceTest : BehaviorSpec({
    
    val jwtService = JwtService(
        secret = "test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm",
        expiration = 3600000L,
        refreshExpiration = 7200000L
    )
    
    given("JWT 토큰 생성") {
        `when`("사용자 정보로 토큰을 생성할 때") {
            then("유효한 토큰이 생성되어야 한다") {
                // Given
                val userId = UserId(1L)
                val username = "testuser"
                val roles = listOf("USER", "ADMIN")
                val permissions = listOf("user:read", "user:write")
                
                // When
                val token = jwtService.generateToken(userId, username, roles, permissions)
                
                // Then
                token.accessToken shouldNotBe null
                token.refreshToken shouldNotBe null
                token.username shouldBe username
                token.roles shouldBe roles
                token.permissions shouldBe permissions
            }
        }
    }
    
    given("JWT 토큰 검증") {
        `when`("유효하지 않은 토큰을 검증할 때") {
            then("검증이 실패해야 한다") {
                // Given
                val invalidToken = "invalid.token.here"
                
                // When
                val result = jwtService.validateToken(invalidToken)
                
                // Then
                result.isValid shouldBe false
                result.errorMessage shouldNotBe null
            }
        }
    }
})