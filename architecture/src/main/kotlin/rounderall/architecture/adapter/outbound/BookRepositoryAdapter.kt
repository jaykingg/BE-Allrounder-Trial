package rounderall.architecture.adapter.outbound

import org.springframework.stereotype.Repository
import rounderall.architecture.domain.model.Book
import rounderall.architecture.domain.model.BookId
import rounderall.architecture.domain.port.BookRepository
import rounderall.architecture.adapter.outbound.entity.BookEntity

/**
 * 도서 저장소 어댑터 (아웃바운드 어댑터)
 * 
 * 도메인 포트를 구현하여 데이터베이스와의 상호작용을 담당합니다.
 * 헥사고날 아키텍처에서 이 계층은 외부 인프라와의 통신을 담당합니다.
 */
@Repository
class BookRepositoryAdapter(
    private val bookJpaRepository: BookJpaRepository
) : BookRepository {
    
    override fun save(book: Book): Book {
        val entity = BookEntity.from(book)
        val savedEntity = bookJpaRepository.save(entity)
        return savedEntity.toDomain()
    }
    
    override fun findById(id: BookId): Book? {
        return bookJpaRepository.findById(id.value)
            .map { it.toDomain() }
            .orElse(null)
    }
    
    override fun findAll(): List<Book> {
        return bookJpaRepository.findAll()
            .map { it.toDomain() }
    }
    
    override fun findByTitle(title: String): List<Book> {
        return bookJpaRepository.findByTitleContaining(title)
            .map { it.toDomain() }
    }
    
    override fun delete(id: BookId) {
        bookJpaRepository.deleteById(id.value)
    }
}
```

