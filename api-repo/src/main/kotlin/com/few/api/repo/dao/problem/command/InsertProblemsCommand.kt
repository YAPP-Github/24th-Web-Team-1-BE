package com.few.api.repo.dao.problem.command

import com.few.api.repo.dao.problem.support.Contents

data class InsertProblemsCommand(
    val articleId: Long,
    val createrId: Long,
    val title: String,
    val contents: Contents,
    val answer: String,
    val explanation: String,
)