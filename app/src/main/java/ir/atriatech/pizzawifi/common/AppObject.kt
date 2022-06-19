package ir.atriatech.pizzawifi.common

import ir.atriatech.extensions.DEFAULT_LANGUAGE
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.UserInfo

object AppObject {
	var language: String = DEFAULT_LANGUAGE
	var branchItem: Branch? = null
	var selectedBranchId: Int = 0
	var cityItem: CityModel? = null
	var selectedCityId: Int = 0
	lateinit var userInfo: UserInfo
	var isLogin: Boolean = false
	var isShowKeyboard = false
}