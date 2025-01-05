package com.few.crm.email.relay.send

import event.message.Message
import event.message.MessagePayload

class EmailSendMessage(
    payload: MessagePayload,
) : Message(
        payload = payload,
    )