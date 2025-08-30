package rounderall.architecture.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * 도서 도메인 모델
 *
 * 헥사고날 아키텍처에서 도메인 모델은 비즈니스 규칙을 캡슐화합니다.
 * 외부 의존성이 없고 순수한 비즈니스 로직만 포함합니다.
 */
data class Book(
    val id: BookId,
    val title: String,
    val author: String,
    val isbn: String,
    val price: Money,
    val status: BookStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {

    /**
     * 도서 대출 가능 여부 확인
     */
    fun canBeBorrowed(): Boolean = status == BookStatus.AVAILABLE

    /**
     * 도서 대출 처리
     */
    fun borrow(): Book {
        require(canBeBorrowed()) { "대출 가능한 도서가 아닙니다." }
        return copy(
            status = BookStatus.BORROWED,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 도서 반납 처리
     */
    fun returnBook(): Book {
        require(status == BookStatus.BORROWED) { "대출 중인 도서가 아닙니다." }
        return copy(
            status = BookStatus.AVAILABLE,
            updatedAt = LocalDateTime.now()
        )
    }

    /**
     * 도서 정보 수정
     */
    fun updateInfo(title: String, author: String, price: Money): Book {
        require(title.isNotBlank()) { "제목은 비어있을 수 없습니다." }
        require(author.isNotBlank()) { "저자는 비어있을 수 없습니다." }

        return copy(
            title = title,
            author = author,
            price = price,
            updatedAt = LocalDateTime.now()
        )
    }
}

/**
 * 도서 ID 값 객체
 */
@JvmInline
value class BookId(val value: Long) {
    companion object {
        fun of(value: Long): BookId = BookId(value)
    }
}

/**
 * 금액 값 객체
 */
@JvmInline
value class Money(val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다." }
    }

    companion object {
        fun of(amount: BigDecimal): Money = Money(amount)
        fun of(amount: Int): Money = Money(BigDecimal.valueOf(amount.toLong()))
    }

    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
}

/**
 * 도서 상태 열거형
 */
enum class BookStatus {
    AVAILABLE,  // 대출 가능
    BORROWED,   // 대출 중
    LOST        // 분실
}
