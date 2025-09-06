package rounderall.security.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import rounderall.security.domain.rbac.Role
import rounderall.security.domain.rbac.Permission
import rounderall.security.infrastructure.repository.RoleRepository
import rounderall.security.infrastructure.repository.PermissionRepository

@Service
@Transactional
class RoleService(
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository
) {

    fun createRole(name: String, description: String? = null): Role {
        if (roleRepository.existsByName(name)) {
            throw IllegalArgumentException("Role already exists: $name")
        }

        val role = Role(
            name = name,
            description = description
        )

        return roleRepository.save(role)
    }

    @Transactional(readOnly = true)
    fun findByName(name: String): Role? {
        return roleRepository.findByName(name)
    }

    @Transactional(readOnly = true)
    fun findByNameWithPermissions(name: String): Role? {
        return roleRepository.findByNameWithPermissions(name)
    }

    @Transactional(readOnly = true)
    fun findAll(): List<Role> {
        return roleRepository.findAll()
    }

    fun assignPermissionToRole(roleName: String, permissionName: String) {
        val role = roleRepository.findByName(roleName)
            ?: throw IllegalArgumentException("Role not found: $roleName")

        val permission = permissionRepository.findByName(permissionName)
            ?: throw IllegalArgumentException("Permission not found: $permissionName")

        role.addPermission(permission)
        roleRepository.save(role)
    }

    fun removePermissionFromRole(roleName: String, permissionName: String) {
        val role = roleRepository.findByName(roleName)
            ?: throw IllegalArgumentException("Role not found: $roleName")

        val permission = permissionRepository.findByName(permissionName)
            ?: throw IllegalArgumentException("Permission not found: $permissionName")

        role.removePermission(permission)
        roleRepository.save(role)
    }
}
