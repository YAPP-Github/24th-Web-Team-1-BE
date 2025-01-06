package com.few.crm.view.email

import com.few.crm.email.domain.EmailSendHistory
import com.few.crm.email.repository.EmailSendHistoryRepository
import com.few.crm.view.CommonVerticalLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.springframework.data.domain.Sort

@Route("/crm/email/send/histories")
class CrmEmailSendHistoryView(
    private val emailSendHistoryRepository: EmailSendHistoryRepository,
) : CommonVerticalLayout() {
    private val grid =
        Grid(EmailSendHistory::class.java).apply {
            removeAllColumns()
        }

    init {
        setSizeFull()

        val emailSendHistories = emailSendHistoryRepository.findAll(Sort.by(Sort.Order.desc("id")))
        grid.setItems(emailSendHistories)

        grid.addColumn(EmailSendHistory::id).setHeader("ID").setSortable(true)
        grid.addColumn(EmailSendHistory::userExternalId).setHeader("User External ID")
        grid
            .addColumn(EmailSendHistory::userEmail)
            .setHeader("User Email")
            .setAutoWidth(true)
        grid
            .addColumn(EmailSendHistory::emailMessageId)
            .setHeader("Email Message ID")
            .setAutoWidth(true)
        grid
            .addComponentColumn { history ->
                TextArea().apply {
                    value = history.emailBody
                    isReadOnly = true
                    width = "100%"
                    style.set("min-height", "100px") // 최소 높이 설정
                }
            }.setHeader("Email Body")
            .setAutoWidth(true)
        grid.addColumn(EmailSendHistory::sendStatus).setHeader("Send Status")

        val searchLayout =
            HorizontalLayout(
                TextField("Search by Email").apply {
                    placeholder = "Enter email..."
                    addValueChangeListener {
                        filterGridByEmail(it.value)
                    }
                },
            )

        contentArea.add(searchLayout, grid)
    }

    private fun filterGridByEmail(email: String) {
        val filteredHistories =
            if (email.isBlank()) {
                emailSendHistoryRepository.findAll(Sort.by(Sort.Order.desc("id")))
            } else {
                emailSendHistoryRepository.findByUserEmailContainingIgnoreCase(email)
            }
        grid.setItems(filteredHistories)
    }
}