package rounderall.batchscheduler.batch

import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import rounderall.batchscheduler.domain.UserActivityLog
import rounderall.batchscheduler.domain.UserActivitySummary
import rounderall.batchscheduler.repository.UserActivityLogJpaRepository
import rounderall.batchscheduler.repository.UserActivitySummaryJpaRepository
import java.time.LocalDate
import java.time.ZoneId

@Configuration
class UserActivitySummaryBatchConfig(
    private val jobRepository: JobRepository,
    private val transactionManager: PlatformTransactionManager,
    private val userActivityLogRepository: UserActivityLogJpaRepository,
    private val userActivitySummaryRepository: UserActivitySummaryJpaRepository,
    private val entityManagerFactory: EntityManagerFactory
) {

    @Bean
    fun userActivitySummaryJob(): Job {
        return JobBuilder("userActivitySummaryJob", jobRepository)
            .start(userActivitySummaryStep())
            .build()
    }

    @Bean
    fun userActivitySummaryStep(): Step {
        return StepBuilder("userActivitySummaryStep", jobRepository)
            .chunk<UserActivityLog, UserActivitySummary>(10, transactionManager)
            .reader(userActivityLogReader())
            .processor(userActivityLogProcessor())
            .writer(userActivitySummaryWriter())
            .build()
    }

    @Bean
    fun userActivityLogReader(): ItemReader<UserActivityLog> {
        return object : ItemReader<UserActivityLog> {
            private var activities: List<UserActivityLog> = emptyList()
            private var currentIndex = 0

            override fun read(): UserActivityLog? {
                if (activities.isEmpty()) {
                    val yesterday = LocalDate.now().minusDays(1)
                    val startInstant = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    val endInstant = yesterday.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                    activities = userActivityLogRepository.findByDateRange(startInstant, endInstant)
                }

                return if (currentIndex < activities.size) {
                    activities[currentIndex++]
                } else {
                    null
                }
            }
        }
    }

    @Bean
    fun userActivityLogProcessor(): ItemProcessor<UserActivityLog, UserActivitySummary> {
        return object : ItemProcessor<UserActivityLog, UserActivitySummary> {
            private val userActivities = mutableMapOf<String, MutableList<UserActivityLog>>()

            override fun process(item: UserActivityLog): UserActivitySummary? {
                userActivities.getOrPut(item.userId) { mutableListOf() }.add(item)

                // 아직 처리하지 않고 누적
                return null
            }
        }
    }

    @Bean
    fun userActivitySummaryWriter(): ItemWriter<UserActivitySummary> {
        return object : ItemWriter<UserActivitySummary> {
            override fun write(chunk: Chunk<out UserActivitySummary>) {
                val yesterday = LocalDate.now().minusDays(1)
                val summaryDateInstant = yesterday.atStartOfDay(ZoneId.systemDefault()).toInstant()

                // 기존 요약 데이터 삭제
                userActivitySummaryRepository.deleteBySummaryDate(summaryDateInstant)

                // 사용자별 요약 생성
                val userActivities = mutableMapOf<String, MutableList<UserActivityLog>>()

                // Reader에서 읽은 데이터를 사용자별로 그룹화
                val allActivities = userActivityLogRepository.findByDateRange(
                    summaryDateInstant,
                    yesterday.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                )

                allActivities.forEach { activity ->
                    userActivities.getOrPut(activity.userId) { mutableListOf() }.add(activity)
                }

                // 사용자별 요약 생성
                userActivities.forEach { (userId, activities) ->
                    val uniqueActivityTypes = activities.map { it.activityType }.distinct().size
                    val lastActivityTime = activities.maxByOrNull { it.createdAt }?.createdAt?.toString() ?: ""

                    val summary = UserActivitySummary.create(
                        userId = userId,
                        summaryDate = summaryDateInstant,
                        totalActivities = activities.size,
                        uniqueActivityTypes = uniqueActivityTypes,
                        lastActivityTime = lastActivityTime
                    )

                    userActivitySummaryRepository.save(summary)
                }
            }
        }
    }
}