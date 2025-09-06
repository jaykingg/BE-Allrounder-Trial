package rounderall.security.infrastructure.service

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import rounderall.security.application.service.UserService

@Service
class CustomUserDetailsService(
    private val userService: UserService
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userService.findByUsernameWithRolesAndPermissions(username)
            ?: throw UsernameNotFoundException("User not found: $username")
        
        val authorities = mutableListOf<GrantedAuthority>()
        
        // 역할 추가
        user.getRoles().forEach { role ->
            authorities.add(SimpleGrantedAuthority("ROLE_${role.name}"))
        }
        
        // 권한 추가
        user.getPermissions().forEach { permission ->
            authorities.add(SimpleGrantedAuthority(permission.name))
        }
        
        return User(
            user.username,
            user.passwordHash ?: "",
            user.isActive,
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            authorities
        )
    }
}
