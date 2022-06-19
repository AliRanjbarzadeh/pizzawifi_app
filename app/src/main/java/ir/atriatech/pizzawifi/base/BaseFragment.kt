package ir.atriatech.pizzawifi.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.delivercloud.base.BaseFragmentListener
import ir.atriatech.extensions.android.hideInputMethod
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.common.SHOULD_REFRESH_HOME
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.common.network.RequestService
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import ir.atriatech.pizzawifi.ui.main.MainActivity
import ir.atriatech.pizzawifi.ui.main.branch.BranchFragment
import ir.atriatech.pizzawifi.ui.main.category.CategoryFragment
import ir.atriatech.pizzawifi.ui.main.category.product.ProductFragment
import ir.atriatech.pizzawifi.ui.main.city.CityFragment
import ir.atriatech.pizzawifi.ui.main.maker.MakerStep1Fragment
import ir.atriatech.pizzawifi.ui.main.orders.detail.OrderDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.AddressFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.detail.AddressDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.map.AddressMapFragment
import ir.atriatech.pizzawifi.ui.main.profile.changemobile.ChangeMobileStep1Fragment
import ir.atriatech.pizzawifi.ui.main.profile.changemobile.ChangeMobileStep2Fragment
import ir.atriatech.pizzawifi.ui.main.profile.editinfo.EditInfoFragment
import ir.atriatech.pizzawifi.ui.main.profile.messages.MessagesFragment
import ir.atriatech.pizzawifi.ui.main.profile.messages.detail.MessageDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.pizza.PizzaFragment
import ir.atriatech.pizzawifi.ui.main.profile.pizza.detail.PizzaDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.SupportFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.add.AddSupportFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.detail.SupportDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.wallet.WalletFragment
import ir.atriatech.util.others.image.ImageLoader
import javax.inject.Inject


open class BaseFragment : BaseCoreFragment(), BaseFragmentListener {
	//region Dagger Implementation
	@Inject
	lateinit var appDataBase: AppDataBase

	@Inject
	lateinit var imageLoader: ImageLoader

	@Inject
	lateinit var requestService: RequestService

	@Inject
	lateinit var gson: Gson

	/**
	 * use component.inject(this) in fragment that you want to use @Inject parameters
	 */
	val component by lazy { AppDH.baseComponent() }
	//endregion


	protected var baseFragmentCallback: BaseFragmentCallback? = null
	protected var baseFragmentListener: BaseFragmentListener = this
	protected var mDialog: MaterialDialog? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		unAuthorizedIntent = Intent(requireActivity(), LoginActivity::class.java)
	}

	override fun onDestroyView() {
		super.onDestroyView()
		mDialog?.cancel()
		mDialog = null
	}

	override fun onPause() {
		super.onPause()
		hideInputMethod()
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		if (context is BaseFragmentCallback) {
			baseFragmentCallback = context
			baseFragmentCallback?.registerFragmentListener(
				this.javaClass.simpleName,
				baseFragmentListener
			)
		}
	}

	override fun onDetach() {
		super.onDetach()
		baseFragmentCallback?.unRegisterFragmentListener(this.javaClass.simpleName)
		baseFragmentCallback = null
	}

	override fun logOut(isSystem: Boolean) {
		when {
			requireActivity() is MainActivity -> {
				when {
					this is OrderDetailFragment -> removeUntil(R.id.ordersFragment)
					this is AddressDetailFragment ||
							this is AddressDetailFragment ||
							this is AddressMapFragment ||
							this is AddressFragment ||
							this is ChangeMobileStep1Fragment ||
							this is ChangeMobileStep2Fragment ||
							this is EditInfoFragment ||
							this is MessageDetailFragment ||
							this is MessagesFragment ||
							this is PizzaDetailFragment ||
							this is PizzaFragment ||
							this is AddSupportFragment ||
							this is SupportDetailFragment ||
							this is SupportFragment ||
							this is WalletFragment
					-> removeUntil(R.id.profileFragment)

					this is MakerStep1Fragment ||
							this is CategoryFragment ||
							this is ProductFragment ||
							this is CityFragment ||
							this is BranchFragment
					-> {
						SHOULD_REFRESH_HOME = true
						removeUntil(R.id.homeFragment)
					}
				}

				(requireActivity() as MainActivity).logout(isSystem)
			}
		}
	}

	open fun onVisibilityChanged(isOpen: Boolean) {}
}

