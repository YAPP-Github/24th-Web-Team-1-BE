package com.few.api.domain.article.usecase

import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseIn
import com.few.api.domain.article.usecase.dto.ReadArticlesUseCaseOut
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ReadArticlesUseCase {

    @Transactional(readOnly = true)
    fun execute(useCaseIn: ReadArticlesUseCaseIn): ReadArticlesUseCaseOut {
        return ReadArticlesUseCaseOut(emptyList()) // TODO: impl
    }
}