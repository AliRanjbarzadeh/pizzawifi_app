package ir.atriatech.pizzawifi.ui.main.profile.messages.detail

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.messages.Message

class MessageDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Message

    init {
        component.inject(this)
    }
}
