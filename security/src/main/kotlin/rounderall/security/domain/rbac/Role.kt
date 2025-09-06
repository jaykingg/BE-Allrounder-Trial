package rounderall.security.domain.rbac

import jakarta.persistence.*
import rounderall.security.domain.common.BaseEntity
import rounderall.security.domain.common.RoleId

@Entity
@Table(name = "roles")
class Role(
    @Column(name = "name", nullable = false, unique = true, length = 50)
    val name: String,
    
    @Column(name = "description", length = 255)
    val description: String? = null,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true
) : BaseEntity() {
    
    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val rolePermissions: MutableSet<RolePermission> = mutableSetOf()
    
    @OneToMany(mappedBy = "role", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userRoles: MutableSet<UserRole> = mutableSetOf()
    
    fun getId(): RoleId = RoleId(id)
    
    fun getPermissions(): Set<Permission> = rolePermissions.map { it.permission }.toSet()
    
    fun addPermission(permission: Permission) {
        if (rolePermissions.none { it.permission.id == permission.id }) {
            rolePermissions.add(RolePermission(this, permission))
        }
    }
    
    fun removePermission(permission: Permission) {
        rolePermissions.removeIf { it.permission.id == permission.id }
    }
    
    fun hasPermission(permissionName: String): Boolean {
        return getPermissions().any { it.name == permissionName }
    }
}