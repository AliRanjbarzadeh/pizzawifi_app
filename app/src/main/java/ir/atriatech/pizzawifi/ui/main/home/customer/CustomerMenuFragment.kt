package ir.atriatech.pizzawifi.ui.main.home.customer

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.android.ui.IsEndOfRecyclerView
import ir.atriatech.extensions.base.fragment.*
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentCustomerMenuBinding
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem


class CustomerMenuFragment : BaseFragment(), RecyclerViewTools, IsEndOfRecyclerView,
    OnRefreshListener {
    lateinit var binding: FragmentCustomerMenuBinding
    private lateinit var mViewModel: CustomerMenuFragmentViewModel

    init {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(CustomerMenuFragmentViewModel::class.java)
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_customer_menu, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (mViewModel.getData(limit, offset)) {
            setAdapter()
        }

        //Check end of data
        checkRvEnd(binding.rvCustomerMenu, this)

        //Refresh listener
        binding.refreshLayout.setOnRefreshListener(this)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        offset = 0
        mViewModel.isRefreshing = true
        mViewModel.isEmptyView.set(false)
        mViewModel.isShowContent.set(false)
        mViewModel.mListItems.clear()
        mViewModel.mAdapter?.list?.clear()
        binding.rvCustomerMenu.adapter = null
        try {
            binding.rvCustomerMenu.removeItemDecorationAt(0)
        } catch (e: Exception) {
        }
        mViewModel.getData(limit, offset)
    }

    override fun onReachToEnd() {
        if (!mViewModel.isEnd) {
            mViewModel.isLoadMore = true
            mViewModel.getData(limit, offset)
        }
    }

    override fun <T> onItemClick(position: Int, view: View, item: T) {
        val customerProduct = item as Pizza
        var extras: FragmentNavigator.Extras? = null
        val transitionName = "customerProductTrans${position}"
        val arg = extraArguments(customerProduct, ARG_STRING_1, transitionName, ARG_STRING_2)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val imgProduct = view.findViewById<AppCompatImageView>(R.id.imgProduct)
            imgProduct.transitionName = transitionName
            extras = FragmentNavigatorExtras(
                imgProduct to transitionName
            )
        }
        navTo(R.id.customerMenuDetailFragment, arg = arg, navigatorExtras = extras)
    }

    override fun <T> onIncreaseItem(position: Int, view: View, item: T) {
        item as Pizza
        var shopCartItem = appDataBase.shopDao().findPizzaByIdMainThreadNullable(item.id)
        if (shopCartItem == null) {
            shopCartItem = ShopCartItem()
            shopCartItem.apply {
                productID = item.id
                name = item.name
                price = item.price
                qty = 1
                pizza = 1
                materials = gson.toJson(item.serverMaterials)
            }
            appDataBase.shopDao().saveItem(shopCartItem)
        } else {
            shopCartItem.qty++
            appDataBase.shopDao().updateItem(shopCartItem)
        }
    }

    override fun <T> onDecreaseItem(position: Int, view: View, item: T) {
        item as Pizza
        val shopCartItem = appDataBase.shopDao().findPizzaByIdMainThreadNullable(item.id)
        if (shopCartItem != null) {
            if (shopCartItem.qty == 1) {
                appDataBase.shopDao().deletePizzaById(item.id)
                return
            }
            shopCartItem.qty--
            appDataBase.shopDao().updateItem(shopCartItem)
        }
    }

    fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = CustomerMenuAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        } else if (offset == 0 && mViewModel.isRefreshing) {
            mViewModel.isRefreshing = false
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        }
        binding.rvCustomerMenu.setHasFixedSize(true)
        binding.rvCustomerMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomerMenu.addItemDecoration(
            MarginItemDecoration(
                dp2px(8),
                marginPosition = MarginItemDecoration.TOP,
                isShowOnFirstItem = true
            )
        )
        binding.rvCustomerMenu.adapter = mViewModel.mAdapter
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
                }

                override fun onSuccess(data: MutableList<Pizza>) {
                    binding.refreshLayout.finishRefresh()
                    binding.refreshLayout.finishLoadMore()
                    if (!mViewModel.isLoadMore) {
                        mViewModel.mListItems = data
                        mViewModel.isEmptyView.set(data.size == 0)
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
}