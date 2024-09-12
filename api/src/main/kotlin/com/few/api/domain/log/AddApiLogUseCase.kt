package com.few.api.domain.log

import com.few.api.domain.log.dto.AddApiLogUseCaseIn
import com.few.api.repo.dao.log.LogIfoDao
import com.few.api.repo.dao.log.command.InsertLogCommand
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AddApiLogUseCase(
    private val logIfoDao: LogIfoDao,
) {
    @Transactional
    fun execute(useCaseIn: AddApiLogUseCaseIn) {
        logIfoDao.insertLogIfo(InsertLogCommand(useCaseIn.history))
    }
}