package ir.atriatech.pizzawifi.ui.main.profile.wallet

import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.profile.wallet.Wallet
import ir.atriatech.pizzawifi.repository.MainRepository
import javax.inject.Inject

class WalletFragmentViewModel : BaseFragmentViewModel() {
	var chargeAmount: String = ""
	var mListItems = mutableListOf<Wallet>()
	var mLoadMoreItems: MutableList<Wallet> = mutableListOf()
	var mAdapter: WalletAdapter? = null
	var isLoadMore = false
	var isEnd = false
	var isRefreshing = false
	var mChargeItem: Msg = Msg()
	var isFromBrowser = false

	@Inject
	lateinit var repository: MainRepository
	val mObserver: LiveData<Outcome<MutableList<Wallet>>> by lazy {
		repository.walletOutcome.toLiveData(
			bag
		)
	}
	val mIncreaseObserver: LiveData<Outcome<Msg>> by lazy {
		repository.walletIncreaseOutcome.toLiveData(
			bag
		)
	}
	val mCheckoutObserver: LiveData<Outcome<Wallet>> by lazy {
		repository.walletCheckOutcome.toLiveData(
			bag
		)
	}

	init {
		component.inject(this)
	}


	fun getData(limit: Int, offset: Int): Boolean {
		when {
			mAdapter == null -> {
				repository.walletArchive(limit, offset, bag)
				return false
			}

			mListItems.isEmpty() -> {
				repository.walletArchive(limit, offset, bag)
				return true
			}

			isLoadMore -> {
				repository.walletArchive(limit, offset, bag)
				return true
			}
		}
		return true
	}

	fun pay() {
		if (chargeAmount.isEmpty()) {
			eToast(findString(R.string.emptyAmount))
			return
		}
		if (chargeAmount.replace(",", "").toInt() < 200) {
			eToast(findString(R.string.wrongAmount))
			return
		}
		repository.walletIncrease(chargeAmount.replace(",", "").toInt(), bag)
	}

	fun checkout() {
		if (mChargeItem.id > 0) {
			repository.walletCheckout(mChargeItem.id, bag)
		}
	}
}
