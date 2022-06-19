package ir.atriatech.pizzawifi.ui.main.maker

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.maker.Maker

class MakerStep2FragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: BreadAdapter? = null
    var mItem = ObservableField<Maker>()
    var mCalory = ObservableField(0)
    var isHalf = false

    init {
        component.inject(this)
    }
}
