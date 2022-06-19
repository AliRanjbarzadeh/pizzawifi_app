package ir.atriatech.pizzawifi.ui.main.home.tournament

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.tournament.QuestionJson
import ir.atriatech.pizzawifi.entities.home.tournament.Tournament
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class TournamentFragmentViewModel : BaseFragmentViewModel() {
    var mItem = ObservableField<Tournament>()
    var mAdapter: TournamentPagerAdapter? = null
    var isLoaded = false

    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Tournament>> by lazy {
        repository.competitionGetOutcome.toLiveData(
            bag
        )
    }
    val mObserverSave: LiveData<Outcome<Msg>> by lazy {
        repository.competitionSaveOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData(): Boolean {
        if (!isLoaded) {
            repository.competitionGet(bag)
            return false
        }
        return true
    }

    fun saveData() {
        val questions = mutableListOf<QuestionJson>()
        for (question in mItem.get()!!.questions) {
            val questionJson = QuestionJson(question.id)
            for (answer in question.answers) {
                if (answer.isSelected) {
                    questionJson.answerId = answer.id
                    break
                }
            }
            questions.add(questionJson)
        }
        repository.competitionSave(mItem.get()!!.id, gson.toJson(questions), bag)
    }
}
