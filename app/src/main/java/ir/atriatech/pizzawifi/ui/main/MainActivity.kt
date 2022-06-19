package ir.atriatech.pizzawifi.ui.main

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.mikhaellopez.rxanimation.RxAnimation
import com.mikhaellopez.rxanimation.height
import ir.atriatech.core.BuildConfig.*
import ir.atriatech.delivercloud.base.BaseFragmentListener
import ir.atriatech.extensions.android.deleteFromSp
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.px2dp
import ir.atriatech.extensions.android.saveToSp
import ir.atriatech.extensions.android.ui.NavigationCallback
import ir.atriatech.extensions.android.ui.getCurrentNavHost
import ir.atriatech.extensions.android.ui.setupWithNavController
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.performOnMainDoInBack
import ir.atriatech.pizzawifi.BuildConfig
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseActivity
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.*
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.databinding.ActivityMainBinding
import ir.atriatech.pizzawifi.entities.home.HomeBase
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.profile.address.Address
import ir.atriatech.pizzawifi.entities.profile.support.Support
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import ir.atriatech.pizzawifi.ui.main.category.product.ProductFragment
import ir.atriatech.pizzawifi.ui.main.home.HomeFragment
import ir.atriatech.pizzawifi.ui.main.home.tournament.TournamentFragment
import ir.atriatech.pizzawifi.ui.main.maker.decide.DecideFragment
import ir.atriatech.pizzawifi.ui.main.orders.OrdersFragment
import ir.atriatech.pizzawifi.ui.main.orders.detail.OrderDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.AddressFragment
import ir.atriatech.pizzawifi.ui.main.profile.address.map.AddressMapFragment
import ir.atriatech.pizzawifi.ui.main.profile.changemobile.ChangeMobileStep2Fragment
import ir.atriatech.pizzawifi.ui.main.profile.editinfo.EditInfoFragment
import ir.atriatech.pizzawifi.ui.main.profile.messages.MessagesFragment
import ir.atriatech.pizzawifi.ui.main.profile.pizza.PizzaFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.SupportFragment
import ir.atriatech.pizzawifi.ui.main.profile.support.detail.SupportDetailFragment
import ir.atriatech.pizzawifi.ui.main.profile.wallet.WalletFragment
import ir.atriatech.pizzawifi.ui.main.shopcart.address.ShopCartAddressFragment
import ir.atriatech.pizzawifi.ui.main.shopcart.checkout.ShopCartCheckoutFragment
import ir.atriatech.util.others.BottomMenuHelper
import kotlinx.android.synthetic.main.activity_main.*
import me.leolin.shortcutbadger.ShortcutBadger
import javax.inject.Inject

class MainActivity : BaseActivity(), MenuCallBack {

	@Inject
	lateinit var appDataBase: AppDataBase

	lateinit var binding: ActivityMainBinding

	var labelDestination = ""
	var idDestination = 0
	var bottomNavHeight: Int = 0
	private lateinit var controller: LiveData<NavController>
	var currentNavController: LiveData<NavController>? = null
	private var fragmentListeners: HashMap<String, BaseFragmentListener> = HashMap()
	private var menuLock = false
	private var notificationExtras: Bundle? = null
	private var badge: BadgeDrawable? = null

	//Home remote model
	lateinit var homeModel: HomeBase

	private val broadcastReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {

			if (intent == null || intent.action == null || intent.extras == null)
				return

			when (intent.action) {
				SMS_RECEIVED -> {
					bottom_nav_menu.getCurrentNavHost(supportFragmentManager)
						?.childFragmentManager?.also { mFragmentManager ->
							try {
								mFragmentManager.fragments[0]?.also { currentFragment ->
									when {
										currentFragment is ChangeMobileStep2Fragment -> {
											val code = intent.extras?.getString(SMS_CODE)
											currentFragment.setCode(code)
										}
									}
								}
							} catch (e: Exception) {
							}
						}
				}

				NOTIFICATION_BROADCAST -> {
					intent.extras?.also { extras ->
						notificationExtras = extras
						handleNotification()
					}
				}
			}
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		component.inject(this)

		binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
		setContentView(binding.root)

		setBottomMenuFont()
		if (savedInstanceState == null) {
			setupBottomNavigationBar()
		}
		removePaddingFromNavigationItem()

		binding.bottomNavMenu.post { bottomNavHeight = px2dp(binding.bottomNavMenu.measuredHeight) }
		appDataBase.shopDao().countAll()
			.performOnMainDoInBack()
			.doAfterNext {
				if (it != null) {
					if (it == 0) {
						appDataBase.shopDao().resetTable()
						binding.bottomNavMenu.removeBadge(R.id.menu1)
					} else if (it > 0) {
						badge = binding.bottomNavMenu.getOrCreateBadge(R.id.menu1)
						badge?.number = it
						badge?.maxCharacterCount = 3
					}
				} else {
					appDataBase.shopDao().resetTable()
					binding.bottomNavMenu.removeBadge(R.id.menu1)
				}
			}
			.subscribe()

		ShortcutBadger.applyCount(this, 0) //for 1.1.4+
		ShortcutBadger.removeCount(this) //for 1.1.4+
		saveToSp(NOTIFICATION_BADGE_COUNT, 0)

		intent?.extras?.also { extras ->
			if (extras[NOTIFICATION_KEY].toString().isNotEmpty()) {
				notificationExtras = extras
			}
		}
	}

	override fun onNewIntent(intent: Intent?) {
		super.onNewIntent(intent)
		if (intent?.data != null) {
			val uri = intent.data!!
			when (uri.scheme) {
				"lavia" -> {
					when (uri.host) {
						//Payment links
						null -> {
							when (currentNavController?.value?.graph?.id) {
								R.id.menu1 -> fragmentListeners[ShopCartCheckoutFragment::class.java.simpleName]?.handlePayment()
								R.id.menu3 -> fragmentListeners[WalletFragment::class.java.simpleName]?.handlePayment()
							}
						}
					}
				}
			}
		} else {
			intent?.extras?.also { extras ->
				notificationExtras = extras
				handleNotification()
			}
		}
	}

	override fun onDestroy() {
		super.onDestroy()

		AppObject.branchItem = null
		AppObject.selectedBranchId = 0
		unregisterReceiver(broadcastReceiver)
	}

	//region Home remote model
	/**
	 * Setter
	 */
	override fun setHomeRemoteModel(homeRemoteModel: HomeBase) {
		homeModel = homeRemoteModel
	}

	/**
	 * Getter
	 */
	override fun getHomeRemoteModel(): HomeBase {
		return homeModel
	}

	override fun handleNotification() {
		notificationExtras?.also { extras ->
			saveToSp(NOTIFICATION_KEY, extras[NOTIFICATION_KEY])
			saveToSp(NOTIFICATION_ADDITIONAL, extras[NOTIFICATION_ADDITIONAL])
			when (extras[NOTIFICATION_KEY]) {
				"orders" -> {
					if (currentNavController?.value?.graph?.id != R.id.menu2) {
						changeMenuSelectedItem(R.id.menu2)
					} else {
						if (fragmentListeners[OrderDetailFragment::class.java.simpleName] != null) {
							fragmentListeners[OrdersFragment::class.java.simpleName]?.updateValues()
							fragmentListeners[OrderDetailFragment::class.java.simpleName]?.handleNotification()

						} else if (fragmentListeners[OrdersFragment::class.java.simpleName] != null) {
							fragmentListeners[OrdersFragment::class.java.simpleName]?.updateValues()
						}
					}
				}

				"wallet" -> {
					d("wallet main, =============", "wallet")
					if (currentNavController?.value?.graph?.id != R.id.menu3) {
						changeMenuSelectedItem(R.id.menu3)
					} else {
						bottom_nav_menu.getCurrentNavHost(supportFragmentManager)
							?.childFragmentManager?.also { mFragmentManager ->
								try {
									mFragmentManager.fragments[0]?.also { currentFragment ->
										d("wallet main, ============= ${currentFragment}", "wallet")
										when {
											currentFragment !is WalletFragment && currentFragment is BaseFragment -> {
												fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
											}

											currentFragment !is BaseFragment -> {
												fragmentListeners[EditInfoFragment::class.java.simpleName]?.handleNotification()
											}

											currentFragment is WalletFragment -> fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
										}
									}
								} catch (e: Exception) {
								}
							}
					}
				}

				"message" -> {
					if (currentNavController?.value?.graph?.id != R.id.menu3) {
						changeMenuSelectedItem(R.id.menu3)
					} else {
						bottom_nav_menu.getCurrentNavHost(supportFragmentManager)
							?.childFragmentManager?.also { mFragmentManager ->
								try {
									mFragmentManager.fragments[0]?.also { currentFragment ->
										when {
											currentFragment !is MessagesFragment && currentFragment is BaseFragment -> {
												fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
											}

											currentFragment !is BaseFragment -> {
												fragmentListeners[EditInfoFragment::class.java.simpleName]?.handleNotification()
											}

											currentFragment is MessagesFragment -> fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
										}
									}
								} catch (e: Exception) {
								}
							}
					}
				}

				"support" -> {
					if (currentNavController?.value?.graph?.id != R.id.menu3) {
						changeMenuSelectedItem(R.id.menu3)
					} else {
						bottom_nav_menu.getCurrentNavHost(supportFragmentManager)
							?.childFragmentManager?.also { mFragmentManager ->
								try {
									mFragmentManager.fragments[0]?.also { currentFragment ->
										d(
											"support main, =============${currentFragment}",
											"support"
										)
										when {

											currentFragment !is MessagesFragment && currentFragment is BaseFragment -> {
												when (currentFragment) {
													is SupportFragment -> {
														fragmentListeners[SupportFragment::class.java.simpleName]?.updateValues()
													}
													is SupportDetailFragment -> {
														DELETE_NOTIF_KEYS = false
														fragmentListeners[SupportFragment::class.java.simpleName]?.updateValues()
														fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
													}
													else -> {
														fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
													}
												}
											}

											currentFragment !is BaseFragment -> {
												fragmentListeners[EditInfoFragment::class.java.simpleName]?.handleNotification()
											}

											currentFragment is MessagesFragment -> fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
										}
									}
								} catch (e: Exception) {
								}
							}
					}
				}

				"peyk" -> {
					bottom_nav_menu.getCurrentNavHost(supportFragmentManager)?.childFragmentManager?.also { mFragmentManager ->
						try {
							mFragmentManager.fragments[0]?.also { currentFragment ->
								when {
									currentFragment is OrderDetailFragment -> {
										fragmentListeners[currentFragment.javaClass.simpleName]?.handleNotification()
									}
								}
							}
						} catch (e: Exception) {
						}
					}
				}
			}
			notificationExtras = null
		}
	}

	override fun onSupportNavigateUp(): Boolean {
		return currentNavController?.value?.navigateUp() ?: false
	}

	override fun onBackPressed() {
		if (fragmentListeners[TournamentFragment::class.java.simpleName] != null) {
			fragmentListeners[TournamentFragment::class.java.simpleName]?.sureOnBack()
			return
		}
		binding.bottomNavMenu.getCurrentNavHost(supportFragmentManager)?.childFragmentManager?.let {
			if (it.backStackEntryCount > 0 && it.fragments[0] is DecideFragment) {
				return
			}
		}
		if (BuildConfig.DEBUG) {
			if (currentNavController?.value?.popBackStack() != true) {
				super.onBackPressed()
			}
		} else {
			when (currentNavController?.value?.graph?.id) {
				R.id.menu0 -> {
					if (currentNavController?.value?.popBackStack() != true) {
						super.onBackPressed()
					}
				}

				null -> {
					super.onBackPressed()
				}

				else -> {
					binding.bottomNavMenu.getCurrentNavHost(supportFragmentManager)
						?.childFragmentManager?.let {
							if (it.backStackEntryCount > 0) {
								currentNavController?.value?.popBackStack()
							} else {
								changeMenuSelectedItem(R.id.menu0)
							}
						}
				}
			}
		}
	}

	override fun onResume() {
		super.onResume()

		//region Receiver
		val intentFilter = IntentFilter()
		intentFilter.addAction(SMS_RECEIVED)
		intentFilter.addAction(NOTIFICATION_BROADCAST)
		registerReceiver(broadcastReceiver, intentFilter)

		if (!menuLock) {
			changeMenuLock(false)
		}
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_main, null, false)
		setupBottomNavigationBar()
	}

	override fun changeMenuSelectedItem(@IdRes menuItem: Int) {
		binding.bottomNavMenu.selectedItemId = menuItem
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		var shouldHideMenu = true
		binding.bottomNavMenu.getCurrentNavHost(supportFragmentManager)?.childFragmentManager?.let {
			it.fragments.forEach { currentFragment ->
				when (currentFragment) {
					is WalletFragment -> {
						shouldHideMenu = !currentFragment.isEmptyList()
						currentFragment.onVisibilityChanged(isOpen)
					}
					is AddressMapFragment -> {
						shouldHideMenu = false
						currentFragment.onVisibilityChanged(isOpen)
					}
					is OrdersFragment -> {
						shouldHideMenu = false
						currentFragment.onVisibilityChanged(isOpen)
					}
					is BaseFragment -> currentFragment.onVisibilityChanged(isOpen)
				}
			}
		}
		when (shouldHideMenu) {
			true -> { // If should hide menu
				when (isOpen) {
					true -> // Hide menu
						RxAnimation.from(binding.bottomNavMenu)
							.height(0)
							.subscribe()

					false -> //Show menu
						RxAnimation.from(binding.bottomNavMenu)
							.height(bottomNavHeight)
							.subscribe()
				}
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		try {
			val fm = supportFragmentManager
			if (fm.fragments.size > 0) {
				for (i in 0 until fm.fragments.size) {
					val fragment = fm.fragments[i]
					when (fragment is NavHostFragment) {
						true -> {
							val childFragments = fragment.childFragmentManager.fragments
							childFragments.forEach { child ->
								child.onActivityResult(requestCode, resultCode, data)
							}
						}
					}
				}
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun changeSoftkeyMethod(isPan: Boolean) {
		if (isPan) {
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
		} else {
			window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
		}
	}

	override fun changeMenuLock(isLock: Boolean) {
		menuLock = isLock
		if (isLock) {
			binding.bottomNavMenu.visibility = View.GONE
		} else {
			binding.bottomNavMenu.visibility = View.VISIBLE
			binding.bottomNavMenu.postDelayed(
				{ bottomNavHeight = px2dp(binding.bottomNavMenu.measuredHeight) },
				200
			)
		}
	}

	override fun <T> addItemToList(mItem: T) {
		when (mItem) {
			is Support -> {
				fragmentListeners[SupportFragment::class.java.simpleName]?.addItemToList(mItem)
			}

			is Address -> {
				fragmentListeners[AddressFragment::class.java.simpleName]?.addItemToList(mItem)
				fragmentListeners[ShopCartAddressFragment::class.java.simpleName]?.addItemToList(
					mItem
				)
			}
		}
	}

	override fun <T> editItemInList(mItem: T, position: Int) {
		when (mItem) {
			is Address -> {
				fragmentListeners[AddressFragment::class.java.simpleName]?.editItemInList(
					mItem,
					position
				)
				fragmentListeners[ShopCartAddressFragment::class.java.simpleName]?.editItemInList(
					mItem,
					position
				)
			}
		}
	}

	override fun <T> deleteItemFromList(mItem: T, position: Int) {
		when (mItem) {
			is Address -> {
				fragmentListeners[AddressFragment::class.java.simpleName]?.deleteItemFromList(
					mItem,
					position
				)
				fragmentListeners[ShopCartAddressFragment::class.java.simpleName]?.deleteItemFromList(
					mItem,
					position
				)
			}

			is Pizza -> {
				fragmentListeners[PizzaFragment::class.java.simpleName]?.deleteItemFromList(
					mItem,
					position
				)
				fragmentListeners[ProductFragment::class.java.simpleName]?.deleteItemFromList(
					mItem,
					position
				)
			}
		}
	}

	override fun goToShopCart() {
		changeMenuSelectedItem(R.id.menu1)
	}

	override fun goToMenu() {
		currentNavController?.value?.graph?.let {
			currentNavController?.value?.popBackStack(it.startDestination, false)
		}
		fragmentListeners[HomeFragment::class.java.simpleName]?.goToMenu()
	}

	override fun goToMenuDetail(argument1: Any, key1: String, argument2: Any, key2: String) {
		currentNavController?.value?.graph?.let {
			currentNavController?.value?.popBackStack(it.startDestination, false)
		}
		fragmentListeners[HomeFragment::class.java.simpleName]?.goToMenuDetail(
			argument1,
			key1,
			argument2,
			key2
		)
	}

	override fun goToOrders() {
		changeMenuSelectedItem(R.id.menu2)
	}

	override fun registerFragmentListener(
		fragmentClassName: String,
		baseFragmentListener: BaseFragmentListener
	) {
		fragmentListeners.put(fragmentClassName, baseFragmentListener)
	}

	override fun unRegisterFragmentListener(fragmentClassName: String) {
		fragmentListeners.remove(fragmentClassName)
	}

	override fun logFirebase(eventName: String, params: Bundle?) {
		firebaseAnalytics.logEvent(eventName, params)
	}

	/*====================Bottom Navigation====================*/
	private fun setBottomMenuFont() {
		val typeface = Typeface.createFromAsset(assets, findString(R.string.app_font))
		BottomMenuHelper(binding.bottomNavMenu).changeFont(typeface)
		changeMenusForLogin()
	}

	override fun changeMenusForLogin() {
		binding.bottomNavMenu.menu.getItem(3).setTitle(if (AppObject.userInfo.isLogin) R.string.menu3 else R.string.loginWord)
		binding.bottomNavMenu.menu.getItem(3).setIcon(if (AppObject.userInfo.isLogin) R.drawable.ic_menu3 else R.drawable.ic_baseline_login_24)
	}

	private fun setupBottomNavigationBar() {
		val navGraphIds = listOf(
			R.navigation.home,
			R.navigation.shopcart,
			R.navigation.orders,
			R.navigation.profile,
			R.navigation.more
		)

		val getLogin = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
			when {
				activityResult.resultCode == Activity.RESULT_OK && activityResult.data != null -> {
					//Continue next step of shopcart
					activityResult.data?.getIntExtra(WHICH_MENU_CLICKED, -1)?.let { menuId ->
						changeMenusForLogin()
						if (menuId > -1) {
							changeMenuSelectedItem(menuId)

							when (menuId) {
								R.id.menu2 -> {
									Handler(mainLooper).postDelayed({
										binding.bottomNavMenu.getCurrentNavHost(supportFragmentManager)?.childFragmentManager?.let {
											if (it.fragments.size > 0 && it.fragments[0] is OrdersFragment) {
												(it.fragments[0] as OrdersFragment).hardRefresh()
											}
										}
									}, 100)
								}
							}
						}
					}
				}
			}
		}

		val navigationCallbacks = listOf(
			null,
			null,
			object : NavigationCallback {
				override fun onItemClick() {
					//go to login activity with menu orders
					val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
					loginIntent.putExtra(WHICH_MENU_CLICKED, R.id.menu2)
					getLogin.launch(loginIntent)
				}
			},
			object : NavigationCallback {
				override fun onItemClick() {
					//go to login activity with menu profile
					val loginIntent = Intent(this@MainActivity, LoginActivity::class.java)
					loginIntent.putExtra(WHICH_MENU_CLICKED, R.id.menu3)
					getLogin.launch(loginIntent)
				}
			},
			null
		)

		// Setup the bottom navigation view with a grouping of navigation graphs
		controller = binding.bottomNavMenu.setupWithNavController(
			navGraphIds = navGraphIds,
			navGraphCallbacks = navigationCallbacks,
			fragmentManager = supportFragmentManager,
			containerId = R.id.nav_host_container,
			intent = intent
		)

		currentNavController = controller
		controller.observe(this) {
			labelDestination = it.graph.label.toString()
			idDestination = it.graph.id

			when (it.graph.id) {
				R.id.menu0 -> {
					when (it.currentDestination?.label) {
						"HomeFragment" -> changeBottomMenuColor(true)

						else -> changeBottomMenuColor(false)
					}
				}

				else -> changeBottomMenuColor(false)
			}
		}
	}

	private fun removePaddingFromNavigationItem() {
		val menuView = binding.bottomNavMenu.getChildAt(0) as BottomNavigationMenuView
		for (i in 0 until menuView.childCount) {
			val item = menuView.getChildAt(i) as BottomNavigationItemView
			val activeLabel = item.findViewById<View>(R.id.largeLabel)
			if (activeLabel is TextView) {
				activeLabel.setPadding(0, 0, 0, 0)
				activeLabel.gravity = Gravity.CENTER
			}
		}
	}

	override fun logout(isSystem: Boolean) {
		deleteFromSp(LOGIN_SESSION_KEY)
		deleteFromSp(TOKEN_REGISTER_KEY)
		deleteFromSp(TOKEN_SESSION_KEY)
		deleteFromSp(USER_INFO)
		AppObject.userInfo.reset()

		if (isSystem) {
			eToast(findString(R.string.forceLogoutDescription))
		}

		//Go to home if in profile or orders
		when (currentNavController?.value?.graph?.id) {
			R.id.menu2, R.id.menu3 -> {
				changeMenuSelectedItem(R.id.menu0)
			}
		}
		changeMenusForLogin()
	}

	override fun changeBottomMenuColor(isRed: Boolean) {
		val bottomMenuColor = if (isRed) {
			intArrayOf(findColor(R.color.white), findColor(R.color.whiteT))
		} else {
			intArrayOf(findColor(R.color.colorPrimary), findColor(R.color.color66))
		}

		val bottomMenuBgColor = if (isRed) {
			findColor(R.color.colorPrimary)
		} else {
			findColor(R.color.white)
		}

		badge?.backgroundColor = if (isRed) {
			findColor(R.color.white)
		} else {
			findColor(R.color.colorPrimary)
		}
		badge?.badgeTextColor = if (isRed) {
			findColor(R.color.colorPrimary)
		} else {
			findColor(R.color.white)
		}

		val stateBottomMenu = arrayOf(
			intArrayOf(android.R.attr.state_checked),
			intArrayOf(-android.R.attr.state_checked)
		)

		val colorStateList = ColorStateList(stateBottomMenu, bottomMenuColor)
		binding.bottomNavMenu.itemIconTintList = colorStateList
		binding.bottomNavMenu.itemTextColor = colorStateList
		binding.bottomNavMenu.setBackgroundColor(bottomMenuBgColor)
	}
}

interface MenuCallBack {
	fun changeMenuSelectedItem(@IdRes menuItem: Int)
}