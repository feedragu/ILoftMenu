package com.dsgroup.iloftmenu

import android.os.Bundle
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


class MainActivity2 : AppCompatActivity() {

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
        GlobalScope.launch(Dispatchers.IO) {
            val builder = StringBuilder()

            try {
                val doc: Document = Jsoup.connect(
                    "https://www.tastenpic.com/menu/i-loft-cafe?menu=1&categoryId=" +
                            "5efb2f1875008f0017df1713"
                ).get()
                val title: String = doc.title()
                println("Title $title")
                val array: Elements = doc.select("script")
//                println("links: $links")
                println("size ${array.size}")
                for (link in array) {
                    if (link.toString().contains("__NUXT__")) {
                        println("Substring ${link.toString().indexesOf("PIATTO DEL GIORNO", true)}")
                        val ind = link.toString().indexesOf("PIATTO DEL GIORNO", true)
                    var sub = link.toString()
                        sub = sub.substring(ind[1])
                        println("Substring $sub")
                        println("Substring ${link.toString().indexesOf("LOFTINO", true)}")
                        val ind2 = link.toString().indexesOf("LOFTINO", true)
                        var sub2 = link.toString()
                        sub2 = sub2.substring(ind2[1])
                        println("Substring $sub2")
                        println("Substring ${link.toString().indexesOf("Penne", true)}")
                        val ind3= link.toString().indexesOf("Penne", true)
                        var sub3 = link.toString()
                        sub3 = sub3.substring(ind3[1])
                        println("Substring $sub3")
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    public fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
        val list = mutableListOf<Int>()
        if (this == null || substr.isBlank()) return list

        var i = -1
        while(true) {
            i = indexOf(substr, i + 1, ignoreCase)
            when (i) {
                -1 -> return list
                else -> list.add(i)
            }
        }
    }


}