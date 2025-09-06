package rounderall.security.domain.policy

import rounderall.security.domain.common.UserId
import rounderall.security.domain.common.ResourceId

/**
 * 접근 정책을 나타내는 인터페이스
 * RBAC, ABAC, PBAC 모두 이 인터페이스를 구현
 */
interface AccessPolicy {
    fun evaluate(context: AccessContext): AccessDecision
}

/**
 * 접근 컨텍스트
 */
data class AccessContext(
    val userId: UserId,
    val resourceId: ResourceId,
    val action: String,
    val attributes: Map<String, Any> = emptyMap(),
    val environment: Map<String, Any> = emptyMap()
)

/**
 * 접근 결정
 */
data class AccessDecision(
    val granted: Boolean,
    val reason: String? = null,
    val policyType: PolicyType
)

/**
 * 정책 타입
 */
enum class PolicyType {
    RBAC,
    ABAC,
    PBAC
}