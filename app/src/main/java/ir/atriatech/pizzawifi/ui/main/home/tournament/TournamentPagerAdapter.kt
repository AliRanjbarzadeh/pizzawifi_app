package ir.atriatech.pizzawifi.ui.main.home.tournament

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ir.atriatech.pizzawifi.entities.home.tournament.Question
import ir.atriatech.pizzawifi.ui.main.home.tournament.questions.TournamentQuestionFragment

class TournamentPagerAdapter(fm: FragmentManager, private val dataList: MutableList<Question>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return TournamentQuestionFragment.newInstance(dataList[position])
    }

    override fun getCount(): Int {
        return dataList.size
    }
}
