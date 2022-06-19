package ir.atriatech.pizzawifi.entities.home

import ir.atriatech.core.interfaces.RecyclerViewTools

class HomeModel2(var recyclerViewTools: RecyclerViewTools) {
    var id = 0
    var title = ""
    var color = ""
    var blogs: MutableList<Blog> = mutableListOf()
}
