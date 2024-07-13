package com.few.api.exception.properties

class NotSetPropertyException(property: String) : RuntimeException("$property is not set")