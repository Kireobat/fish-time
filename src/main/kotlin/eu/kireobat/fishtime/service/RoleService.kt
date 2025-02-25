package eu.kireobat.fishtime.service

import eu.kireobat.fishtime.persistence.entity.RoleEntity
import eu.kireobat.fishtime.persistence.repo.RoleRepo
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService(private val roleRepo: RoleRepo) {
    fun getRoleById(id: Int): Optional<RoleEntity> {
        return roleRepo.findById(id.toString())
    }
}