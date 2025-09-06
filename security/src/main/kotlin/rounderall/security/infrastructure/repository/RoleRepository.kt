package rounderall.security.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import rounderall.security.domain.rbac.Role

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    
    fun findByName(name: String): Role?
    
    fun existsByName(name: String): Boolean
    
    @Query("SELECT r FROM Role r JOIN FETCH r.rolePermissions rp JOIN FETCH rp.permission WHERE r.name = :name")
    fun findByNameWithPermissions(@Param("name") name: String): Role?
}
