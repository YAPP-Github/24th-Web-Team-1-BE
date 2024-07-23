package com.few.api.client.repo.dto

import java.lang.Exception

data class RepoAlterArgs(
    val exception: Exception,
    val requestURL: String,
    val query: String?,
)