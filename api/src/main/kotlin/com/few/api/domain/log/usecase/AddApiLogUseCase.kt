package com.few.api.domain.log.usecase

import com.few.api.domain.log.dto.AddApiLogUseCaseIn
import com.few.api.domain.log.repo.LogIfoDao
import com.few.api.domain.log.repo.command.InsertLogCommand
import org.springframework.stereotype.Component
import repo.jooq.DataSourceTransactional

@Component
class AddApiLogUseCase(
    private val logIfoDao: LogIfoDao,
) {
    @DataSourceTransactional
    fun execute(useCaseIn: AddApiLogUseCaseIn) {
        logIfoDao.insertLogIfo(InsertLogCommand(useCaseIn.history))
    }
}