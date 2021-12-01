package com.dsgroup.iloftmenu

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dsgroup.iloftmenu.databinding.ActivityMainBinding
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        _binding.getBtn.setOnClickListener {
            getWebsite()
        }


    }

    @SuppressLint("SetJavaScriptEnabled")
    @DelicateCoroutinesApi
    fun getWebsite() {
        GlobalScope.launch(Dispatchers.IO) {
            val builder = StringBuilder()

            try {
                val webClient = WebClient(BrowserVersion.CHROME)
                webClient.options.isJavaScriptEnabled = true // enable javascript

                webClient.options.isThrowExceptionOnScriptError =
                    false //even if there is error in js continue

                webClient.waitForBackgroundJavaScript(200) // important! wait until javascript finishes rendering
                val url =
                    "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"
                val page: HtmlPage = webClient.getPage(url)
                val doc  = Jsoup.parse(page.asXml())

                val title: String = doc.title()
                println("Title $title")
                val links: Elements = doc.select("pre")
                builder.append(title).append("\n")
                for (link in links) {
                    builder.append("\n").append("Link : ").append(link.attr("href"))
                        .append("\n").append("Text : ").append(link.text())
                }
                // clean up resources

                // clean up resources
                webClient.closeAllWindows()
//                page.getElementsByName("pre")
                println("page ${page.getElementsByIdAndOrName("div")}")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}