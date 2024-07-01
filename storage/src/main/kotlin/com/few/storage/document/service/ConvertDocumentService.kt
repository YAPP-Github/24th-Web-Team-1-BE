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
        val HTML_HEADER =
            "<!DOCTYPE html> <html></html>"
    }

    fun mdToHtml(md: String): String {
        val html = Jsoup.parse(HTML_HEADER)
        val body = htmlRenderer.render(parser.parse(md))
        html.body().append(body)
        return html.toString()
    }
}