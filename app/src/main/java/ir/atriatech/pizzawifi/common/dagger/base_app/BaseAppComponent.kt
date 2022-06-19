package ir.atriatech.pizzawifi.common.dagger.base_app

import android.content.Context
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.Component
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.CoreScheduler
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.common.network.RequestService
import ir.atriatech.util.dagger.UtilComponent
import retrofit2.Retrofit

@BaseAppScope
@Component(dependencies = [UtilComponent::class], modules = [BaseAppModule::class])
interface BaseAppComponent {
    fun context(): Context
    fun retrofit(): Retrofit
    fun scheduler(): CoreScheduler
    fun gson(): Gson
    fun compositeDisposable(): CompositeDisposable
    fun requestService(): RequestService
    fun picasso(): Picasso
    fun appDataBase(): AppDataBase
}