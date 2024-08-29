package com.few.api.domain.member.usecase

import com.few.api.domain.member.usecase.dto.DeleteMemberUseCaseIn
import com.few.api.domain.member.usecase.dto.DeleteMemberUseCaseOut
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.command.DeleteMemberCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class DeleteMemberUseCase(
    private val memberDao: MemberDao,
) {
    @Transactional
    fun execute(useCaseIn: DeleteMemberUseCaseIn): DeleteMemberUseCaseOut {
        memberDao.deleteMember(
            DeleteMemberCommand(
                memberId = useCaseIn.memberId
            )
        )

        return DeleteMemberUseCaseOut(true)
    }
}