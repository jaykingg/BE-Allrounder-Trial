package rounderall.batchscheduler.scheduler

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class UserActivityScheduler(
    private val jobLauncher: JobLauncher,
    private val userActivitySummaryJob: Job
) {

    @Scheduled(cron = "0 * * * * ?")
    fun runUserActivitySummaryJob() {
        try {
            val jobParameters = JobParametersBuilder()
                .addString("executionTime", LocalDateTime.now().toString())
                .toJobParameters()

            val execution = jobLauncher.run(userActivitySummaryJob, jobParameters)
            println("배치 작업 실행 완료: ${execution.status}")
        } catch (e: Exception) {
            println("배치 작업 실행 실패: ${e.message}")
        }
    }
}
