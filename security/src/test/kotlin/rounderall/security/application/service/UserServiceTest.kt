package rounderall.security.application.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import rounderall.security.domain.rbac.User
import rounderall.security.domain.rbac.Role
import rounderall.security.infrastructure.repository.UserRepository
import rounderall.security.infrastructure.repository.RoleRepository
import rounderall.security.infrastructure.repository.PermissionRepository
import org.springframework.security.crypto.password.PasswordEncoder

class UserServiceTest : BehaviorSpec({
    
    val userRepository = mockk<UserRepository>()
    val roleRepository = mockk<RoleRepository>()
    val permissionRepository = mockk<PermissionRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    
    val userService = UserService(userRepository, roleRepository, permissionRepository, passwordEncoder)
    
    given("사용자 생성") {
        `when`("새로운 사용자를 생성할 때") {
            then("사용자가 성공적으로 생성되어야 한다") {
                // Given
                val username = "testuser"
                val email = "test@example.com"
                val password = "password123"
                val encodedPassword = "encoded_password"
                
                every { userRepository.existsByUsername(username) } returns false
                every { userRepository.existsByEmail(email) } returns false
                every { passwordEncoder.encode(password) } returns encodedPassword
                every { userRepository.save(any()) } answers { firstArg() }
                
                // When
                val result = userService.createUser(username, email, password)
                
                // Then
                result.username shouldBe username
                result.email shouldBe email
                result.passwordHash shouldBe encodedPassword
                verify { userRepository.save(any()) }
            }
        }
        
        `when`("이미 존재하는 사용자명으로 생성할 때") {
            then("예외가 발생해야 한다") {
                // Given
                val username = "existinguser"
                val email = "test@example.com"
                val password = "password123"
                
                every { userRepository.existsByUsername(username) } returns true
                
                // When & Then
                try {
                    userService.createUser(username, email, password)
                    throw AssertionError("예외가 발생해야 함")
                } catch (e: IllegalArgumentException) {
                    e.message shouldBe "Username already exists: $username"
                }
            }
        }
    }
})