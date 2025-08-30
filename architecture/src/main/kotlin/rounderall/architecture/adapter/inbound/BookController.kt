package rounderall.architecture.adapter.inbound

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import rounderall.architecture.domain.model.BookId
import rounderall.architecture.domain.model.Money
import rounderall.architecture.domain.port.BookService
import java.math.BigDecimal

/**
 * 도서 컨트롤러 (인바운드 어댑터)
 * 
 * 외부 HTTP 요청을 도메인 서비스로 변환하는 어댑터입니다.
 * 헥사고날 아키텍처에서 이 계층은 외부와의 통신을 담당합니다.
 */
@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {
    
    /**
     * 도서 등록
     */
    @PostMapping
    fun registerBook(@RequestBody request: RegisterBookRequest): ResponseEntity<BookResponse> {
        val book = bookService.registerBook(
            title = request.title,
            author = request.author,
            isbn = request.isbn,
            price = Money.of(request.price)
        )
        return ResponseEntity.ok(BookResponse.from(book))
    }
    
    /**
     * 도서 조회
     */
    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val book = bookService.getBook(BookId.of(id))
        return ResponseEntity.ok(BookResponse.from(book))
    }
    
    /**
     * 도서 목록 조회
     */
    @GetMapping
    fun getAllBooks(): ResponseEntity<List<BookResponse>> {
        val books = bookService.getAllBooks()
        return ResponseEntity.ok(books.map { BookResponse.from(it) })
    }
    
    /**
     * 도서 대출
     */
    @PostMapping("/{id}/borrow")
    fun borrowBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val book = bookService.borrowBook(BookId.of(id))
        return ResponseEntity.ok(BookResponse.from(book))
    }
    
    /**
     * 도서 반납
     */
    @PostMapping("/{id}/return")
    fun returnBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val book = bookService.returnBook(BookId.of(id))
        return ResponseEntity.ok(BookResponse.from(book))
    }
    
    /**
     * 도서 정보 수정
     */
    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @RequestBody request: UpdateBookRequest
    ): ResponseEntity<BookResponse> {
        val book = bookService.updateBook(
            id = BookId.of(id),
            title = request.title,
            author = request.author,
            price = Money.of(request.price)
        )
        return ResponseEntity.ok(BookResponse.from(book))
    }
    
    /**
     * 도서 삭제
     */
    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Unit> {
        bookService.deleteBook(BookId.of(id))
        return ResponseEntity.noContent().build()
    }
}

/**
 * 도서 등록 요청 DTO
 */
data class RegisterBookRequest(
    val title: String,
    val author: String,
    val isbn: String,
    val price: BigDecimal
)

/**
 * 도서 수정 요청 DTO
 */
data class UpdateBookRequest(
    val title: String,
    val author: String,
    val price: BigDecimal
)

/**
 * 도서 응답 DTO
 */
data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val price: BigDecimal,
    val status: String,
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(book: rounderall.architecture.domain.model.Book): BookResponse {
            return BookResponse(
                id = book.id.value,
                title = book.title,
                author = book.author,
                isbn = book.isbn,
                price = book.price.amount,
                status = book.status.name,
                createdAt = book.createdAt.toString(),
                updatedAt = book.updatedAt.toString()
            )
        }
    }
}
