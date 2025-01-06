package com.few.crm.view

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginI18n
import com.vaadin.flow.component.login.LoginOverlay
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.auth.AnonymousAllowed
import org.springframework.web.client.RestTemplate

@Route("/crm/login")
@PageTitle("Login")
@AnonymousAllowed
class CrmLoginView :
    VerticalLayout(),
    BeforeEnterObserver {
    private val loginOverlay = LoginOverlay()

    init {
        addClassName("login-view")
        setSizeFull()

        // i18n 설정 (Password 필드 제거)
        val i18n = LoginI18n.createDefault()
        i18n.form.title = "Log in"
        i18n.form.username = "Email"
        i18n.form.password = "AuthToken"
        i18n.form.submit = "Log in"
        i18n.form.forgotPassword = ""
        i18n.errorMessage.title = "Login failed"
        i18n.errorMessage.message = "Please check your username and try again."

        loginOverlay.setI18n(i18n)
        loginOverlay.setTitle("Crm Application")
        loginOverlay.description = "Please log in to access the application."
        loginOverlay.isForgotPasswordButtonVisible = false
        loginOverlay.isOpened = true
        loginOverlay.action = "" // 서버 요청 방지

        loginOverlay.addLoginListener { event ->
            val username = event.username
            val authToken = event.password
            val restTemplate = RestTemplate()
            val parameters = emptyMap<String, String>()
            val response =
                restTemplate.postForEntity(
                    "http://localhost:8080/api/v1/members/token?auth_token=$authToken",
                    parameters,
                    String::class.java,
                )

            if (response.statusCodeValue == 200) {
                VaadinService.getCurrentRequest().wrappedSession.setAttribute("user", username)
                UI.getCurrent().navigate("crm/users")
                loginOverlay.isOpened = false
            } else {
                loginOverlay.isError = true
            }
        }

        add(H1("Crm Application"), loginOverlay)
    }

    override fun beforeEnter(beforeEnterEvent: BeforeEnterEvent) {
        if (beforeEnterEvent.location.queryParameters.parameters
                .containsKey("error")
        ) {
            loginOverlay.isError = true
        }
    }
}