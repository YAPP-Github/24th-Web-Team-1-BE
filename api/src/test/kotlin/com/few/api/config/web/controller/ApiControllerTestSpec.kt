package com.few.api.config.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.api.domain.admin.controller.AdminController
import com.few.api.domain.admin.usecase.*
import com.few.api.domain.article.controller.ArticleController
import com.few.api.domain.article.usecase.BrowseArticlesUseCase
import com.few.api.domain.article.usecase.ReadArticleByEmailUseCase
import com.few.api.domain.article.usecase.ReadArticleUseCase
import com.few.api.domain.log.controller.ApiLogController
import com.few.api.domain.log.usecase.AddApiLogUseCase
import com.few.api.domain.log.usecase.AddEmailLogUseCase
import com.few.api.domain.member.controller.MemberController
import com.few.api.domain.member.usecase.DeleteMemberUseCase
import com.few.api.domain.member.usecase.SaveMemberUseCase
import com.few.api.domain.member.usecase.TokenUseCase
import com.few.api.domain.problem.controller.ProblemController
import com.few.api.domain.problem.usecase.BrowseProblemsUseCase
import com.few.api.domain.problem.usecase.BrowseUndoneProblemsUseCase
import com.few.api.domain.problem.usecase.CheckProblemUseCase
import com.few.api.domain.problem.usecase.ReadProblemUseCase
import com.few.api.domain.subscription.controller.SubscriptionController
import com.few.api.domain.subscription.usecase.*
import com.few.api.domain.workbook.article.controller.WorkBookArticleController
import com.few.api.domain.workbook.article.usecase.ReadWorkBookArticleUseCase
import com.few.api.domain.workbook.controller.WorkBookController
import com.few.api.domain.workbook.usecase.BrowseWorkbooksUseCase
import com.few.api.domain.workbook.usecase.ReadWorkbookUseCase
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.web.client.RestTemplate
import security.TokenResolver
import security.config.SecurityConfig
import web.config.WebConfig

@ActiveProfiles(value = ["test", "new"])
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@Import(
    WebConfig::class,
    SecurityConfig::class,
    ApiControllerTestComponentConfig::class,
)
@WebMvcTest(
    controllers = [
        AdminController::class,
        ApiLogController::class,
        ArticleController::class,
        MemberController::class,
        ProblemController::class,
        SubscriptionController::class,
        WorkBookArticleController::class,
        WorkBookController::class,
    ],
)
@ExtendWith(RestDocumentationExtension::class)
abstract class ApiControllerTestSpec {
    /** WebConfig */
    @MockBean
    lateinit var restTemplate: RestTemplate

    /** Common */
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var tokenResolver: TokenResolver

    /** AdminControllerTest */
    @MockBean
    lateinit var addArticleUseCase: AddArticleUseCase

    @MockBean
    lateinit var addWorkbookUseCase: AddWorkbookUseCase

    @MockBean
    lateinit var mapArticleUseCase: MapArticleUseCase

    @MockBean
    lateinit var convertContentUseCase: ConvertContentUseCase

    @MockBean
    lateinit var putImageUseCase: PutImageUseCase

    /** ApiLogControllerTest */
    @MockBean
    lateinit var addApiLogUseCase: AddApiLogUseCase

    @MockBean
    lateinit var addEmailLogUseCase: AddEmailLogUseCase

    /** ArticleControllerTest */
    @MockBean
    lateinit var readArticleUseCase: ReadArticleUseCase

    @MockBean
    lateinit var browseArticlesUseCase: BrowseArticlesUseCase

    @MockBean
    lateinit var readArticleByEmailUseCase: ReadArticleByEmailUseCase

    /** MemberControllerTest */
    @MockBean
    lateinit var saveMemberUseCase: SaveMemberUseCase

    @MockBean
    lateinit var deleteMemberUseCase: DeleteMemberUseCase

    @MockBean
    lateinit var tokenUseCase: TokenUseCase

    /** ProblemControllerTest */
    @MockBean
    lateinit var browseProblemsUseCase: BrowseProblemsUseCase

    @MockBean
    lateinit var readProblemUseCase: ReadProblemUseCase

    @MockBean
    lateinit var checkProblemUseCase: CheckProblemUseCase

    @MockBean
    lateinit var browseUndoneProblemsUseCase: BrowseUndoneProblemsUseCase

    /** SubscriptionControllerTest */
    @MockBean
    lateinit var subscribeWorkbookUseCase: SubscribeWorkbookUseCase

    @MockBean
    lateinit var unsubscribeWorkbookUseCase: UnsubscribeWorkbookUseCase

    @MockBean
    lateinit var unsubscribeAllUseCase: UnsubscribeAllUseCase

    @MockBean
    lateinit var browseSubscribeWorkbooksUseCase: BrowseSubscribeWorkbooksUseCase

    @MockBean
    lateinit var updateSubscriptionDayUseCase: UpdateSubscriptionDayUseCase

    @MockBean
    lateinit var updateSubscriptionTimeUseCase: UpdateSubscriptionTimeUseCase

    /** WorkBookArticleControllerTest */
    @MockBean
    lateinit var readWorkBookArticleUseCase: ReadWorkBookArticleUseCase

    /** WorkBookControllerTest */
    @MockBean
    lateinit var readWorkbookUseCase: ReadWorkbookUseCase

    @MockBean
    lateinit var browseWorkBooksUseCase: BrowseWorkbooksUseCase
}