package ir.atriatech.pizzawifi.ui.main.profile.support.add

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.support.Support
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class AddSupportFragmentViewModel : BaseFragmentViewModel() {
    var mTitle = ""
    var mMessage = ""

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Support>> by lazy {
        repository.supportReplyOutcome.toLiveData(
            bag
        )
    }


    init {
        component.inject(this)
    }

    fun sendSupport() {
        if (mTitle.isEmpty()) {
            eToast(findString(R.string.emptyTitle))
            return
        }

        if (mMessage.isEmpty()) {
            eToast(findString(R.string.emptyMessage))
            return
        }
        repository.supportAdd(mTitle, mMessage, bag)
    }

    fun sendNotice(id: Int) {
        repository.supportNotice(id, bag)
    }
}
