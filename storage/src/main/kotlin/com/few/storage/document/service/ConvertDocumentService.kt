package com.few.storage.document.service

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import java.io.File

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
            it.addClass("sub1-semibold top bottom 5px")
        }
        html.getElementsByTag("h3").forEach {
            it.addClass("sub1-semibold")
        }
        html.getElementsByTag("img").forEach {
            it.addClass("!max-h-[260px] object-contain")
        }
        return html.body().html()
    }
}

fun main() {
    val convertDocumentService = ConvertDocumentService()
    val file =
        File("/Users/jongjun/Documents/Code/Spring/24th-Web-Team-1-BE/storage/src/main/resources/test.md")
    val md = file.readText()
    println(convertDocumentService.mdToHtml(md))
}