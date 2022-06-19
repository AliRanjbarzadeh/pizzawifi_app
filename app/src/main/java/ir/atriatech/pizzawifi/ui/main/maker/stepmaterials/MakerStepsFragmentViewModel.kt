package ir.atriatech.pizzawifi.ui.main.maker.stepmaterials

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.maker.Step

class MakerStepsFragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: MakerStepMaterialsAdapter? = null
    var mRightAdapter: MakerStepMaterialsAdapter? = null
    var mLeftAdapter: MakerStepMaterialsAdapter? = null
    var mItem = ObservableField<Step>()
    var isHalf: Boolean = false

    fun checkIsHalf(): Boolean {
        return isHalf && mItem.get()!!.half == 1
    }

    init {
        component.inject(this)
    }
}
