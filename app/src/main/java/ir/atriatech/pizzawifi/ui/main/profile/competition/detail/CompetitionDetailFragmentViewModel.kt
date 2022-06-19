package ir.atriatech.pizzawifi.ui.main.profile.competition.detail

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.competition.Competition
import ir.atriatech.pizzawifi.entities.profile.competition.CompetitionQuestion
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class CompetitionDetailFragmentViewModel : BaseFragmentViewModel() {
    var item: Competition? = null

    var mListItems: MutableList<CompetitionQuestion> = mutableListOf()
    var mAdapter: CompetitionQuestionAdapter? = null

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Competition>> by lazy {
        repository.competitionDetailOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (mAdapter == null) {
            repository.competitionDetail(item!!.id, bag)
            return false
        }
        return true
    }
}
