package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.UserEntity
import eu.kireobat.fishtime.persistence.repo.UserMapRoleRepo
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class UserMapRoleService(
    private val userMapRoleRepo: UserMapRoleRepo,
    private val authService: AuthService
) {
    fun deleteMappingsByCreatedBy(deleteUserEntity: UserEntity, authUserEntity: UserEntity, dataWipe:Boolean) {
        if(!authService.hasSufficientRolePermissions(authUserEntity, listOf(0)) && authUserEntity.id != deleteUserEntity.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        if (dataWipe) {
            userMapRoleRepo.deleteAllByCreatedBy(deleteUserEntity.id)
        } else {
            userMapRoleRepo.updateCreatedByForMappings(deleteUserEntity.id, 1)
        }
    }
}