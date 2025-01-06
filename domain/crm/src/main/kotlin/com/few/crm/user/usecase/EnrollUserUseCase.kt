package com.few.crm.user.usecase

import com.few.crm.support.jpa.CrmTransactional
import com.few.crm.user.domain.User
import com.few.crm.user.repository.UserRepository
import com.few.crm.user.usecase.dto.EnrollUserUseCaseIn
import com.few.crm.user.usecase.dto.EnrollUserUseCaseOut
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class EnrollUserUseCase(
    private val userRepository: UserRepository,
) {
    @CrmTransactional
    fun execute(useCaseIn: EnrollUserUseCaseIn): EnrollUserUseCaseOut {
        val id: Long? = useCaseIn.id
        val externalId: String = useCaseIn.externalId
        val userAttributes: String = useCaseIn.userAttributes

        if (id != null) {
            val modifiedUser =
                userRepository
                    .findById(id)
                    .orElseThrow {
                        throw IllegalArgumentException("User not found id: $id")
                    }.apply {
                        updateAttributes(userAttributes)
                    }
            userRepository.save(modifiedUser)
        } else {
            userRepository.save(
                User(
                    externalId = externalId,
                    userAttributes = userAttributes,
                ),
            )
        }.let {
            return EnrollUserUseCaseOut(
                id = it.id!!,
                externalId = it.externalId!!,
                userAttributes = it.userAttributes,
            )
        }
    }
}