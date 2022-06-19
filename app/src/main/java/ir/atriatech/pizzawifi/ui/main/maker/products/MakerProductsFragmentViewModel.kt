package ir.atriatech.pizzawifi.ui.main.maker.products

import androidx.databinding.ObservableField
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.home.maker.Maker

class MakerProductsFragmentViewModel : BaseFragmentViewModel() {
    var mAdapter: MakerProductAdapter? = null
    var mAdapter2: MakerProductAdapter? = null
    var mItem = ObservableField<Maker>()

    init {
        component.inject(this)
    }
}
