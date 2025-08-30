package rounderall.batchscheduler

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.domain.enums.ActivityType
import rounderall.batchscheduler.repository.UserActivityLogJpaRepository
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
class BatchSchedulerApplication {

    @Bean
    fun initData(userActivityLogRepository: UserActivityLogJpaRepository): CommandLineRunner {
        return CommandLineRunner { args ->
            println("=== 더미 데이터 생성 시작 ===")

            // 기존 데이터가 있는지 확인
            val existingCount = userActivityLogRepository.count()
            if (existingCount > 0) {
                println("기존 데이터가 ${existingCount}건 존재합니다. 더미 데이터 생성을 건너뜁니다.")
                return@CommandLineRunner
            }

            // 어제 날짜의 더미 데이터 생성
            val yesterday = LocalDate.now().minusDays(1)
            val baseTime = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()

            val dummyActivities = listOf(
                Triple("user1", ActivityType.LOGIN, baseTime.plusSeconds(3600)),
                Triple("user1", ActivityType.VIEW_PRODUCT, baseTime.plusSeconds(5400)),
                Triple("user1", ActivityType.VIEW_PRODUCT, baseTime.plusSeconds(7200)),
                Triple("user1", ActivityType.PURCHASE_PRODUCT, baseTime.plusSeconds(9000)),
                Triple("user1", ActivityType.PURCHASE_PRODUCT, baseTime.plusSeconds(10800)),
                Triple("user1", ActivityType.LOGOUT, baseTime.plusSeconds(32400)),

                Triple("user2", ActivityType.LOGIN, baseTime.plusSeconds(39600)),
                Triple("user2", ActivityType.VIEW_PRODUCT, baseTime.plusSeconds(41400)),
                Triple("user2", ActivityType.VIEW_PRODUCT, baseTime.plusSeconds(43200)),
                Triple("user2", ActivityType.PURCHASE_PRODUCT, baseTime.plusSeconds(45000)),
                Triple("user2", ActivityType.LOGOUT, baseTime.plusSeconds(64800)),

                Triple("user3", ActivityType.LOGIN, baseTime.plusSeconds(18000)),
                Triple("user3", ActivityType.VIEW_PRODUCT, baseTime.plusSeconds(19800)),
                Triple("user3", ActivityType.LOGOUT, baseTime.plusSeconds(21600)),

                Triple("user4", ActivityType.LOGIN, Instant.now().minusSeconds(3600)),
                Triple("user4", ActivityType.VIEW_PRODUCT, Instant.now().minusSeconds(1800)),
                Triple("user4", ActivityType.PURCHASE_PRODUCT, Instant.now().minusSeconds(900)),
            )

            val savedActivities = mutableListOf<UserActivityLog>()

            dummyActivities.forEach { (userId, activityType, createdAt) ->
                val activityLog = UserActivityLog(
                    userId = userId,
                    activityType = activityType,
                    description = activityType.description,
                    createdAt = createdAt
                )
                savedActivities.add(activityLog)
            }

            userActivityLogRepository.saveAll(savedActivities)

            println("=== 더미 데이터 생성 완료 ===")
            println("생성된 활동 로그: ${savedActivities.size}건")
            println("사용자별 활동 수:")
            savedActivities.groupBy { it.userId }
                .forEach { (userId, activities) ->
                    println("  - $userId: ${activities.size}건")
                }
            println("활동 유형별 분포:")
            savedActivities.groupBy { it.activityType }
                .forEach { (activityType, activities) ->
                    println("  - $activityType: ${activities.size}건")
                }
            println("================================")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<BatchSchedulerApplication>(*args)
}
