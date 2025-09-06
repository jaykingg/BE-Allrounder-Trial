package rounderall.security.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import rounderall.security.application.service.UserService
import rounderall.security.domain.rbac.User

@RestController
@RequestMapping("/api/security/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<Any> {
        return try {
            val user = userService.createUser(
                username = request.username,
                email = request.email,
                password = request.password,
                firstName = request.firstName,
                lastName = request.lastName,
                keycloakId = request.keycloakId
            )
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #username")
    fun getUser(@PathVariable username: String): ResponseEntity<Any> {
        val user = userService.findByUsernameWithRolesAndPermissions(username)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @PostMapping("/{username}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignRoleToUser(
        @PathVariable username: String,
        @PathVariable roleName: String
    ): ResponseEntity<Any> {
        return try {
            userService.assignRoleToUser(username, roleName)
            ResponseEntity.ok(mapOf("message" to "Role assigned successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @DeleteMapping("/{username}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removeRoleFromUser(
        @PathVariable username: String,
        @PathVariable roleName: String
    ): ResponseEntity<Any> {
        return try {
            userService.removeRoleFromUser(username, roleName)
            ResponseEntity.ok(mapOf("message" to "Role removed successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @PostMapping("/{username}/permissions/{permissionName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun assignPermissionToUser(
        @PathVariable username: String,
        @PathVariable permissionName: String
    ): ResponseEntity<Any> {
        return try {
            userService.assignPermissionToUser(username, permissionName)
            ResponseEntity.ok(mapOf("message" to "Permission assigned successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
    
    @DeleteMapping("/{username}/permissions/{permissionName}")
    @PreAuthorize("hasRole('ADMIN')")
    fun removePermissionFromUser(
        @PathVariable username: String,
        @PathVariable permissionName: String
    ): ResponseEntity<Any> {
        return try {
            userService.removePermissionFromUser(username, permissionName)
            ResponseEntity.ok(mapOf("message" to "Permission removed successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }
}

data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String?,
    val firstName: String? = null,
    val lastName: String? = null,
    val keycloakId: String? = null
)
