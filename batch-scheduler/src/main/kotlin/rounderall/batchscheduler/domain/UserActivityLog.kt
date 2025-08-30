package rounderall.batchscheduler.domain

import jakarta.persistence.*
import rounderall.batchscheduler.domain.enums.ActivityType
import java.time.Instant

@Entity
@Table(name = "user_activity_logs")
class UserActivityLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val activityType: ActivityType,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now()
) {
    companion object {
        fun create(userId: String, activityType: ActivityType): UserActivityLog {
            return UserActivityLog(
                userId = userId,
                activityType = activityType,
                description = activityType.description,
            )
        }
    }
}
