package rounderall.architecture.adapter.outbound

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import rounderall.architecture.adapter.outbound.entity.BookEntity

/**
 * JPA 리포지토리 인터페이스
 * 
 * Spring Data JPA를 사용하여 데이터베이스 접근을 담당합니다.
 */
@Repository
interface BookJpaRepository : JpaRepository<BookEntity, Long> {
    fun findByTitleContaining(title: String): List<BookEntity>
}

