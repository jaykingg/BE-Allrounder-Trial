package rounderall.batchscheduler.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rounderall.batchscheduler.domain.UserActivitySummary
import java.time.Instant

@Repository
interface UserActivitySummaryJpaRepository : JpaRepository<UserActivitySummary, Long> {
    fun findBySummaryDate(summaryDate: Instant): List<UserActivitySummary>
    fun deleteBySummaryDate(summaryDate: Instant)
}
