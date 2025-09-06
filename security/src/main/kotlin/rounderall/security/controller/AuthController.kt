package rounderall.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rounderall.security.application.service.AuthenticationService
import rounderall.security.domain.jwt.TokenRequest
import rounderall.security.domain.jwt.RefreshTokenRequest

@RestController
@RequestMapping("/api/security/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {
    
    @PostMapping("/login")
    fun login(@RequestBody request: TokenRequest): ResponseEntity<Any> {
        return try {
            val token = authenticationService.authenticate(request)
            ResponseEntity.ok(token)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<Any> {
        return try {
            val token = authenticationService.refreshToken(request)
            ResponseEntity.ok(token)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @PostMapping("/validate")
    fun validateToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<Any> {
        val token = authHeader.substring(7) // "Bearer " 제거
        val result = authenticationService.validateToken(token)
        
        return if (result.isValid) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.badRequest().body(mapOf("error" to result.errorMessage))
        }
    }
}
