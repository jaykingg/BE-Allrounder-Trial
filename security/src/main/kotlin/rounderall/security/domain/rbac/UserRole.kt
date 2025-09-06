package rounderall.security.domain.rbac

import jakarta.persistence.*

@Entity
@Table(name = "user_roles", uniqueConstraints = [
    UniqueConstraint(columnNames = ["user_id", "role_id"])
])
class UserRole(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    val role: Role
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}