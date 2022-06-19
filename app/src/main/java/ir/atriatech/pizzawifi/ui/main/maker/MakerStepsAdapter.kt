package ir.atriatech.pizzawifi.ui.main.maker

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import ir.atriatech.pizzawifi.entities.home.maker.Step
import ir.atriatech.pizzawifi.ui.main.maker.stepmaterials.MakerStepsFragment

class MakerStepsAdapter(fm: FragmentManager, private val dataList: List<Step>, private val isHalf: Boolean) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return MakerStepsFragment.newInstance(dataList[position], isHalf)
    }

    override fun getCount(): Int {
        return dataList.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return dataList[position].name
    }
}
