package ir.atriatech.pizzawifi.base

import android.content.Context
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ir.atriatech.core.base.BaseCoreRepository
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.app.topActivity
import ir.atriatech.extensions.base.sendToRequest
import ir.atriatech.extensions.dialog.ConnectionCheck
import ir.atriatech.extensions.dialog.connectionCheck
import javax.inject.Inject

open class BaseRepository @Inject constructor(private val context: Context) : BaseCoreRepository() {

    fun <T> requestToNetwork(
        subject: PublishSubject<Outcome<T>>,
        single: Single<T>,
        disposable: CompositeDisposable,
        checkNetwork: Boolean = true
    ) {
        if (checkNetwork) {
            topActivity?.let {
                connectionCheck(object : ConnectionCheck {
                    override fun isConnected() {
                        sendToRequest(subject, single, disposable)
                    }
                }, it)
            }
        } else {
            sendToRequest(subject, single, disposable)
        }
    }


}