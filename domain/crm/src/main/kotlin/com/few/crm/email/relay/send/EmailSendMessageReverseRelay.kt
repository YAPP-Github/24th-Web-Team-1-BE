package com.few.crm.email.relay.send

import com.few.crm.email.event.send.EmailSendEvent
import event.message.MessageReverseRelay

abstract class EmailSendMessageReverseRelay : MessageReverseRelay<EmailSendEvent>