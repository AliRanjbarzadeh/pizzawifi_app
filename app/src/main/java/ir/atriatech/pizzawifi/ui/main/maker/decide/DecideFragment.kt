package ir.atriatech.pizzawifi.ui.main.maker.decide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.GO_SHOP_CART
import ir.atriatech.pizzawifi.common.GO_TO_MENU
import ir.atriatech.pizzawifi.databinding.FragmentDecideBinding


class DecideFragment : BaseFragment() {
	lateinit var binding: FragmentDecideBinding
	private lateinit var mViewModel: DecideFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(DecideFragmentViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_decide, container, false)

		binding.lifecycleOwner = this
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		baseFragmentCallback?.changeMenuLock(true)

		binding.goToShopCart.setOnClickListener {
			GO_SHOP_CART = true
			removeUntil(R.id.homeFragment)
		}
		binding.makeAnotherPizza.setOnClickListener { removeUntil(R.id.makerStep1Fragment) }

		binding.goToMenu.setOnClickListener {
			GO_TO_MENU = true
			removeUntil(R.id.homeFragment)
		}
	}

	override fun onDestroy() {
		baseFragmentCallback?.changeMenuLock(false)
		super.onDestroy()
	}
}