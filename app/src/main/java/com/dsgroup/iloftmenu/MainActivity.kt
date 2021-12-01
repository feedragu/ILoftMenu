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
import com.gargoylesoftware.htmlunit.BrowserVersion
import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.IOException
import android.widget.Toast





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

        _binding.getBtn.setOnClickListener {
            getWebsite()
        }
        jInterface = MyJavaScriptInterface(applicationContext)
        val webView = _binding.webView
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(jInterface, "HtmlViewer")

        url =            "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"

        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(url)
        println(jInterface.html)


    }

    @RequiresApi(Build.VERSION_CODES.O)
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

                val num =
                    webClient.waitForBackgroundJavaScript(10000) // important! wait until javascript finishes rendering
                val num2 =
                    webClient.waitForBackgroundJavaScriptStartingBefore(10000) // important! wait until javascript finishes rendering
                println(num)
                println(num2)


                val page: HtmlPage = webClient.getPage(url)
                Thread.sleep(5000)

                val doc = Jsoup.parse(page.asXml())

                val title: String = doc.title()
//                println("Title $title")
//                println("HTML $doc")
                val links: Elements = doc.select("div")
//                println("links $links")
                for (link in links) {
                    println(" div $link")
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
    class MyWebViewClient : WebViewClient() {

        @SuppressLint("JavascriptInterface")
        override fun onPageFinished(webView: WebView?, url: String?) {

            //Load HTML
            println("finished")
            webView!!.addJavascriptInterface(this, "HtmlViewer")
            webView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                    "('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');");


        }
    }

}

class MyJavaScriptInterface internal constructor(private val ctx: Context) {
    var html: String? = null
    private var handlerForJavascriptInterface: Handler = Handler()
    @JavascriptInterface
    fun showHTML(_html: String?) {
        html = _html
        handlerForJavascriptInterface.post {
            val toast: Toast = Toast.makeText(
                ctx,
                "Page has been loaded in webview. html content :$html", Toast.LENGTH_LONG
            )
            toast.show()
            println("Page has been loaded in webview. html content :$html")
        }
    }

}
