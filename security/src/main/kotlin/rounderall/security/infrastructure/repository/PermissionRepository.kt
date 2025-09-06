package rounderall.security.infrastructure.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import rounderall.security.domain.rbac.Permission

@Repository
interface PermissionRepository : JpaRepository<Permission, Long> {
    
    fun findByName(name: String): Permission?
    
    fun existsByName(name: String): Boolean
    
    fun findByResourceAndAction(resource: String, action: String): List<Permission>
    
    @Query("SELECT p FROM Permission p WHERE p.resource = :resource AND (p.action = :action OR p.action IS NULL)")
    fun findByResourceAndActionOrNull(@Param("resource") resource: String, @Param("action") action: String): List<Permission>
}
