package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(private val userMapRoleService: UserMapRoleService) {

    fun checkPermissions(userEntity: UserEntity, requiredRoles: List<Int>) {
        val userRoleMappings = userMapRoleService.getMappingsByUserId(userEntity.id)

        val roleList = userRoleMappings.map { userRoleMapping -> userRoleMapping.role}

        var insufficientPermissions = true

        for (role in roleList) {
            if (requiredRoles.contains(role.id)) {
                insufficientPermissions = false
            }
        }
        if (insufficientPermissions) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
    }
}