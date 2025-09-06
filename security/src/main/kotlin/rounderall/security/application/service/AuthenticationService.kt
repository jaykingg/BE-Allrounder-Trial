package rounderall.security.application.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import rounderall.security.domain.jwt.*
import rounderall.security.domain.common.UserId

@Service
class AuthenticationService(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun authenticate(request: TokenRequest): Token {
        val user = userService.findByUsernameWithRolesAndPermissions(request.username)
            ?: throw IllegalArgumentException("User not found: ${request.username}")
        
        if (!user.isActive) {
            throw IllegalArgumentException("User is inactive: ${request.username}")
        }
        
        // 비밀번호 검증 (Keycloak 사용 시에는 생략)
        if (user.passwordHash != null && request.password != null) {
            if (!passwordEncoder.matches(request.password, user.passwordHash)) {
                throw IllegalArgumentException("Invalid password")
            }
        }
        
        val roles = user.getRoles().map { it.name }
        val permissions = user.getPermissions().map { it.name }
        
        return jwtService.generateToken(
            userId = user.getId(),
            username = user.username,
            roles = roles,
            permissions = permissions
        )
    }
    
    fun refreshToken(request: RefreshTokenRequest): Token {
        val validationResult = jwtService.validateRefreshToken(request.refreshToken)
        
        if (!validationResult.isValid) {
            throw IllegalArgumentException("Invalid refresh token: ${validationResult.errorMessage}")
        }
        
        val user = userService.findByUsernameWithRolesAndPermissions(validationResult.username!!)
            ?: throw IllegalArgumentException("User not found: ${validationResult.username}")
        
        if (!user.isActive) {
            throw IllegalArgumentException("User is inactive: ${validationResult.username}")
        }
        
        val roles = user.getRoles().map { it.name }
        val permissions = user.getPermissions().map { it.name }
        
        return jwtService.generateToken(
            userId = user.getId(),
            username = user.username,
            roles = roles,
            permissions = permissions
        )
    }
    
    fun validateToken(token: String): TokenValidationResult {
        return jwtService.validateToken(token)
    }
}
