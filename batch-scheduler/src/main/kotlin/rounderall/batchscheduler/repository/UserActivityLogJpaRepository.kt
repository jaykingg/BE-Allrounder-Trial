package rounderall.batchscheduler.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import rounderall.batchscheduler.domain.UserActivityLog
import java.time.Instant

@Repository
interface UserActivityLogJpaRepository : JpaRepository<UserActivityLog, Long> {
    
    @Query("""
        SELECT l FROM UserActivityLog l 
        WHERE l.createdAt >= :startDate AND l.createdAt < :endDate
        ORDER BY l.userId, l.createdAt
    """)
    fun findByDateRange(
        @Param("startDate") startDate: Instant,
        @Param("endDate") endDate: Instant
    ): List<UserActivityLog>
}
