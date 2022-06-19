package ir.atriatech.pizzawifi.common.dagger.base_app

import android.content.Context
import androidx.room.Room
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.BuildConfig
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.common.network.RequestService
import retrofit2.Retrofit

@Module
class BaseAppModule {
    //Composite disposable
    @Provides
    @BaseAppScope
    fun compositeDisposable(): CompositeDisposable = CompositeDisposable()

    //Request service
    @Provides
    @BaseAppScope
    fun publicRequestService(retrofit: Retrofit): RequestService =
        retrofit.create(RequestService::class.java)

    @Provides
    @BaseAppScope
    fun providesPicasso(): Picasso = Picasso.get()


    @Provides
    @BaseAppScope
    fun providesAppDatabase(context: Context): AppDataBase =
        Room.databaseBuilder(context, AppDataBase::class.java, BuildConfig.DATABASE_NAME)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
}