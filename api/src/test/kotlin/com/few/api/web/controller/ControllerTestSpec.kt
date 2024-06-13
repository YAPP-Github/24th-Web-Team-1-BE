package com.few.api.web.controller

import com.few.ApiMain
import com.few.api.web.handler.ApiControllerExceptionHandler
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(value = ["test"])
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest(classes = [ApiMain::class])
@ExtendWith(RestDocumentationExtension::class)
abstract class ControllerTestSpec {

    @Autowired
    lateinit var apiControllerExceptionHandler: ApiControllerExceptionHandler
}