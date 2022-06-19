package ir.atriatech.pizzawifi.ui.main.orders.survey

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.survey.Question
import ir.atriatech.pizzawifi.entities.orders.survey.SurveyJson
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class SurveyFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Order
    var mAdapter: SurveyAdapter? = null
    var mListItems = mutableListOf<Question>()

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<MutableList<Question>>> by lazy {
        repository.surveyOutcome.toLiveData(
            bag
        )
    }
    val mAddObserver: LiveData<Outcome<Msg>> by lazy { repository.surveyAddOutcome.toLiveData(bag) }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (mAdapter == null) {
            repository.survey(bag)
            return false
        }
        return true
    }

    fun add() {
        val answers: MutableList<SurveyJson> = ArrayList()
        var description = ""
        for (mListItem in mListItems) {
            if (mListItem.id != 0) {
                answers.add(SurveyJson(questionId = mListItem.id, rate = mListItem.rating))
            } else {
                description = mListItem.description
            }
        }
        repository.surveyAdd(mItem.id, gson.toJson(answers), description, bag)
    }
}
