package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.UserEntity
import org.springframework.stereotype.Service

@Service
class AuthService(private val userMapRoleService: UserMapRoleService) {

    fun hasSufficientRolePermissions(userEntity: UserEntity, requiredRoles: List<Int>): Boolean {
        val userRoleMappings = userMapRoleService.getMappingsByUserId(userEntity.id)

        val roleList = userRoleMappings.map { userRoleMapping -> userRoleMapping.role}

        var sufficientPermissions = false

        for (role in roleList) {
            if (requiredRoles.contains(role.id)) {
                sufficientPermissions = true
            }
        }

        return sufficientPermissions
    }
}