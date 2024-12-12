package email

import email.provider.EmailSendProvider
import org.springframework.boot.autoconfigure.mail.MailProperties

abstract class EmailSender<T : SendMailArgs<*, *>>(
    private val mailProperties: MailProperties,
    private val defaultEmailSendProvider: EmailSendProvider,
) {

    fun send(args: T, emailSendProvider: EmailSendProvider? = null): String {
        val from = mailProperties.username
        val to = args.to
        val subject = args.subject
        val message = getHtml(args)
        return emailSendProvider?.sendEmail("FEW Letter <$from>", to, subject, message)
            ?: run {
                defaultEmailSendProvider.sendEmail(
                    "FEW Letter <$from>",
                    to,
                    subject,
                    message
                )
            }
    }

    abstract fun getHtml(args: T): String
}