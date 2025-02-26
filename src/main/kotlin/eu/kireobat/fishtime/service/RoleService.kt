package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.RoleRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class RoleService(
    private val authService: AuthService,
    private val roleRepo: RoleRepo
) {
    fun deleteRolesByCreatedBy(deleteUserEntity: UserEntity, authUserEntity: UserEntity, dataWipe:Boolean) {
        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) || authUserEntity.id == deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (dataWipe) {
            roleRepo.deleteAllByCreatedBy(deleteUserEntity.id)
        } else {
            roleRepo.updateCreatedByForRoles(deleteUserEntity.id, 1)
        }
    }
}