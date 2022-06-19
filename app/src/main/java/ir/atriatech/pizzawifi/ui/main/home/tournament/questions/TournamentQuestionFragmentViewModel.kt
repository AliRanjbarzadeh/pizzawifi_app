package ir.atriatech.pizzawifi.ui.main.home.tournament.questions

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.tournament.Question

class TournamentQuestionFragmentViewModel : BaseFragmentViewModel() {
    var mItem = ObservableField<Question>()
    var mAdapter: TournamentAnswerAdapter? = null
    var lastSelectedPosition = -1

    init {
        component.inject(this)
    }
}
