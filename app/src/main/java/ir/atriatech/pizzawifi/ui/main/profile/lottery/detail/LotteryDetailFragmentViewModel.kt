package ir.atriatech.pizzawifi.ui.main.profile.lottery.detail

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.lottery.LotteryModel

class LotteryDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: LotteryModel

    init {
        component.inject(this)
    }
}
