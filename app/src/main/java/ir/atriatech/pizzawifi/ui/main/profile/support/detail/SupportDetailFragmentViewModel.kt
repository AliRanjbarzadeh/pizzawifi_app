package ir.atriatech.pizzawifi.ui.main.profile.support.detail

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

class SupportDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Support
    var mListItems = mutableListOf<Support>()
    var mAdapter: SupportDetailAdapter? = null
    var mMessage = ""

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Support>>> by lazy {
        repository.supportDetailOutcome.toLiveData(
            bag
        )
    }
    val mObserverReply: LiveData<Outcome<Support>> by lazy {
        repository.supportReplyOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        when {
            mAdapter == null || mListItems.isEmpty() -> {
                repository.supportDetail(mItem.id, bag)
                return false
            }
        }
        return true
    }

    fun sendReply() {
        if (mMessage.isEmpty()) {
            eToast(findString(R.string.emptyMessage))
            return
        }
        repository.supportReply(mItem.id, mMessage, bag)
    }

    fun sendNotice() {
        repository.supportNotice(mItem.id, bag)
    }
}
