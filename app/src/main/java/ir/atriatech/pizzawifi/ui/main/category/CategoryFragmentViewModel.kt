package ir.atriatech.pizzawifi.ui.main.category

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject

class CategoryFragmentViewModel : BaseFragmentViewModel() {
	lateinit var mAdapter: CategoryAdapter
	var cityModel = AppObject.cityItem!!

	init {
		component.inject(this)
	}
}
