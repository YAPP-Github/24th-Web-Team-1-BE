package com.few.storage.document.service

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.springframework.stereotype.Service

@Service
class ConvertDocumentService {

    companion object {
        val parser = Parser.builder().build()
        val htmlRenderer = HtmlRenderer.builder().build()
    }

    fun mdToHtml(md: String): String {
        val article = htmlRenderer.render(parser.parse(md))
        return article.toString()
    }
}