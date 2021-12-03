package com.dsgroup.iloftmenu.ui.splash_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dsgroup.iloftmenu.model.MenuElement

class MainViewModel : ViewModel() {

    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.


    var menuList: MutableLiveData<MutableList<MenuElement>> = MutableLiveData()
    var intList: MutableLiveData<MutableList<Int>> = MutableLiveData()
    var titleArray: ArrayList<String> = ArrayList<String>()
    var descrArray: ArrayList<String> = ArrayList<String>()
    var imageArray: ArrayList<String> = ArrayList<String>()
    var endLoadingImage = MutableLiveData(0)
    var counter = MutableLiveData(0)

    fun addMenuToList(menuElement: MenuElement) {
        menuList.value?.add(menuElement)
        // so that the observer can see that the list has changed
        menuList.value = menuList.value
    }

    fun updateList(num: Int, image: String) {
        menuList.value?.get(num)?.image = image
        counter.value = counter.value!! +1
        // so that the observer can see that the list has changed
        menuList.value = menuList.value
    }

}

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}