package rounderall.security.application.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import rounderall.security.domain.common.UserId
import rounderall.security.domain.jwt.*
import java.security.Key
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val secret: String,

    @Value("\${jwt.expiration}")
    private val expiration: Long,

    @Value("\${jwt.refresh-expiration}")
    private val refreshExpiration: Long
) {

    private val key: Key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(
        userId: UserId,
        username: String,
        roles: List<String>,
        permissions: List<String>
    ): Token {
        val now = Date()
        val accessTokenExpiry = Date(now.time + expiration)
        val refreshTokenExpiry = Date(now.time + refreshExpiration)

        val accessToken = Jwts.builder()
            .setSubject(username)
            .claim("userId", userId.value)
            .claim("roles", roles)
            .claim("permissions", permissions)
            .setIssuedAt(now)
            .setExpiration(accessTokenExpiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        val refreshToken = Jwts.builder()
            .setSubject(username)
            .claim("userId", userId.value)
            .claim("type", "refresh")
            .setIssuedAt(now)
            .setExpiration(refreshTokenExpiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return Token(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresIn = expiration / 1000,
            refreshExpiresIn = refreshExpiration / 1000,
            userId = userId,
            username = username,
            roles = roles,
            permissions = permissions
        )
    }

    fun validateToken(token: String): TokenValidationResult {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body

            val userId = claims["userId"] as? Long
            val username = claims.subject
            val roles = claims["roles"] as? List<String> ?: emptyList()
            val permissions = claims["permissions"] as? List<String> ?: emptyList()

            TokenValidationResult(
                isValid = true,
                userId = userId?.let { UserId(it) },
                username = username,
                roles = roles,
                permissions = permissions
            )
        } catch (e: JwtException) {
            TokenValidationResult(
                isValid = false,
                errorMessage = e.message
            )
        }
    }

    fun validateRefreshToken(token: String): TokenValidationResult {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body

            val tokenType = claims["type"] as? String
            if (tokenType != "refresh") {
                return TokenValidationResult(
                    isValid = false,
                    errorMessage = "Invalid token type"
                )
            }

            val userId = claims["userId"] as? Long
            val username = claims.subject

            TokenValidationResult(
                isValid = true,
                userId = userId?.let { UserId(it) },
                username = username
            )
        } catch (e: JwtException) {
            TokenValidationResult(
                isValid = false,
                errorMessage = e.message
            )
        }
    }

    fun extractUsername(token: String): String? {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (e: JwtException) {
            null
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .body
            claims.expiration.before(Date())
        } catch (e: JwtException) {
            true
        }
    }
}
