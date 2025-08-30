package rounderall.batchscheduler.service

import org.springframework.stereotype.Service
import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.domain.enums.ActivityType
import rounderall.batchscheduler.service.port.out.CreateActivity
import rounderall.batchscheduler.service.port.out.SearchActivity
import java.time.Instant

@Service
class UserActivityService(
    private val createActivity: CreateActivity,
    private val searchActivity: SearchActivity,
) {
    
    fun createActivityLog(userId: String, activityType: ActivityType): UserActivityLog {
        return createActivity.save(UserActivityLog.create(userId, activityType))
    }
    
    fun getActivitiesByDateRange(startDate: Instant, endDate: Instant): List<UserActivityLog> {
        return searchActivity.findActivitiesByDateRange(startDate, endDate)
    }
}
