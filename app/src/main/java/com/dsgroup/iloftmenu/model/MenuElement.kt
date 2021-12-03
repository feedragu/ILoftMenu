package com.dsgroup.iloftmenu.model

import java.io.Serializable


class MenuElement(
    val title: String,
    val descr: String,
    var image: String
) : Serializable
