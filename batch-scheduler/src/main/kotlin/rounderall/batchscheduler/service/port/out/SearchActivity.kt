package rounderall.batchscheduler.service.port.out

import rounderall.batchscheduler.domain.UserActivityLog
import java.time.Instant

interface SearchActivity {
    fun findActivitiesByDateRange(startDate: Instant, endDate: Instant): List<UserActivityLog>
}