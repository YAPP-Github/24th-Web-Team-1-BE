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
        html.getElementsByTag("article").append(article)
        html.getElementsByTag("h1").forEach {
            it.addClass("sub1-semibold")
        }
        html.getElementsByTag("h2").forEach {
            it.addClass("sub1-semibold")
        }
        html.getElementsByTag("h3").forEach {
            it.addClass("sub1-semibold")
        }
        html.getElementsByTag("img").forEach {
            it.attr("style", "max-height: 260px; object-fit: contain;")
        }
        return html.body().html()
    }
}