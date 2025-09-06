package rounderall.security.domain.jwt

import rounderall.security.domain.common.UserId
import java.time.LocalDateTime

/**
 * JWT 토큰을 나타내는 도메인 모델
 */
data class Token(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long,
    val refreshExpiresIn: Long,
    val userId: UserId,
    val username: String,
    val roles: List<String>,
    val permissions: List<String>,
    val issuedAt: LocalDateTime = LocalDateTime.now()
)

/**
 * 토큰 요청을 나타내는 값 객체
 */
data class TokenRequest(
    val username: String,
    val password: String? = null,
    val grantType: GrantType = GrantType.PASSWORD
)

/**
 * 토큰 갱신 요청을 나타내는 값 객체
 */
data class RefreshTokenRequest(
    val refreshToken: String
)

/**
 * 인증 타입 열거형
 */
enum class GrantType {
    PASSWORD,
    REFRESH_TOKEN,
    AUTHORIZATION_CODE
}

/**
 * 토큰 검증 결과
 */
data class TokenValidationResult(
    val isValid: Boolean,
    val userId: UserId? = null,
    val username: String? = null,
    val roles: List<String> = emptyList(),
    val permissions: List<String> = emptyList(),
    val errorMessage: String? = null
)
