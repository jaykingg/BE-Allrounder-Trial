package rounderall.security.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rounderall.security.domain.rbac.Permission
import rounderall.security.infrastructure.repository.PermissionRepository

@Service
@Transactional
class PermissionService(
    private val permissionRepository: PermissionRepository
) {
    
    fun createPermission(
        name: String,
        description: String? = null,
        resource: String? = null,
        action: String? = null
    ): Permission {
        if (permissionRepository.existsByName(name)) {
            throw IllegalArgumentException("Permission already exists: $name")
        }
        
        val permission = Permission(
            name = name,
            description = description,
            resource = resource,
            action = action
        )
        
        return permissionRepository.save(permission)
    }
    
    @Transactional(readOnly = true)
    fun findByName(name: String): Permission? {
        return permissionRepository.findByName(name)
    }
    
    @Transactional(readOnly = true)
    fun findByResourceAndAction(resource: String, action: String): List<Permission> {
        return permissionRepository.findByResourceAndActionOrNull(resource, action)
    }
    
    @Transactional(readOnly = true)
    fun findAll(): List<Permission> {
        return permissionRepository.findAll()
    }
}
