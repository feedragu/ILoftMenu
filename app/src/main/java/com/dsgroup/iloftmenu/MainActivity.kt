package com.dsgroup.iloftmenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dsgroup.iloftmenu.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.lang.StringBuilder


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
    @DelicateCoroutinesApi
    fun getWebsite() {
        GlobalScope.launch (Dispatchers.IO){
            val builder = StringBuilder()

            try {
                val doc: Document = Jsoup.connect("https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=" +
                        "5efb2f1875008f0017df1713").get()
                val title: String = doc.title()
                println("Title $title")
                val links: Elements = doc.select("meal-description mb-2 text-justify pr-5")
                println("links: $links")
                builder.append(title).append("\n")
                for (link in links) {
                    builder.append("\n").append("Link : ").append(link.attr("href"))
                        .append("\n").append("Text : ").append(link.text())
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}