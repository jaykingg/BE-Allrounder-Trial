package rounderall.security.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import rounderall.security.domain.rbac.User

@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    fun findByUsername(username: String): User?
    
    fun findByEmail(email: String): User?
    
    fun findByKeycloakId(keycloakId: String): User?
    
    fun existsByUsername(username: String): Boolean
    
    fun existsByEmail(email: String): Boolean
    
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role WHERE u.username = :username")
    fun findByUsernameWithRoles(@Param("username") username: String): User?
    
    @Query("SELECT u FROM User u JOIN FETCH u.userRoles ur JOIN FETCH ur.role r JOIN FETCH r.rolePermissions rp JOIN FETCH rp.permission WHERE u.username = :username")
    fun findByUsernameWithRolesAndPermissions(@Param("username") username: String): User?
