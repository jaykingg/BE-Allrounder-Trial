package rounderall.security.application.service

import com.navercorp.fixturemonkey.FixtureMonkey
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import rounderall.security.domain.common.UserId
import rounderall.security.infrastructure.config.FixtureMonkeyConfig

@ActiveProfiles("test")
@SpringBootTest(classes = [FixtureMonkeyConfig::class])
@TestPropertySource(properties = [
    "jwt.secret=test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm",
    "jwt.expiration=3600000",
    "jwt.refresh-expiration=7200000"
])
class JwtServiceTest(
    private val fixtureMonkey: FixtureMonkey
) : BehaviorSpec({
    
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
        `when`("유효한 토큰을 검증할 때") {
            then("검증이 성공해야 한다") {
                // Given
                val userId = UserId(1L)
                val username = "testuser"
                val roles = listOf("USER")
                val permissions = listOf("user:read")
                
                val token = jwtService.generateToken(userId, username, roles, permissions)
                
                // When
                val result = jwtService.validateToken(token.accessToken)
                
                // Then
                result.isValid shouldBe true
                result.userId shouldBe userId
                result.username shouldBe username
                result.roles shouldBe roles
                result.permissions shouldBe permissions
            }
        }
        
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
    
    given("리프레시 토큰 검증") {
        `when`("유효한 리프레시 토큰을 검증할 때") {
            then("검증이 성공해야 한다") {
                // Given
                val userId = UserId(1L)
                val username = "testuser"
                val roles = listOf("USER")
                val permissions = listOf("user:read")
                
                val token = jwtService.generateToken(userId, username, roles, permissions)
                
                // When
                val result = jwtService.validateRefreshToken(token.refreshToken)
                
                // Then
                result.isValid shouldBe true
                result.userId shouldBe userId
                result.username shouldBe username
            }
        }
    }
})
