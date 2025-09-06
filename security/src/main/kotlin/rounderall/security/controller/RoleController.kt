package rounderall.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import rounderall.security.application.service.RoleService
import rounderall.security.domain.rbac.Role

@RestController
@RequestMapping("/api/security/roles")
class RoleController(
    private val roleService: RoleService
) {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createRole(@RequestBody request: CreateRoleRequest): ResponseEntity<Any> {
        return try {
            val role = roleService.createRole(request.name, request.description)
            ResponseEntity.ok(role)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllRoles(): ResponseEntity<List<Role>> {
        val roles = roleService.findAll()
        return ResponseEntity.ok(roles)
    }
    
    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getRole(@PathVariable name: String): ResponseEntity<Any> {
        val role = roleService.findByNameWithPermissions(name)
        return if (role != null) {
            ResponseEntity.ok(role)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/{roleName}/permissions/{permissionName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignPermissionToRole(
        @PathVariable roleName: String,
        @PathVariable permissionName: String
    ): ResponseEntity<Any> {
        return try {
            roleService.assignPermissionToRole(roleName, permissionName)
            ResponseEntity.ok(mapOf("message" to "Permission assigned to role successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @DeleteMapping("/{roleName}/permissions/{permissionName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removePermissionFromRole(
        @PathVariable roleName: String,
        @PathVariable permissionName: String
    ): ResponseEntity<Any> {
        return try {
            roleService.removePermissionFromRole(roleName, permissionName)
            ResponseEntity.ok(mapOf("message" to "Permission removed from role successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}

data class CreateRoleRequest(
    val name: String,
    val description: String? = null
)
