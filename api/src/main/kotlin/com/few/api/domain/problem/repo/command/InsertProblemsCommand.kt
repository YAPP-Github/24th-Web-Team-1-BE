package com.few.api.domain.problem.repo.command

import com.few.api.domain.problem.repo.support.Contents

data class InsertProblemsCommand(
    val articleId: Long,
    val createrId: Long,
    val title: String,
    val contents: Contents,
    val answer: String,
    val explanation: String,
)