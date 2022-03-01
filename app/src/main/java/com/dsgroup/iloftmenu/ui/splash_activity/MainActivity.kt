package com.dsgroup.iloftmenu.ui.splash_activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dsgroup.iloftmenu.databinding.ActivityMainBinding
import com.dsgroup.iloftmenu.model.MenuElement
import com.dsgroup.iloftmenu.ui.menu_activity.MenuActivity
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements


class MainActivity : AppCompatActivity() {

    private lateinit var jInterface: MyJavaScriptInterface
    private lateinit var mainViewModel: MainViewModel
    private val HOST_IP = "2.35.2.221"

    private var url =
        "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=5efb2f1875008f0017df1713"
    private lateinit var _binding: ActivityMainBinding


    @DelicateCoroutinesApi
    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = _binding.root
        setContentView(view)

        mainViewModel =
            ViewModelProvider(
                this,
                MainViewModelFactory()
            )[MainViewModel::class.java]
        mainViewModel.menuList.value = ArrayList<MenuElement>()

        jInterface = MyJavaScriptInterface(applicationContext, mainViewModel)
        val webView = _binding.webView
        webView.settings.javaScriptEnabled = true

        mainViewModel.menuList.observe(this, {
            println("list changed")
        })
        mainViewModel.counter.observe(this) {
            println("endLoadingImage $it")
            if (it == mainViewModel.descrArray.size && it != 0) {
                var intenttest = Intent(this, MenuActivity::class.java)
                intenttest.putExtra(
                    "MenuList",
                    mainViewModel.menuList.value as ArrayList<MenuElement>
                )
                startActivity(intenttest)
            }
        }


        webView.webViewClient = MyWebViewClient()
        webView.loadUrl(url)
        webView.addJavascriptInterface(jInterface, "HtmlViewer")


    }

    class MyJavaScriptInterface internal constructor(
        private val ctx: Context,
        private val mainViewModel: MainViewModel
    ) {
        var html: String? = null
        private var handlerForJavascriptInterface: Handler = Handler()
        private var alreadyLoaded = false


        private var menuEl: ArrayList<MenuElement> = ArrayList<MenuElement>()

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
            GlobalScope.launch(Dispatchers.Main) {
                val doc = Jsoup.parse(html)

                val title: String = doc.title()

                val links: Elements = doc.select("pre")
                var i = 0
                for (link in links) {
                    if (link.`is`("pre.meal-description")) {
                        mainViewModel.descrArray.add(link.text())
                        println("Descrizione2: ${link.text()};")

                    } else if (link.`is`("pre.mb-0")) {
                        if(link.text().contains("ROSSO AL CALICE")){
                            mainViewModel.descrArray.add("Refosco terre passeri montepulciano d'abruzzo")

                        }
                        mainViewModel.titleArray.add((link.text()))
                        println("Title2: ${link.text()};")
                    }

                }
                println("${mainViewModel.descrArray.size} ${mainViewModel.titleArray.size}")

                for (j in 0 until mainViewModel.descrArray.size) {

                    println(
                        "Title: ${mainViewModel.titleArray[j]};  Descrizione: ${mainViewModel.descrArray[j]}; ;"
                    )
                    mainViewModel.addMenuToList(
                        MenuElement(
                            mainViewModel.titleArray[j],
                            mainViewModel.descrArray[j],
                            ""
                        )
                    )


                }
                println(
                    mainViewModel.menuList.value?.size
                )
                getImage(mainViewModel, mainViewModel.descrArray)

//                GlobalScope.launch(Dispatchers.IO) {
//                    getImage(descrArray)
//                }

            }
        }

    }


}

fun getImage(mainViewModel: MainViewModel, imageName: ArrayList<String>) {
    println(imageName.size)
    val imageArray: ArrayList<String> = ArrayList<String>()
    val width = 600
    val height = 600

    for ((k, url) in imageName.withIndex()) {
        val webURL = ("https://www.google.com/search?tbm=isch&q="
                + imageName[k])
        var src = ""

        val j = GlobalScope.launch(Dispatchers.IO) {
                val doc: Document = Jsoup.connect(webURL)
                    .userAgent("Mozilla")
                    .get()
                val img: Elements = doc.getElementsByTag("img")
                src = img[1].absUrl("src")
                println(src)
            withContext(Dispatchers.Main) {
                mainViewModel.updateList(k, src)
            }


            }
//        println("src attribute is: $src")

    }
}

class MyWebViewClient : WebViewClient() {

    @SuppressLint("JavascriptInterface")
    override fun onPageFinished(webView: WebView?, url: String?) {

        //Load HTML
        println("finished")
        Handler(Looper.getMainLooper()).postDelayed({
            webView?.loadUrl(
                "javascript:window.HtmlViewer.showHTML('&lt;html&gt;'+document.getElementsByTagName('html')[0].innerHTML+'&lt;/html&gt;');"
            )
        }, 500)


    }
}



