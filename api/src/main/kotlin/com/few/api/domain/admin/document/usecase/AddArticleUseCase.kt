package com.few.api.domain.admin.document.usecase

import com.few.api.domain.admin.document.dto.AddArticleUseCaseIn
import com.few.api.domain.admin.document.dto.AddArticleUseCaseOut
import com.few.api.repo.dao.article.ArticleDao
import com.few.api.repo.dao.article.command.InsertFullArticleRecordCommand
import com.few.api.repo.dao.member.MemberDao
import com.few.api.repo.dao.member.query.SelectMemberByEmailQuery
import com.few.api.repo.dao.problem.ProblemDao
import com.few.api.repo.dao.problem.command.InsertProblemsCommand
import com.few.api.repo.dao.problem.support.Content
import com.few.api.repo.dao.problem.support.Contents
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AddArticleUseCase(
    private val articleDao: ArticleDao,
    private val memberDao: MemberDao,
    private val problemDao: ProblemDao
) {
    @Transactional
    fun execute(useCaseIn: AddArticleUseCaseIn): AddArticleUseCaseOut {
        /** select writerId */
        val writerId = SelectMemberByEmailQuery(useCaseIn.writerEmail).let {
            memberDao.selectMemberByEmail(it)
        } ?: throw RuntimeException("writer not found")

        /** insert article */
        val articleMstId = InsertFullArticleRecordCommand(
            writerId = writerId.memberId,
            mainImageURL = useCaseIn.articleImageUrl,
            title = useCaseIn.title,
            category = 0, // todo fix
            content = useCaseIn.contentSource
        ).let { articleDao.insertFullArticleRecord(it) }

        /** insert problems */
        InsertProblemsCommand(
            articleId = articleMstId,
            createrId = 0L, // todo fix
            title = useCaseIn.title,
            contents = Contents(
                useCaseIn.problemData.contents.map {
                    Content(
                        it.number,
                        it.content
                    )
                }
            ),
            answer = useCaseIn.problemData.answer,
            explanation = useCaseIn.problemData.explanation
        ).let {
            problemDao.insertProblems(it)
        }

        return AddArticleUseCaseOut(articleMstId)
    }
}