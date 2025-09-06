package rounderall.security.domain.common

/**
 * 값 객체를 나타내는 마커 인터페이스
 */
interface ValueObject

/**
 * 도메인 식별자를 나타내는 인터페이스
 */
interface DomainId<T> : ValueObject {
    val value: T
}

/**
 * 사용자 ID 값 객체
 */
@JvmInline
value class UserId(override val value: Long) : DomainId<Long>

/**
 * 역할 ID 값 객체
 */
@JvmInline
value class RoleId(override val value: Long) : DomainId<Long>

/**
 * 권한 ID 값 객체
 */
@JvmInline
value class PermissionId(override val value: Long) : DomainId<Long>

/**
 * 리소스 ID 값 객체
 */
@JvmInline
value class ResourceId(override val value: String) : DomainId<String>
