package ir.atriatech.util.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import ir.atriatech.core.CoreScheduler
import ir.atriatech.core.CoreSchedulerImpl
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {
	@Provides
	@Singleton
	fun providesContext(): Context {
		return context
	}

	@Provides
	@Singleton
	fun providesScheduler(): CoreScheduler {
		return CoreSchedulerImpl()
	}
}