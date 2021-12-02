package com.dsgroup.iloftmenu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.dsgroup.iloftmenu.databinding.ActivityMainBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var jInterface: MyJavaScriptInterface
    private var url =
        "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"
    private lateinit var _binding: ActivityMainBinding


    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        jInterface = MyJavaScriptInterface(applicationContext)
        val webView = _binding.webView
        webView.settings.javaScriptEnabled = true



        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(url)
        webView.addJavascriptInterface(jInterface, "HtmlViewer")
        println(jInterface.html)

    }

    class MyJavaScriptInterface internal constructor(private val ctx: Context) {
        var html: String? = null
        private var handlerForJavascriptInterface: Handler = Handler()
        private var alreadyLoaded = false

        private var titleArray: ArrayList<String> = ArrayList<String>()
        private var descrArray: ArrayList<String> = ArrayList<String>()
        private var imageArray: ArrayList<String> = ArrayList<String>()

        @RequiresApi(Build.VERSION_CODES.O)
        @JavascriptInterface
        fun showHTML(_html: String?) {
            html = _html
            if (!alreadyLoaded)
                alreadyLoaded = true
            else
                handlerForJavascriptInterface.post {
                    println("Page has been loaded in webview.")
                    html?.let { getWebsite(it) }
                }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetJavaScriptEnabled")
        @DelicateCoroutinesApi
        fun getWebsite(html: String) {
            GlobalScope.launch(Dispatchers.IO) {
                val doc = Jsoup.parse(html)

                val title: String = doc.title()

                val links: Elements = doc.select("pre")
                var i = 0
                for (link in links) {
                    if (link.`is`("pre.meal-description")) {
                        descrArray.add(link.text())

                    } else if (link.`is`("pre.mb-0")) {
                        titleArray.add((link.text()))
                    }

//                    when (link.text()) {
//                        "PIATTO DEL GIORNO"-> {
//
//                        }
//                    }
//                    getImage(link.text())
                }

                for (j in 0..descrArray.size) {
                    try {
                        println(
                            "Title: ${titleArray.get(j)};  Descrizione: ${descrArray.get(j)};  Immagine: ${
                                imageArray.get(j)
                            };"
                        )

                    } catch (e: Exception) {
//                        println("Title: ${titleArray.get(j)}")
                    }
                }
                GlobalScope.launch(Dispatchers.IO) { getImage(descrArray) }
            }
        }

    }


}

fun getImage(imageName: ArrayList<String>): ArrayList<String> {
    val imageArray: ArrayList<String> = ArrayList<String>()
    val width = 600
    val height = 600
    for (url in imageName) {
        val webURL = ("https://www.google.com/search?tbm=isch&q="
                + imageName)

        try {
            val doc: Document = Jsoup.connect(webURL)
                .userAgent("Mozilla")
                .get()
            val img: Elements = doc.getElementsByTag("img")
            val src = img[1].absUrl("src")
            println(src)
            imageArray.add(src)
//        println("src attribute is: $src")

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    return imageArray
}

class MyWebViewClient : WebViewClient() {

    @SuppressLint("JavascriptInterface")
    override fun onPageFinished(webView: WebView?, url: String?) {

        //Load HTML
        println("finished")
        Handler(Looper.getMainLooper()).postDelayed({
            webView?.loadUrl(
                "javascript:window.HtmlViewer.showHTML" +
                        "('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');"
            )
        }, 500)


    }
}



