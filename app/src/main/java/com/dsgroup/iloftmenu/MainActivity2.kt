package com.dsgroup.iloftmenu

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dsgroup.iloftmenu.databinding.ActivityMainBinding
import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.html.HtmlAnchor
import com.gargoylesoftware.htmlunit.html.HtmlDivision
import com.gargoylesoftware.htmlunit.html.HtmlPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.net.URL


class MainActivity2 : AppCompatActivity() {

    private var page: HtmlPage? = null
    private lateinit var _binding: ActivityMainBinding
    val url =
        "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        _binding.getBtn.setOnClickListener {
            getWebsite()
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    @DelicateCoroutinesApi
    fun getWebsite() {
        GlobalScope.launch(Dispatchers.IO) {
            val builder = StringBuilder()

            try {
                val webClient = getConfiguredWebClient()
                webClient!!.options.isJavaScriptEnabled = true // enable javascript

                webClient.options.isThrowExceptionOnScriptError =
                    false //even if there is error in js continue

                webClient.waitForBackgroundJavaScript(1000) // important! wait until javascript finishes rendering
                getNextPage(webClient)

                webClient.addWebWindowListener(MyWebWindowListener(webClient, url))
                val page: HtmlPage = webClient.getPage(url)
                val doc = Jsoup.parse(page.asXml())
                getParse(doc.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    @Throws(IOException::class)
    private fun getNextPage(pClient: WebClient) {
        GlobalScope.launch(Dispatchers.IO) {
                val apagLanding: HtmlPage = pClient.getPage(url)
                val anchor = apagLanding.getElementById("__nuxt") as HtmlDivision
                val apagAllAds = anchor.click<HtmlPage>()
                try {
                    println("Pausing for JavaScript execution to return page . . .")
                    Thread.sleep((1000 * 2).toLong())
                } catch (e: InterruptedException) {
                    println(
                        "InterruptedException encountered (non-critical condition) . . ."
                    )
                }
                page = apagAllAds
                println("page ${apagAllAds.asXml()}")


        }
    }

    class MyWebWindowListener(val webClient: WebClient, val url: String) : WebWindowListener {
        override fun webWindowOpened(event: WebWindowEvent?) {
            TODO("Not yet implemented")
        }

        override fun webWindowContentChanged(event: WebWindowEvent?) {

        }

        override fun webWindowClosed(event: WebWindowEvent?) {
            TODO("Not yet implemented")
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetJavaScriptEnabled")
    @DelicateCoroutinesApi
    fun getParse(html: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val doc = Jsoup.parse(html)

            val title: String = doc.title()
//                println("Title $title")
//                println("HTML $doc")
            val links: Elements = doc.select("pre")
//                println("links $links")
            for (link in links) {
                println("pre  ${link.text()}")
//                getImage(link.text())
            }
            // clean up resources
        }
    }



    @SuppressLint("SetJavaScriptEnabled")
    private fun getConfiguredWebClient(): WebClient? {
        var aClient: WebClient? = null
        aClient = WebClient(BrowserVersion.CHROME)

        aClient.waitForBackgroundJavaScript((3 * 1000).toLong()) // Experimental API: May be changed in next release and may not yet work perfectly!
        aClient.options.isCssEnabled = true
        aClient.options.isJavaScriptEnabled = true
        aClient.ajaxController = NicelyResynchronizingAjaxController()

        return aClient
    }

}



