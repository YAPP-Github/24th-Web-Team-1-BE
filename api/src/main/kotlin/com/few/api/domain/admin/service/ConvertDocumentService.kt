package com.few.api.domain.admin.service

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.springframework.stereotype.Service

@Service
class ConvertDocumentService {

    companion object {
        val parser = Parser.builder().build()!!
        val htmlRenderer = HtmlRenderer.builder().build()!!
        const val ARTICLE = "<article class='flex flex-col gap-1 body2-regular'> </article>"
    }

    fun mdToHtml(md: String): String {
        val html = Jsoup.parse(ARTICLE)
        val article = htmlRenderer.render(parser.parse(md))
        html.getElementsByTag("article").append(article)
        html.getElementsByTag("h1").forEach {
            it.attr("style", "font-size: 20px; line-height: 140%; font-weight: 600")
        }
        html.getElementsByTag("h2").forEach {
            it.attr("style", "font-size: 20px; line-height: 140%; font-weight: 600")
        }
        html.getElementsByTag("h3").forEach {
            it.attr("style", "font-size: 20px; line-height: 140%; font-weight: 600")
        }
        html.getElementsByTag("img").forEach {
            it.attr("style", "max-height: 280px; object-fit: contain; max-width: 480px; margin-left: auto; margin-right: auto; width: 100%;")
        }
        html.getElementsByTag("article").forEach {
            it.attr("style", "max-width: 480px; font-size: 15px; line-height: 170%; font-weight: 400;")
        }
        html.getElementsByTag("a").forEach {
            it.attr("style", "overflow: hidden; word-break: break-all;")
        }
        return html.body().html()
    }
}