package rounderall.batchscheduler.service.port.out

import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.domain.enums.ActivityType

interface CreateActivity {
    fun save(userActivityLog: UserActivityLog): UserActivityLog
}