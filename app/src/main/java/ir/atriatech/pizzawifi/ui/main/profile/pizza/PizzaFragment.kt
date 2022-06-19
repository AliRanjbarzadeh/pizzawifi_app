package ir.atriatech.pizzawifi.ui.main.profile.pizza

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.yanzhenjie.recyclerview.*
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.android.ui.afterDelayTextChanged
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.app.makeTypeFace
import ir.atriatech.extensions.base.fragment.*
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentPizzaBinding
import ir.atriatech.pizzawifi.entities.pizza.Pizza


class PizzaFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView, OnRefreshListener {
	lateinit var binding: FragmentPizzaBinding
	private lateinit var mViewModel: PizzaFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(PizzaFragmentViewModel::class.java)
		mObserver()
		mDeleteObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pizza, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		hideKeyboard(binding.mainView)
		hideKeyboard(binding.rvPizza)

		if (mViewModel.getData(limit, offset)) {
			setAdapter()
		}

		//Check end of data
		checkRvEnd(binding.rvPizza, this)

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
		baseFragmentCallback?.changeSoftkeyMethod(true)

		binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
			var keyAction = false
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				keyAction = when {
					mViewModel.mSearchText.isEmpty() -> {
						eToast(findString(R.string.emptyPizzaName))
						true
					}
					else -> {
						binding.imgSearch.performClick()
						false
					}
				}
			}
			keyAction
		}
		binding.imgSearch.setOnClickListener {
			if (mViewModel.isRefreshing) {
				return@setOnClickListener
			}
			if (mViewModel.mSearchText.isEmpty()) {
				eToast(findString(R.string.emptyPizzaName))
				return@setOnClickListener
			}
			mViewModel.mSearchText = binding.etSearch.text.toString()
			mViewModel.mListItems.clear()
			mViewModel.mAdapter?.list?.clear()
			offset = 0

			mViewModel.isSearching = true
			mViewModel.getData(limit, offset)
		}
	}

	override fun onResume() {
		super.onResume()
		binding.etSearch.afterDelayTextChanged({
			if (it.isEmpty()) {
				binding.txtEmptyPizza.visibility = View.GONE
				mViewModel.mListItems.clear()
				mViewModel.mAdapter?.list?.clear()
				mViewModel.isSearching = true
				offset = 0
				mViewModel.getData(limit, offset)
			}
		}, 300, requireActivity())
	}

	override fun onDestroyView() {
		baseFragmentCallback?.changeSoftkeyMethod(false)
		super.onDestroyView()
	}

	override fun handleNotification() {
		mDialog?.cancel()
		removeUntil(R.id.profileFragment)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etSearch.clearFocus()
		}
	}

	override fun onRefresh(refreshLayout: RefreshLayout) {
		offset = 0
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		binding.rvPizza.adapter = null
		mViewModel.getData(limit, offset)
	}

	override fun onReachToEnd() {
		if (!mViewModel.isEnd) {
			mViewModel.isLoadMore = true
			mViewModel.getData(limit, offset)
		}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		val pizza = item as Pizza
		var extras: FragmentNavigator.Extras? = null
		val transitionName = "UserPizza${position}"
		val arg = extraArguments(pizza, ARG_STRING_1, transitionName, ARG_STRING_2)
		val imgProduct = view.findViewById<AppCompatImageView>(R.id.imgProduct)
		imgProduct.transitionName = transitionName
		extras = FragmentNavigatorExtras(
			imgProduct to transitionName
		)
		navTo(R.id.pizzaDetailFragment, arg = arg, navigatorExtras = extras)
	}

	override fun <T> deleteItemFromList(mItem: T, position: Int) {
		mItem as Pizza
		if (mItem.shopCount > 0) {
			appDataBase.shopDao().deletePizzaById(mItem.id)
		}
		mViewModel.mListItems.removeAt(position)
		mViewModel.mAdapter?.list?.removeAt(position)
		mViewModel.mAdapter?.notifyItemRemoved(position)
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = PizzaAdapter(this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (offset == 0 && mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (mViewModel.isSearching) {
			mViewModel.isSearching = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}
		try {
			binding.rvPizza.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}
		binding.rvPizza.adapter = null
		binding.rvPizza.setHasFixedSize(true)
		binding.rvPizza.layoutManager = LinearLayoutManager(requireContext())
		binding.rvPizza.addItemDecoration(MarginItemDecoration(mHeight = dp2px(1), marginPosition = 1, isShowOnFirstItem = true))
		binding.rvPizza.setSwipeMenuCreator(swipeMenuCreator)
		binding.rvPizza.setOnItemMenuClickListener(mMenuItemClickListener)
		binding.rvPizza.isLongPressDragEnabled = false
		binding.rvPizza.adapter = mViewModel.mAdapter
	}

	private val swipeMenuCreator = object : SwipeMenuCreator {
		override fun onCreateMenu(
			swipeLeftMenu: SwipeMenu,
			swipeRightMenu: SwipeMenu,
			position: Int
		) {
			val width = dp2px(60)
			val height = ViewGroup.LayoutParams.MATCH_PARENT

			run {
				val deleteItem = SwipeMenuItem(requireContext())
					.setBackground(R.drawable.bg_delete_swipe)
					.setImage(R.drawable.ic_delete_white)
					.setText("حذف")
					.setTextColor(findColor(R.color.white))
					.setWidth(width)
					.setTextTypeface(makeTypeFace())
					.setHeight(height)
				swipeRightMenu.addMenuItem(deleteItem)
			}

		}
	}

	private val mMenuItemClickListener = OnItemMenuClickListener { menuBridge, position ->
		menuBridge.closeMenu()
		menuBridge.closeMenu()
		val direction = menuBridge.direction
		if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
			val pizza = mViewModel.mListItems[position]
			mDialog = MaterialDialog(requireContext())
				.apply {
					title(R.string.pizzaDeleteTittle)
					message(R.string.pizzaDeleteText)
					positiveButton(R.string._yes)
						.positiveButton {
							mViewModel.mDeleteId = pizza.id
							mViewModel.mDeletePosition = position
							mViewModel.delete()
						}
					negativeButton(R.string._no)
					show()
				}
		}
	}

	private fun mObserver() {
		baseListObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Pizza>> {
				override fun onProgress(loading: Boolean) {
					if (mViewModel.mListItems.isEmpty() && !mViewModel.isRefreshing) {
						mViewModel.isShowContent.set(false)
						setProgressView(binding.mainView, loading, 2)
					} else {
						binding.refreshLayout.setEnableLoadMore(loading)
					}
					binding.etSearch.isEnabled = !loading
				}

				override fun onSuccess(data: MutableList<Pizza>) {
					binding.refreshLayout.finishRefresh()
					binding.refreshLayout.finishLoadMore()
					if (!mViewModel.isLoadMore) {
						mViewModel.mListItems = data
						if (mViewModel.mSearchText.isEmpty()) {
							mViewModel.isEmptyView.set(data.size == 0)
							mViewModel.isShowContent.set(data.size != 0)
							mViewModel.isSearchBox.set(data.size != 0)
						} else {
							if (data.size == 0) {
								binding.txtEmptyPizza.visibility = View.VISIBLE
								binding.txtEmptyPizza.setText(findString(R.string.noPizzaFound2))
							}
						}
						mViewModel.isShowContent.set(data.size != 0)
					} else if (data.isNotEmpty()) {
						mViewModel.mLoadMoreItems = data
					}
					mViewModel.isEnd = limit > data.size
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData(limit, offset)
				}
			},
			4
		)
	}

	private fun mDeleteObserver() {
		baseObserver(this, mViewModel.mDeleteObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 2)
			}

			override fun onSuccess(data: Msg) {
				mDialog = null
				baseFragmentCallback?.deleteItemFromList(
					mViewModel.mListItems[mViewModel.mDeletePosition],
					mViewModel.mDeletePosition
				)
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData(limit, offset)
			}
		}, 4)
	}
}