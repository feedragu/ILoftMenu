package com.dsgroup.iloftmenu

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {

    private lateinit var jInterface: MyJavaScriptInterface
    private lateinit var url: String
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
        webView.addJavascriptInterface(jInterface, "HtmlViewer")

        url =
            "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"

        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(url)
        println(jInterface.html)


    }

    class MyJavaScriptInterface internal constructor(private val ctx: Context) {
        var html: String? = null
        private var handlerForJavascriptInterface: Handler = Handler()

        @RequiresApi(Build.VERSION_CODES.O)
        @JavascriptInterface
        fun showHTML(_html: String?) {
            html = _html
            handlerForJavascriptInterface.post {
//            println("Page has been loaded in webview. html content :$html")
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
//                println("Title $title")
//                println("HTML $doc")
                val links: Elements = doc.select("pre")
//                println("links $links")
                for (link in links) {
                    println(" div ${link.text()}")
                }
                // clean up resources
            }
        }

    }


}

class MyWebViewClient : WebViewClient() {

    @SuppressLint("JavascriptInterface")
    override fun onPageFinished(webView: WebView?, url: String?) {

        //Load HTML
        println("finished")
        webView?.loadUrl(
            "javascript:window.HtmlViewer.showHTML" +
                    "('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');"
        )

    }
}



