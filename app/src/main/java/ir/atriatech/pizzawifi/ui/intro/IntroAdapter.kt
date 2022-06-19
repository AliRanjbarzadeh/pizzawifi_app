package ir.atriatech.pizzawifi.ui.intro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.databinding.TempIntroBinding


class IntroAdapter(private val mainList: List<IntroItem>) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding: TempIntroBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container.context),
            R.layout.temp_intro, container, false
        )
        binding.item = mainList[position]

        binding.imgIntro.setImageResource(mainList[position].image)
        binding.executePendingBindings()

        container.addView(binding.root)

        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeViewAt(position)
    }

    override fun isViewFromObject(p0: View, p1: Any): Boolean {
        return p0 == p1
    }

    override fun getCount(): Int {
        return mainList.size
    }
}