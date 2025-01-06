package com.few.crm.config

import com.vaadin.flow.spring.annotation.EnableVaadin
import org.springframework.context.annotation.Configuration

@Configuration
@EnableVaadin(value = [CrmConfig.BASE_PACKAGE])
class CrmVaadinConfig