package ir.atriatech.pizzawifi.common.dagger

import ir.atriatech.pizzawifi.common.dagger.base_app.BaseAppComponent
import ir.atriatech.pizzawifi.common.dagger.base_app.DaggerBaseAppComponent
import ir.atriatech.util.UtilApp
import javax.inject.Singleton

@Singleton
object AppDH {
    /*========================================App=======================================*/
    private var baseApp: BaseAppComponent? = null

    fun baseAppComponent(): BaseAppComponent {

        if (baseApp == null)
            baseApp = DaggerBaseAppComponent.builder().utilComponent(UtilApp.utilComponent).build()

        return baseApp as BaseAppComponent
    }

    private var baseComponent: BaseComponent? = null

    fun baseComponent(): BaseComponent {
        if (baseComponent == null)
            baseComponent =
                DaggerBaseComponent.builder().baseAppComponent(baseAppComponent()).build()

        return baseComponent as BaseComponent
    }
}