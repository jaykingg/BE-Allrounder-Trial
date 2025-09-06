package rounderall.security.infrastructure.config

import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import rounderall.security.application.service.UserService
import rounderall.security.application.service.RoleService
import rounderall.security.application.service.PermissionService

@Component
class DataInitializer(
    private val userService: UserService,
    private val roleService: RoleService,
    private val permissionService: PermissionService,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {
    
    override fun run(vararg args: String?) {
        initializeRoles()
        initializePermissions()
        initializeUsers()
        assignPermissionsToRoles()
        assignRolesToUsers()
    }
    
    private fun initializeRoles() {
        try {
            roleService.createRole("ADMIN", "시스템 관리자")
            roleService.createRole("USER", "일반 사용자")
            roleService.createRole("MANAGER", "매니저")
        } catch (e: Exception) {
            // 이미 존재하는 경우 무시
        }
    }
    
    private fun initializePermissions() {
        try {
            // 사용자 관리 권한
            permissionService.createPermission("user:read", "사용자 조회", "user", "read")
            permissionService.createPermission("user:write", "사용자 생성/수정", "user", "write")
            permissionService.createPermission("user:delete", "사용자 삭제", "user", "delete")
            
            // 역할 관리 권한
            permissionService.createPermission("role:read", "역할 조회", "role", "read")
            permissionService.createPermission("role:write", "역할 생성/수정", "role", "write")
            permissionService.createPermission("role:delete", "역할 삭제", "role", "delete")
            
            // 권한 관리 권한
            permissionService.createPermission("permission:read", "권한 조회", "permission", "read")
            permissionService.createPermission("permission:write", "권한 생성/수정", "permission", "write")
            permissionService.createPermission("permission:delete", "권한 삭제", "permission", "delete")
            
            // 일반 권한
            permissionService.createPermission("profile:read", "프로필 조회", "profile", "read")
            permissionService.createPermission("profile:write", "프로필 수정", "profile", "write")
        } catch (e: Exception) {
            // 이미 존재하는 경우 무시
        }
    }
    
    private fun initializeUsers() {
        try {
            userService.createUser(
                username = "admin",
                email = "admin@example.com",
                password = "admin123",
                firstName = "Admin",
                lastName = "User"
            )
            
            userService.createUser(
                username = "user1",
                email = "user1@example.com",
                password = "user123",
                firstName = "User",
                lastName = "One"
            )
            
            userService.createUser(
                username = "manager1",
                email = "manager1@example.com",
                password = "manager123",
                firstName = "Manager",
                lastName = "One"
            )
        } catch (e: Exception) {
            // 이미 존재하는 경우 무시
        }
    }
    
    private fun assignPermissionsToRoles() {
        try {
            // ADMIN 역할에 모든 권한 부여
            roleService.assignPermissionToRole("ADMIN", "user:read")
            roleService.assignPermissionToRole("ADMIN", "user:write")
            roleService.assignPermissionToRole("ADMIN", "user:delete")
            roleService.assignPermissionToRole("ADMIN", "role:read")
            roleService.assignPermissionToRole("ADMIN", "role:write")
            roleService.assignPermissionToRole("ADMIN", "role:delete")
            roleService.assignPermissionToRole("ADMIN", "permission:read")
            roleService.assignPermissionToRole("ADMIN", "permission:write")
            roleService.assignPermissionToRole("ADMIN", "permission:delete")
            roleService.assignPermissionToRole("ADMIN", "profile:read")
            roleService.assignPermissionToRole("ADMIN", "profile:write")
            
            // MANAGER 역할에 제한된 권한 부여
            roleService.assignPermissionToRole("MANAGER", "user:read")
            roleService.assignPermissionToRole("MANAGER", "role:read")
            roleService.assignPermissionToRole("MANAGER", "permission:read")
            roleService.assignPermissionToRole("MANAGER", "profile:read")
            roleService.assignPermissionToRole("MANAGER", "profile:write")
            
            // USER 역할에 기본 권한 부여
            roleService.assignPermissionToRole("USER", "profile:read")
            roleService.assignPermissionToRole("USER", "profile:write")
        } catch (e: Exception) {
            // 이미 존재하는 경우 무시
        }
    }
    
    private fun assignRolesToUsers() {
        try {
            userService.assignRoleToUser("admin", "ADMIN")
            userService.assignRoleToUser("user1", "USER")
            userService.assignRoleToUser("manager1", "MANAGER")
        } catch (e: Exception) {
            // 이미 존재하는 경우 무시
        }
    }
}
