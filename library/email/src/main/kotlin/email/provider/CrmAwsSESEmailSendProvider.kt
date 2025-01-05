package email.provider

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService
import org.springframework.stereotype.Component

@Component
class CrmAwsSESEmailSendProvider(
    amazonSimpleEmailService: AmazonSimpleEmailService,
) : AwsSESEmailSendProvider(
        amazonSimpleEmailService,
    ) {
    override fun getWithConfigurationSetName(): String = "few-crm-configuration-set"
}