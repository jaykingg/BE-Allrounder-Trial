package rounderall.architecture.domain.port

import rounderall.architecture.domain.model.Book
import rounderall.architecture.domain.model.BookId

/**
 * 도서 저장소 포트 (아웃바운드 포트)
 *
 * 도메인에서 외부 저장소와의 상호작용을 정의하는 인터페이스입니다.
 * 구체적인 구현은 어댑터 계층에서 제공됩니다.
 */
interface BookRepository {

    /**
     * 도서 저장
     */
    fun save(book: Book): Book

    /**
     * ID로 도서 조회
     */
    fun findById(id: BookId): Book?

    /**
     * 모든 도서 조회
     */
    fun findAll(): List<Book>

    /**
     * 제목으로 도서 검색
     */
    fun findByTitle(title: String): List<Book>

    /**
     * 도서 삭제
     */
    fun delete(id: BookId)
}