package rounderall.batchscheduler.controller

import org.springframework.web.bind.annotation.*
import rounderall.batchscheduler.service.UserActivityService
import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.domain.enums.ActivityType

@RestController
@RequestMapping("/api/batch/")
class UserActivityController(
    private val userActivityService: UserActivityService
) {

    @PostMapping("/log")
    fun createActivityLog(
        @RequestParam userId: String,
        @RequestParam activityType: ActivityType,
    ): UserActivityLog {
        return userActivityService.createActivityLog(userId, activityType)
    }

    @GetMapping("/test-data")
    fun createTestData(): Map<String, String> {
        // 테스트용 데이터 생성
        val activities = listOf(
            Pair("user1", ActivityType.LOGIN),
            Pair("user1", ActivityType.VIEW_PRODUCT),
            Pair("user2", ActivityType.LOGIN),
            Pair("user2", ActivityType.PURCHASE_PRODUCT),
            Pair("user1", ActivityType.LOGOUT)
        )

        activities.forEach { (userId, activityType) ->
            userActivityService.createActivityLog(userId, activityType)
        }

        return mapOf("message" to "테스트 데이터가 생성되었습니다.")
    }
}

