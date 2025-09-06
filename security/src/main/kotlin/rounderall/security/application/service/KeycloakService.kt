package rounderall.security.application.service

import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.UserRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import rounderall.security.domain.rbac.User
import rounderall.security.domain.rbac.Role
import rounderall.security.domain.rbac.Permission
import rounderall.security.domain.jwt.Token
import rounderall.security.domain.jwt.TokenRequest
import rounderall.security.domain.common.UserId
import java.util.*

@Service
class KeycloakService(
    @Value("\${keycloak.server-url}")
    private val serverUrl: String,
    
    @Value("\${keycloak.realm}")
    private val realm: String,
    
    @Value("\${keycloak.client-id}")
    private val clientId: String,
    
    @Value("\${keycloak.client-secret}")
    private val clientSecret: String,
    
    @Value("\${keycloak.admin.username}")
    private val adminUsername: String,
    
    @Value("\${keycloak.admin.password}")
    private val adminPassword: String,
    
    private val userService: UserService,
    private val jwtService: JwtService
) {
    
    private fun getKeycloakAdminClient(): Keycloak {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")
            .username(adminUsername)
            .password(adminPassword)
            .clientId("admin-cli")
            .build()
    }
    
    fun syncUserFromKeycloak(keycloakId: String): User? {
        val keycloak = getKeycloakAdminClient()
        val realmResource = keycloak.realm(realm)
        
        return try {
            val userRepresentation = realmResource.users().get(keycloakId).toRepresentation()
            
            // 로컬 DB에서 사용자 찾기 또는 생성
            var user = userService.findByKeycloakId(keycloakId)
            
            if (user == null) {
                user = userService.createUser(
                    username = userRepresentation.username ?: userRepresentation.email!!,
                    email = userRepresentation.email!!,
                    password = null, // Keycloak에서 관리
                    firstName = userRepresentation.firstName,
                    lastName = userRepresentation.lastName,
                    keycloakId = keycloakId
                )
            }
            
            // Keycloak 역할을 로컬 역할과 동기화
            syncUserRoles(user, realmResource, keycloakId)
            
            user
        } catch (e: Exception) {
            null
        }
    }
    
    private fun syncUserRoles(user: User, realmResource: Any, keycloakId: String) {
        // Keycloak 역할 동기화 로직
        // 실제 구현에서는 Keycloak API를 사용하여 역할 정보를 가져와서
        // 로컬 DB의 역할과 매핑
    }
    
    fun authenticateWithKeycloak(request: TokenRequest): Token? {
        // Keycloak을 통한 인증
        // 실제 구현에서는 Keycloak의 토큰 엔드포인트를 호출
        return null
    }
    
    fun createUserInKeycloak(user: User, password: String): String? {
        val keycloak = getKeycloakAdminClient()
        val realmResource = keycloak.realm(realm)
        
        val userRepresentation = UserRepresentation()
        userRepresentation.username = user.username
        userRepresentation.email = user.email
        userRepresentation.firstName = user.firstName
        userRepresentation.lastName = user.lastName
        userRepresentation.isEnabled = user.isActive
        
        return try {
            val response = realmResource.users().create(userRepresentation)
            val userId = response.location?.path?.substringAfterLast("/")
            userId
        } catch (e: Exception) {
            null
        }
    }
}
