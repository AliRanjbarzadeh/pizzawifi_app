package ir.atriatech.pizzawifi.entities.home

import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.pizzawifi.entities.CityModel

class HomeModel1(val recyclerViewTools: RecyclerViewTools) {
    var homeButtons: HomeButtons = HomeButtons()
    var isMaker = true
    var isRestaurantMenu = true
    var isCustomerMenu = true
    var cityModel: CityModel = CityModel()
}
