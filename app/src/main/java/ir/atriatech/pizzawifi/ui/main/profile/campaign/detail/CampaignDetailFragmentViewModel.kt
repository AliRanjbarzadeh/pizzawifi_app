package ir.atriatech.pizzawifi.ui.main.profile.campaign.detail

import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.campaign.Campaign

class CampaignDetailFragmentViewModel : BaseFragmentViewModel() {
    lateinit var mItem: Campaign

    init {
        component.inject(this)
    }
}
