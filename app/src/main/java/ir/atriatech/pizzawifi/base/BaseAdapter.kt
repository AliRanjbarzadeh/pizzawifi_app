package ir.atriatech.pizzawifi.base

import ir.atriatech.core.base.BaseCoreAdapter
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.util.others.image.ImageLoader
import javax.inject.Inject

abstract class BaseAdapter<T> : BaseCoreAdapter<T>() {
    val component by lazy { AppDH.baseComponent() }

    @Inject
    lateinit var imageLoader: ImageLoader
}



