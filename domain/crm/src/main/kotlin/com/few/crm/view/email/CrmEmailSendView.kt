package com.few.crm.view.email

import com.fasterxml.jackson.databind.ObjectMapper
import com.few.crm.email.domain.EmailTemplate
import com.few.crm.email.repository.EmailTemplateRepository
import com.few.crm.email.usecase.SendNotificationEmailUseCase
import com.few.crm.email.usecase.dto.SendNotificationEmailUseCaseIn
import com.few.crm.user.domain.User
import com.few.crm.user.repository.UserRepository
import com.few.crm.view.CommonVerticalLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.springframework.data.domain.Sort

@Route("/crm/email/send")
class CrmEmailSendView(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val userRepository: UserRepository,
    private val sendNotificationEmailUseCase: SendNotificationEmailUseCase,
    private val objectMapper: ObjectMapper,
) : CommonVerticalLayout() {
    private val templateGrid =
        Grid(EmailTemplate::class.java).apply {
            removeAllColumns()
        }
    private val userGrid =
        Grid(User::class.java).apply {
            removeAllColumns()
        }

    private val selectedTemplates = mutableListOf<EmailTemplate>()
    private val selectedUsers = mutableListOf<User>()

    init {
        setSizeFull()
        templateGrid.selectionMode = Grid.SelectionMode.MULTI
        templateGrid.addSelectionListener { event ->
            selectedTemplates.clear()
            selectedTemplates.add(event.allSelectedItems.first())
            if (event.allSelectedItems.size > 1) {
                event.allSelectedItems.drop(1).forEach {
                    templateGrid.deselect(it)
                }
            }
        }
        userGrid.selectionMode = Grid.SelectionMode.MULTI
        userGrid.addSelectionListener { event ->
            selectedUsers.clear()
            selectedUsers.addAll(event.allSelectedItems)
        }
        templateGrid.height = "auto"
        userGrid.height = "auto"

        val notificationButton =
            Button("Notification").apply {
                addClickListener { sendNotificationEmail() }
            }

        val templates = emailTemplateRepository.findAll(Sort.by(Sort.Order.desc("id")))
        templateGrid.setItems(templates)

        templateGrid.addColumn(EmailTemplate::id).setHeader("ID").setSortable(true)
        templateGrid.addColumn(EmailTemplate::templateName).setHeader("Template Name")
        templateGrid.addColumn(EmailTemplate::subject).setHeader("Subject")
        templateGrid
            .addComponentColumn { template ->
                TextArea().apply {
                    value = template.body
                    isReadOnly = true
                    width = "100%"
                    style.set("min-height", "100px") // 최소 높이 설정
                }
            }.setHeader("Body")
            .setAutoWidth(true)
        templateGrid.addColumn { it.variables.joinToString(", ") }.setHeader("Variables")
        templateGrid.addColumn(EmailTemplate::version).setHeader("Version")
        templateGrid.addColumn(EmailTemplate::createdAt).setHeader("Created At")

        val searchLayout =
            HorizontalLayout(
                TextField("Search by Template Name").apply {
                    placeholder = "Enter template name..."
                    addValueChangeListener {
                        filterGridByEmail(it.value)
                    }
                },
            )

        val users = userRepository.findAll(Sort.by(Sort.Order.desc("id")))
        userGrid.setItems(users)

        userGrid.addColumn(User::id).setHeader("ID").setSortable(true)
        userGrid.addColumn(User::externalId).setHeader("External ID")
        userGrid
            .addComponentColumn {
                TextField().apply {
                    value = objectMapper.readValue(it.userAttributes, Map::class.java)["email"] as String
                    isReadOnly = true
                }
            }.setHeader("Email")
            .setAutoWidth(true)
        userGrid.addColumn(User::userAttributes).setHeader("User Attributes")
        userGrid.addColumn(User::createdAt).setHeader("Created At")

        contentArea.add(notificationButton)
        contentArea.add(searchLayout, templateGrid)
        contentArea.add(userGrid)
    }

    private fun filterGridByEmail(templateName: String) {
        val filteredTemplates =
            if (templateName.isBlank()) {
                emailTemplateRepository.findAll(Sort.by(Sort.Order.desc("id")))
            } else {
                emailTemplateRepository.findByTemplateNameContainingIgnoreCase(templateName)
            }
        templateGrid.setItems(filteredTemplates)
    }

    private fun sendNotificationEmail() {
        val dialog =
            Dialog().apply {
                width = "80%"
                height = "60%"
            }

        val layout =
            VerticalLayout().apply {
                setSizeFull()
                style.set("overflow", "auto")
            }

        val emailTemplate = selectedTemplates.firstOrNull()
        val templateGrid =
            Grid(EmailTemplate::class.java).apply {
            }
        templateGrid.setItems(emailTemplate)
        templateGrid.addColumn(EmailTemplate::id).setHeader("ID").setSortable(true)
        templateGrid.addColumn(EmailTemplate::templateName).setHeader("Template Name")
        templateGrid.addColumn(EmailTemplate::subject).setHeader("Subject")
        templateGrid
            .addComponentColumn { template ->
                TextArea().apply {
                    value = template.body
                    isReadOnly = true
                    width = "100%"
                    style.set("min-height", "100px") // 최소 높이 설정
                }
            }.setHeader("Body")
            .setAutoWidth(true)
        templateGrid.addColumn { it.variables.joinToString(", ") }.setHeader("Variables")
        templateGrid.addColumn(EmailTemplate::version).setHeader("Version")
        templateGrid.addColumn(EmailTemplate::createdAt).setHeader("Created At")

        val userGrid =
            Grid(User::class.java).apply {
                removeAllColumns()
            }
        userGrid.setItems(selectedUsers)
        userGrid.addColumn(User::id).setHeader("ID").setSortable(true)
        userGrid
            .addComponentColumn {
                TextField().apply {
                    value = objectMapper.readValue(it.userAttributes, Map::class.java)["email"] as String
                    isReadOnly = true
                }
            }.setHeader("Email")
            .setAutoWidth(true)

        val sendButton =
            Button("Send").apply {
                addClickListener {
                    sendNotificationEmailUseCase.execute(
                        SendNotificationEmailUseCaseIn(
                            templateId = emailTemplate!!.id!!,
                            templateVersion = null,
                            userIds = selectedUsers.map { it.id!! },
                        ),
                    )
                    dialog.close()
                }
            }

        layout.add(templateGrid, userGrid, sendButton)
        dialog.add(layout)
        dialog.open()
    }
}