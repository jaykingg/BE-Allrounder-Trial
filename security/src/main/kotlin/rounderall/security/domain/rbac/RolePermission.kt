package rounderall.security.domain.rbac

import jakarta.persistence.*

@Entity
@Table(name = "role_permissions", uniqueConstraints = [
    UniqueConstraint(columnNames = ["role_id", "permission_id"])
])
class RolePermission(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}