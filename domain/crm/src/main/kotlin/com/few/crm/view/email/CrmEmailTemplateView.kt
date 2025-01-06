package com.few.crm.view.email

import com.few.crm.email.domain.EmailTemplate
import com.few.crm.email.domain.EmailTemplateHistory
import com.few.crm.email.repository.EmailTemplateHistoryRepository
import com.few.crm.email.repository.EmailTemplateRepository
import com.few.crm.email.usecase.PostTemplateUseCase
import com.few.crm.email.usecase.dto.PostTemplateUseCaseIn
import com.few.crm.view.CommonVerticalLayout
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextArea
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.Route
import org.springframework.data.domain.Sort

@Route("/crm/email/templates")
class CrmEmailTemplateView(
    private val emailTemplateRepository: EmailTemplateRepository,
    private val emailTemplateHistoryRepository: EmailTemplateHistoryRepository,
    private val postTemplateUseCase: PostTemplateUseCase,
) : CommonVerticalLayout() {
    private val grid =
        Grid(EmailTemplate::class.java).apply {
            removeAllColumns()
        }

    init {
        val addButton =
            Button("Add").apply {
                addClickListener { openAddDialog() }
            }
        val templates = emailTemplateRepository.findAll(Sort.by(Sort.Order.desc("id")))
        grid.setItems(templates)

        grid.addColumn(EmailTemplate::id).setHeader("ID").setSortable(true)
        grid.addColumn(EmailTemplate::templateName).setHeader("Template Name")
        grid.addColumn(EmailTemplate::subject).setHeader("Subject")
        grid
            .addComponentColumn { template ->
                TextArea().apply {
                    value = template.body
                    isReadOnly = true
                    width = "100%"
                    style.set("min-height", "100px") // 최소 높이 설정
                }
            }.setHeader("Body")
            .setAutoWidth(true)
        grid.addColumn { it.variables.joinToString(", ") }.setHeader("Variables")
        grid.addColumn(EmailTemplate::version).setHeader("Version")
        grid.addColumn(EmailTemplate::createdAt).setHeader("Created At")

        grid.addComponentColumn { template ->
            Button("Edit").apply {
                addClickListener {
                    openEditDialog(template)
                }
            }
        }

        grid.addComponentColumn { template ->
            Button("History").apply {
                addClickListener { openHistoryDialog(template) }
            }
        }

        contentArea.add(addButton)
        contentArea.add(grid)
    }

    private fun openAddDialog() {
        val dialog = Dialog()
        val form = FormLayout()

        val templateNameField = TextField("Template Name")
        val subjectField = TextField("Subject")
        val bodyField = TextArea("Body")
        val variablesField = TextField("Variables (comma-separated)")

        val saveButton =
            Button("Save") {
                PostTemplateUseCaseIn(
                    id = null,
                    templateName = templateNameField.value,
                    subject = subjectField.value,
                    version = 1.0f,
                    body = bodyField.value,
                    variables = variablesField.value.split(",").map { it.trim() },
                ).let {
                    postTemplateUseCase.execute(it)
                }

                grid.setItems(emailTemplateRepository.findAll())
                dialog.close()
            }

        val cancelButton =
            Button("Cancel") {
                dialog.close()
            }

        form.add(templateNameField, subjectField, bodyField, variablesField, saveButton, cancelButton)
        dialog.add(form)
        dialog.open()
    }

    private fun openEditDialog(template: EmailTemplate) {
        val dialog = Dialog()
        val form = FormLayout()

        val templateNameField =
            TextField("Template Name").apply {
                value = template.templateName
            }
        val subjectField =
            TextField("Subject").apply {
                value = template.subject
            }
        val bodyField =
            TextArea("Body").apply {
                value = template.body
            }
        val variablesField =
            TextField("Variables (comma-separated)").apply {
                value = template.variables.joinToString(", ")
            }

        val saveButton =
            Button("Save") {
                PostTemplateUseCaseIn(
                    id = template.id,
                    templateName = templateNameField.value,
                    subject = subjectField.value,
                    version = template.version + 1,
                    body = bodyField.value,
                    variables = variablesField.value.split(",").map { it.trim() },
                ).let {
                    postTemplateUseCase.execute(it)
                }
                grid.setItems(emailTemplateRepository.findAll())
                dialog.close()
            }

        val cancelButton =
            Button("Cancel") {
                dialog.close()
            }

        form.add(templateNameField, subjectField, bodyField, variablesField, saveButton, cancelButton)
        dialog.add(form)
        dialog.open()
    }

    private fun openHistoryDialog(template: EmailTemplate) {
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

        val historyGrid =
            Grid<EmailTemplateHistory>().apply {
                removeAllColumns()
                setSizeFull()
            }

        val histories = emailTemplateHistoryRepository.findAllByTemplateId(template.id!!)
        historyGrid.setItems(histories)

        historyGrid.addColumn(EmailTemplateHistory::templateId).setHeader("Template ID")
        historyGrid.addColumn(EmailTemplateHistory::subject).setHeader("Subject")
        historyGrid
            .addComponentColumn { history ->
                TextArea().apply {
                    value = history.body
                    isReadOnly = true
                    width = "100%"
                    style.set("min-height", "100px")
                }
            }.setHeader("Body")
            .setAutoWidth(true)

        historyGrid.addColumn { it.variables.joinToString(", ") }.setHeader("Variables")
        historyGrid.addColumn(EmailTemplateHistory::version).setHeader("Version")
        historyGrid.addColumn(EmailTemplateHistory::createdAt).setHeader("Created At")

        val closeButton =
            Button("Close") {
                dialog.close()
            }

        layout.add(historyGrid, closeButton)
        dialog.add(layout)
        dialog.open()
    }
}