package com.few.storage.document.service

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class ConvertDocumentService {

    companion object {
        val parser = Parser.builder().build()
        val htmlRenderer = HtmlRenderer.builder().build()
        val ARTICLE = "<article class='flex flex-col gap-1 body2-regular'> </article>"
    }

    fun mdToHtml(md: String): String {
        val html = Jsoup.parse(ARTICLE)
        val article = htmlRenderer.render(parser.parse(md))
        html.body().append(article)
        return html.toString()
    }
}