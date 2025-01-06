package com.few.crm.view

import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.server.VaadinService

abstract class CommonVerticalLayout : VerticalLayout() {
    val contentArea =
        VerticalLayout().apply {
            setSizeFull()
            style.set("background-color", "#ffffff")
        }

    init {
        val user = VaadinService.getCurrentRequest().wrappedSession.getAttribute("user")
        if (user == null || user == "") {
            UI.getCurrent().access {
                UI.getCurrent().navigate("crm/login")
            }
        }

        val logo = H1("FEW CRM")
        logo.addClassName("logo")
        val logout =
            Button("Logout") {
                VaadinService.getCurrentRequest().wrappedSession.setAttribute("user", null)
                UI.getCurrent().navigate("crm/login")
            }
        val header = HorizontalLayout(logo, logout)
        header.setWidthFull()
        header.justifyContentMode = FlexComponent.JustifyContentMode.BETWEEN
        add(header)

        val navBar =
            VerticalLayout().apply {
                width = "200px"
                style.set("background-color", "#f8f9fa")
                add(
                    Button("Templates") { UI.getCurrent().navigate("crm/email/templates") },
                    Button("Send") { UI.getCurrent().navigate("crm/email/send") },
                    Button("Send Histories") { UI.getCurrent().navigate("crm/email/send/histories") },
                    Button("Users") { UI.getCurrent().navigate("crm/users") },
                )
            }

        val mainLayout =
            HorizontalLayout(navBar, contentArea).apply {
                setSizeFull()
                expand(contentArea) // 콘텐츠 영역 확장
            }
        add(mainLayout)
        setSizeFull()
    }
}