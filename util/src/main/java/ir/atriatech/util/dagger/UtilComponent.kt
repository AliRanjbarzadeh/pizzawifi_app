package ir.atriatech.util.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import ir.atriatech.core.CoreScheduler
import ir.atriatech.util.dagger.network.NetworkModule
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface UtilComponent {
	fun context(): Context
	fun retrofit(): Retrofit
	fun scheduler(): CoreScheduler
	fun gson(): Gson
}