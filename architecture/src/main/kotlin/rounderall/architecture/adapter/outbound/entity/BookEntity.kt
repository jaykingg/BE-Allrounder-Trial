package rounderall.architecture.adapter.outbound.entity

import jakarta.persistence.*
import rounderall.architecture.domain.model.*
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 도서 엔티티 (JPA)
 * 
 * 데이터베이스와의 매핑을 담당하는 엔티티입니다.
 * 도메인 모델과 분리되어 있습니다.
 */
@Entity
@Table(name = "books")
class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(nullable = false)
    val author: String,
    
    @Column(nullable = false, unique = true)
    val isbn: String,
    
    @Column(nullable = false, precision = 10, scale = 2)
    val price: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: BookStatus,
    
    @Column(nullable = false)
    val createdAt: LocalDateTime,
    
    @Column(nullable = false)
    val updatedAt: LocalDateTime
) {
    
    /**
     * 도메인 모델로 변환
     */
    fun toDomain(): Book {
        return Book(
            id = BookId.of(id),
            title = title,
            author = author,
            isbn = isbn,
            price = Money.of(price),
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
    
    companion object {
        /**
         * 도메인 모델에서 엔티티 생성
         */
        fun from(book: Book): BookEntity {
            return BookEntity(
                id = book.id.value,
                title = book.title,
                author = book.author,
                isbn = book.isbn,
                price = book.price.amount,
                status = book.status,
                createdAt = book.createdAt,
                updatedAt = book.updatedAt
            )
        }
    }
}

