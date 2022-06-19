package ir.atriatech.pizzawifi.ui.main.more

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.ui.MyMenuAdapter

class MoreFragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: MyMenuAdapter? = null

    init {
        component.inject(this)
    }
}
