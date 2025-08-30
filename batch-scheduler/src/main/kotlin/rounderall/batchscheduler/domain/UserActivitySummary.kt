package rounderall.batchscheduler.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "user_activity_summaries")
class UserActivitySummary(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false)
    val summaryDate: Instant,

    @Column(nullable = false)
    val totalActivities: Int,

    @Column(nullable = false)
    val uniqueActivityTypes: Int,

    @Column(nullable = false)
    val lastActivityTime: String
) {
    companion object {
        fun create(
            userId: String,
            summaryDate: Instant,
            totalActivities: Int,
            uniqueActivityTypes: Int,
            lastActivityTime: String
        ): UserActivitySummary {
            return UserActivitySummary(
                userId = userId,
                summaryDate = summaryDate,
                totalActivities = totalActivities,
                uniqueActivityTypes = uniqueActivityTypes,
                lastActivityTime = lastActivityTime
            )
        }
    }
}
