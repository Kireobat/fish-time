package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.UserMapRoleEntity
import eu.kireobat.fishtime.persistence.repo.UserMapRoleRepo
import org.springframework.stereotype.Service

@Service
class UserMapRoleService(private val userMapRoleRepo: UserMapRoleRepo) {
    fun getMappingsByUserId(id: Int): List<UserMapRoleEntity> {
        return userMapRoleRepo.findAllByUserId(id)
    }
}