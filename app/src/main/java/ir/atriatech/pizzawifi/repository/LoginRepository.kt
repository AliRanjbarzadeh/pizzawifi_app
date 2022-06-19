package ir.atriatech.pizzawifi.repository

import android.content.Context
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.base.outCome
import ir.atriatech.pizzawifi.base.BaseRepository
import ir.atriatech.pizzawifi.common.network.RequestService
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val requestService: RequestService,
    context: Context
) : BaseRepository(context) {
    var msgOutcome = outCome<Msg>()
    var smsOutcome = outCome<Msg>()
    var resendCodeOutcome = outCome<Msg>()
    var verifyCodeOutcome = outCome<Msg>()
    var registerOutcome = outCome<Msg>()

    fun login(mobile: String, bag: CompositeDisposable) =
        requestToNetwork(msgOutcome, requestService.login(mobile), bag)

    fun sendCode(mobile: String, bag: CompositeDisposable) =
        requestToNetwork(smsOutcome, requestService.sendCode(mobile), bag)

    fun resendCode(mobile: String, bag: CompositeDisposable) =
        requestToNetwork(resendCodeOutcome, requestService.resendCode(mobile), bag)

    fun verifyCode(mobile: String, code: String, bag: CompositeDisposable) =
        requestToNetwork(verifyCodeOutcome, requestService.verifyCode(mobile, code), bag)

    fun register(name: String, introducer: String, bag: CompositeDisposable) =
        requestToNetwork(registerOutcome, requestService.register(name, introducer), bag)
}