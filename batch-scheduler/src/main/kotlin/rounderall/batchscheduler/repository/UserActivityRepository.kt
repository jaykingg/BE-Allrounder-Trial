package rounderall.batchscheduler.repository

import org.springframework.stereotype.Repository
import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.service.port.out.CreateActivity
import rounderall.batchscheduler.service.port.out.SearchActivity
import java.time.Instant

@Repository
class UserActivityRepository(
    private val userActivityLogJpaRepository: UserActivityLogJpaRepository,
    private val userActivitySummaryJpaRepository: UserActivitySummaryJpaRepository
) : CreateActivity, SearchActivity {

    override fun save(
        userActivityLog: UserActivityLog
    ): UserActivityLog {
        return userActivityLogJpaRepository.save(userActivityLog)
    }

    override fun findActivitiesByDateRange(
        startDate: Instant,
        endDate: Instant
    ): List<UserActivityLog> {
        return userActivityLogJpaRepository.findByDateRange(startDate, endDate)
    }
}