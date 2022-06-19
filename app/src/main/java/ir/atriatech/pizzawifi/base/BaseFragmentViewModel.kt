package ir.atriatech.pizzawifi.base

import com.google.gson.Gson
import ir.atriatech.core.base.BaseCoreFragmentViewModel
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.common.network.RequestService
import ir.atriatech.util.others.image.ImageLoader
import javax.inject.Inject

open class BaseFragmentViewModel : BaseCoreFragmentViewModel() {
	//region Dagger Implementation
	@Inject
	lateinit var appDataBase: AppDataBase

	@Inject
	lateinit var imageLoader: ImageLoader

	@Inject
	lateinit var requestService: RequestService

	@Inject
	lateinit var gson: Gson

	/**
	 * use component.inject(this) in activity that you want to use @Inject parameters
	 */
	val component by lazy { AppDH.baseComponent() }
	//endregion

	var userInfo = AppObject.userInfo
}



