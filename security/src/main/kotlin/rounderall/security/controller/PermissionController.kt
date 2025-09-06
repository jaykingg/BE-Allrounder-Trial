package rounderall.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import rounderall.security.application.service.PermissionService
import rounderall.security.domain.rbac.Permission

@RestController
@RequestMapping("/api/security/permissions")
class PermissionController(
    private val permissionService: PermissionService
) {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createPermission(@RequestBody request: CreatePermissionRequest): ResponseEntity<Any> {
        return try {
            val permission = permissionService.createPermission(
                name = request.name,
                description = request.description,
                resource = request.resource,
                action = request.action
            )
            ResponseEntity.ok(permission)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllPermissions(): ResponseEntity<List<Permission>> {
        val permissions = permissionService.findAll()
        return ResponseEntity.ok(permissions)
    }
    
    @GetMapping("/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getPermission(@PathVariable name: String): ResponseEntity<Any> {
        val permission = permissionService.findByName(name)
        return if (permission != null) {
            ResponseEntity.ok(permission)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/resource/{resource}/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getPermissionsByResourceAndAction(
        @PathVariable resource: String,
        @PathVariable action: String
    ): ResponseEntity<List<Permission>> {
        val permissions = permissionService.findByResourceAndAction(resource, action)
        return ResponseEntity.ok(permissions)
    }
}

data class CreatePermissionRequest(
    val name: String,
    val description: String? = null,
    val resource: String? = null,
    val action: String? = null
)
