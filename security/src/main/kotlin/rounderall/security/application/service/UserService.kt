package rounderall.security.application.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rounderall.security.domain.rbac.User
import rounderall.security.domain.rbac.Role
import rounderall.security.domain.rbac.Permission
import rounderall.security.infrastructure.repository.UserRepository
import rounderall.security.infrastructure.repository.RoleRepository
import rounderall.security.infrastructure.repository.PermissionRepository

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val passwordEncoder: PasswordEncoder
) {
    
    fun createUser(
        username: String,
        email: String,
        password: String?,
        firstName: String? = null,
        lastName: String? = null,
        keycloakId: String? = null
    ): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists: $username")
        }
        
        if (userRepository.existsByEmail(email)) {
            throw IllegalArgumentException("Email already exists: $email")
        }
        
        val passwordHash = password?.let { passwordEncoder.encode(it) }
        
        val user = User(
            username = username,
            email = email,
            passwordHash = passwordHash,
            firstName = firstName,
            lastName = lastName,
            keycloakId = keycloakId
        )
        
        return userRepository.save(user)
    }
    
    @Transactional(readOnly = true)
    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }
    
    @Transactional(readOnly = true)
    fun findByUsernameWithRoles(username: String): User? {
        return userRepository.findByUsernameWithRoles(username)
    }
    
    @Transactional(readOnly = true)
    fun findByUsernameWithRolesAndPermissions(username: String): User? {
        return userRepository.findByUsernameWithRolesAndPermissions(username)
    }
    
    @Transactional(readOnly = true)
    fun findByKeycloakId(keycloakId: String): User? {
        return userRepository.findByKeycloakId(keycloakId)
    }
    
    fun assignRoleToUser(username: String, roleName: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found: $username")
        
        val role = roleRepository.findByName(roleName)
            ?: throw IllegalArgumentException("Role not found: $roleName")
        
        user.addRole(role)
        userRepository.save(user)
    }
    
    fun removeRoleFromUser(username: String, roleName: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found: $username")
        
        val role = roleRepository.findByName(roleName)
            ?: throw IllegalArgumentException("Role not found: $roleName")
        
        user.removeRole(role)
        userRepository.save(user)
    }
    
    fun assignPermissionToUser(username: String, permissionName: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found: $username")
        
        val permission = permissionRepository.findByName(permissionName)
            ?: throw IllegalArgumentException("Permission not found: $permissionName")
        
        user.addPermission(permission)
        userRepository.save(user)
    }
    
    fun removePermissionFromUser(username: String, permissionName: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found: $username")
        
        val permission = permissionRepository.findByName(permissionName)
            ?: throw IllegalArgumentException("Permission not found: $permissionName")
        
        user.removePermission(permission)
        userRepository.save(user)
    }
}
