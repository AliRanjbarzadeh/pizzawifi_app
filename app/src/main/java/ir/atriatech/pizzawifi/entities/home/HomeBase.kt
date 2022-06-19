package ir.atriatech.pizzawifi.entities.home

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.HomeExtra

data class HomeBase(
    @SerializedName("popup")
    @Expose
    var homePopup: HomePopup? = HomePopup(),
    @SerializedName("extras")
    @Expose
    var homeExtra: HomeExtra = HomeExtra(),
    @SerializedName("order_buttons")
    @Expose
    var homeButtons: HomeButtons = HomeButtons(),
    @SerializedName("home_categories")
    @Expose
    var homeCategories: MutableList<HomeCategory> = mutableListOf(),
    @SerializedName("tournament")
    @Expose
    var tournamentHome: TournamentHome = TournamentHome(),
    @SerializedName("categories")
    @Expose
    var categories: MutableList<Category> = mutableListOf(),
    @SerializedName("blog_categories")
    @Expose
    var blogCategories: MutableList<BlogCategory> = mutableListOf(),
    @SerializedName("cities")
    @Expose
    var cities: MutableList<CityModel> = mutableListOf()
)
