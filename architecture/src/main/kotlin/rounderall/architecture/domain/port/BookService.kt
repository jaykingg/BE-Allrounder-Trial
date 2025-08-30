package rounderall.architecture.domain.port

import rounderall.architecture.domain.model.Book
import rounderall.architecture.domain.model.BookId

/**
 * 도서 서비스 포트 (인바운드 포트)
 * 
 * 외부에서 도메인 서비스를 호출할 수 있는 인터페이스입니다.
 * 애플리케이션 서비스에서 이 인터페이스를 구현합니다.
 */
interface BookService {
    
    /**
     * 도서 등록
     */
    fun registerBook(title: String, author: String, isbn: String, price: Money): Book
    
    /**
     * 도서 조회
     */
    fun getBook(id: BookId): Book
    
    /**
     * 도서 목록 조회
     */
    fun getAllBooks(): List<Book>
    
    /**
     * 도서 대출
     */
    fun borrowBook(id: BookId): Book
    
    /**
     * 도서 반납
     */
    fun returnBook(id: BookId): Book
    
    /**
     * 도서 정보 수정
     */
    fun updateBook(id: BookId, title: String, author: String, price: Money): Book
    
    /**
     * 도서 삭제
     */
    fun deleteBook(id: BookId)
}
```

```

