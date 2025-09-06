package rounderall.security.domain.policy

import rounderall.security.domain.common.UserId
import rounderall.security.domain.common.ResourceId
import rounderall.security.domain.rbac.User
import rounderall.security.domain.rbac.Permission

/**
 * RBAC 정책 구현
 */
class RbacPolicy(
    private val user: User
) : AccessPolicy {
    
    override fun evaluate(context: AccessContext): AccessDecision {
        val hasPermission = user.hasPermission("${context.resourceId.value}:${context.action}") ||
                           user.getRoles().any { role ->
                               role.hasPermission("${context.resourceId.value}:${context.action}")
                           }
        
        return AccessDecision(
            granted = hasPermission,
            reason = if (hasPermission) "User has required permission" else "User lacks required permission",
            policyType = PolicyType.RBAC
        )
    }
}
