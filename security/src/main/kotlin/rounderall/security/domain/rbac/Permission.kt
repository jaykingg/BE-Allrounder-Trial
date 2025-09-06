package rounderall.security.domain.rbac

import jakarta.persistence.*
import rounderall.security.domain.common.BaseEntity
import rounderall.security.domain.common.PermissionId
import rounderall.security.domain.common.ResourceId

@Entity
@Table(name = "permissions")
class Permission(
    @Column(name = "name", nullable = false, unique = true, length = 100)
    val name: String,
    
    @Column(name = "description", length = 255)
    val description: String? = null,
    
    @Column(name = "resource", length = 100)
    val resource: String? = null,
    
    @Column(name = "action", length = 50)
    val action: String? = null,
    
    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true
) : BaseEntity() {
    
    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val rolePermissions: MutableSet<RolePermission> = mutableSetOf()
    
    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userPermissions: MutableSet<UserPermission> = mutableSetOf()
    
    fun getId(): PermissionId = PermissionId(id)
    
    fun getResourceId(): ResourceId? = resource?.let { ResourceId(it) }
    
    fun matches(resource: String, action: String): Boolean {
        return (this.resource == null || this.resource == resource) &&
               (this.action == null || this.action == action)
    }
}
