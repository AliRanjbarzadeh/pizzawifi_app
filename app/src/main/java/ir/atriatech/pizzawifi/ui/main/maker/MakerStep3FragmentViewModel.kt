package ir.atriatech.pizzawifi.ui.main.maker

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.maker.Bread
import ir.atriatech.pizzawifi.entities.home.maker.Maker
import ir.atriatech.pizzawifi.entities.home.maker.Step

class MakerStep3FragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: MakerStepsAdapter? = null
    var mItem = ObservableField<Maker>()
    var mBread = ObservableField<Bread>()
    var mCalory = ObservableField(0)
    var mStep = ObservableField<Step>()
    var isHalf = false

    init {
        component.inject(this)
    }
}
