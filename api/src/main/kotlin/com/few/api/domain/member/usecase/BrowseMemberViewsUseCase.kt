package com.few.api.domain.member.usecase

import com.few.api.domain.member.repo.MemberDao
import com.few.api.domain.member.usecase.dto.BrowseMemberViewsUseCaseOut
import com.few.api.domain.member.usecase.dto.MemberView
import org.springframework.stereotype.Component

@Component
class BrowseMemberViewsUseCase(
    private val memberDao: MemberDao,
) {
    fun execute(): BrowseMemberViewsUseCaseOut {
        val selectAllMemberViews = memberDao.selectAllMemberViews()
        selectAllMemberViews
            .map {
                MemberView(
                    id = it.id,
                    email = it.email,
                    typeCd = it.typeCd,
                    description = it.description,
                    createdAt = it.createdAt,
                )
            }.toList()
            .let {
                return BrowseMemberViewsUseCaseOut(it)
            }
    }
}