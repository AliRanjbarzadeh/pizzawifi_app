package ir.atriatech.pizzawifi.ui.main.home.customer.detail.comment

import android.net.Uri
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.network.createPartFromString
import ir.atriatech.extensions.network.createPartFromUri
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.repository.MainRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class AddCommentFragmentViewModel : BaseFragmentViewModel() {
    var id = 0
    var rating: Float = 1f
    var comment: String = ""
    var uri: Uri? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Msg>> by lazy { repository.commentAddOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun addComment() {
        if (rating < 1) {
            eToast(findString(R.string.rateError))
            return
        }
        if (comment.isEmpty()) {
            eToast(findString(R.string.commentEmptyError))
            return
        }

        val fileUri: MultipartBody.Part? = if (uri != null) {
            createPartFromUri(uri!!, "image")
        } else null

        val id2 = createPartFromString(id.toString())
        val rating2 = createPartFromString(rating.toString())
        val comment2 = createPartFromString(comment)

        repository.commentAdd(id2, rating2, comment2, fileUri, bag)
    }
}
