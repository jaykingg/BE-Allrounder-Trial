package rounderall.security.domain.rbac

import jakarta.persistence.*
import rounderall.security.domain.common.BaseEntity
import rounderall.security.domain.common.UserId

@Entity
@Table(name = "users")
class User(
    @Column(name = "username", nullable = false, unique = true, length = 50)
    val username: String,
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    val email: String,
    
    @Column(name = "password_hash", length = 255)
    val passwordHash: String? = null,
    
    @Column(name = "first_name", length = 50)
    val firstName: String? = null,
    
    @Column(name = "last_name", length = 50)
    val lastName: String? = null,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,
    
    @Column(name = "keycloak_id", unique = true, length = 100)
    val keycloakId: String? = null,
    
    @Column(name = "last_login_at")
    val lastLoginAt: java.time.LocalDateTime? = null
) : BaseEntity() {
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userRoles: MutableSet<UserRole> = mutableSetOf()
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userPermissions: MutableSet<UserPermission> = mutableSetOf()
    
    fun getId(): UserId = UserId(id)
    
    fun getRoles(): Set<Role> = userRoles.map { it.role }.toSet()
    
    fun getPermissions(): Set<Permission> = userPermissions.map { it.permission }.toSet()
    
    fun addRole(role: Role) {
        if (userRoles.none { it.role.id == role.id }) {
            userRoles.add(UserRole(this, role))
        }
    }
    
    fun removeRole(role: Role) {
        userRoles.removeIf { it.role.id == role.id }
    }
    
    fun addPermission(permission: Permission) {
        if (userPermissions.none { it.permission.id == permission.id }) {
            userPermissions.add(UserPermission(this, permission))
        }
    }
    
    fun removePermission(permission: Permission) {
        userPermissions.removeIf { it.permission.id == permission.id }
    }
    
    fun hasRole(roleName: String): Boolean {
        return getRoles().any { it.name == roleName }
    }
    
    fun hasPermission(permissionName: String): Boolean {
        return getPermissions().any { it.name == permissionName } ||
               getRoles().flatMap { it.getPermissions() }.any { it.name == permissionName }
    }
    
    fun updateLastLogin() {
        // JPA 엔티티이므로 실제로는 서비스에서 처리
    }
}