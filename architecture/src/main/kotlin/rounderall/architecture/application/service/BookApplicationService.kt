package rounderall.architecture.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rounderall.architecture.domain.model.*
import rounderall.architecture.domain.port.BookRepository
import rounderall.architecture.domain.port.BookService
import java.time.LocalDateTime

/**
 * 도서 애플리케이션 서비스
 * 
 * 헥사고날 아키텍처에서 애플리케이션 서비스는 유스케이스를 구현합니다.
 * 도메인 서비스와 포트를 조합하여 비즈니스 시나리오를 실행합니다.
 */
@Service
@Transactional
class BookApplicationService(
    private val bookRepository: BookRepository
) : BookService {
    
    override fun registerBook(title: String, author: String, isbn: String, price: Money): Book {
        // 도메인 규칙 검증
        require(title.isNotBlank()) { "제목은 비어있을 수 없습니다." }
        require(author.isNotBlank()) { "저자는 비어있을 수 없습니다." }
        require(isbn.isNotBlank()) { "ISBN은 비어있을 수 없습니다." }
        
        // 도메인 모델 생성
        val book = Book(
            id = BookId.of(0), // ID는 저장소에서 생성
            title = title,
            author = author,
            isbn = isbn,
            price = price,
            status = BookStatus.AVAILABLE,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        
        // 저장소에 저장
        return bookRepository.save(book)
    }
    
    override fun getBook(id: BookId): Book {
        return bookRepository.findById(id) 
            ?: throw IllegalArgumentException("도서를 찾을 수 없습니다: ${id.value}")
    }
    
    override fun getAllBooks(): List<Book> {
        return bookRepository.findAll()
    }
    
    override fun borrowBook(id: BookId): Book {
        val book = getBook(id)
        val borrowedBook = book.borrow() // 도메인 모델의 비즈니스 로직 호출
        return bookRepository.save(borrowedBook)
    }
    
    override fun returnBook(id: BookId): Book {
        val book = getBook(id)
        val returnedBook = book.returnBook() // 도메인 모델의 비즈니스 로직 호출
        return bookRepository.save(returnedBook)
    }
    
    override fun updateBook(id: BookId, title: String, author: String, price: Money): Book {
        val book = getBook(id)
        val updatedBook = book.updateInfo(title, author, price) // 도메인 모델의 비즈니스 로직 호출
        return bookRepository.save(updatedBook)
    }
    
    override fun deleteBook(id: BookId) {
        getBook(id) // 존재 여부 확인
        bookRepository.delete(id)
    }
}
```

