package rounderall.security.domain.rbac

import jakarta.persistence.*

@Entity
@Table(name = "user_permissions", uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "permission_id"])
])
class UserPermission(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    val permission: Permission
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}