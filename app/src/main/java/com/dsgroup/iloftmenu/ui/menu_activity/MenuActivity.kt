package com.dsgroup.iloftmenu.ui.menu_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsgroup.iloftmenu.R
import com.dsgroup.iloftmenu.adapter.MenuListAdapter
import com.dsgroup.iloftmenu.databinding.ActivityMenuBinding
import com.dsgroup.iloftmenu.model.MenuElement
import com.google.android.material.snackbar.Snackbar

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var menuViewModel: MenuViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menuViewModel =
            ViewModelProvider(
                this,
                MenuViewModelFactory()
            )[MenuViewModel::class.java]

        val menuList: ArrayList<MenuElement> =
            intent.extras?.get("MenuList") as ArrayList<MenuElement>

        binding.layoutScrolling.recyclerView?.layoutManager = LinearLayoutManager(this)

        val adapter = MenuListAdapter(menuList, applicationContext)

        // Setting the Adapter with the recyclerview
        binding.layoutScrolling.recyclerView?.adapter = adapter

        println("size menulist ${menuList.size}")
        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }
}