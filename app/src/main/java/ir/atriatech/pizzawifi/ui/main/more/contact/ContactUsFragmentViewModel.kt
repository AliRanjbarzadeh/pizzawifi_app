package ir.atriatech.pizzawifi.ui.main.more.contact

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.more.contactus.ContactItem
import ir.atriatech.pizzawifi.entities.more.contactus.ContactUs
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class ContactUsFragmentViewModel : BaseFragmentViewModel() {
	var mItem: ContactUs = ContactUs()
	var mAdapter: ContactUsAdapter? = null
	val mListItem = mutableListOf<ContactItem>()
	var mAdapterHorizontal: ContactUsAdapterHorizontal? = null

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<ContactUs>> by lazy { repository.contactOutcome.toLiveData(bag) }

	init {
		component.inject(this)
	}

	fun getData(): Boolean {
		if (mAdapter == null) {
			repository.contact(bag)
			return false
		}
		return true
	}
}
