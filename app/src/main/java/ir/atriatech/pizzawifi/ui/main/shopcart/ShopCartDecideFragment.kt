package ir.atriatech.pizzawifi.ui.main.shopcart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.DELIVER_TYPE
import ir.atriatech.pizzawifi.databinding.FragmentShopCartDecideBinding
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartDecide


class ShopCartDecideFragment : BaseFragment() {
    lateinit var binding: FragmentShopCartDecideBinding
    private lateinit var mViewModel: ShopCartDecideFragmentViewModel

    init {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(ShopCartDecideFragmentViewModel::class.java)
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_shop_cart_decide, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mViewModel.mListItems = appDataBase.shopDao().getAll()
        if (mViewModel.mListItems.isEmpty()) {
            removeUntil(R.id.shopCartFragment)
            return
        }

        if (mViewModel.getData()) {
            // use when come to this page az the previos page_ using back button, and request isn't call
            if (mViewModel.shopCartDecide?.takeBySelf != 1) {
                binding.takeBySelf.visibility = View.GONE
            }
            if (mViewModel.shopCartDecide?.eatInPlace != 1) {
                binding.eatInPlace.visibility = View.GONE
            }
            if (mViewModel.shopCartDecide?.sendToMe != 1) {
                binding.sendToMe.visibility = View.GONE
            }
            if (mViewModel.shopCartDecide != null) {
                AppObject.branchItem = mViewModel.shopCartDecide!!.branch
            }
        }

        binding.eatInPlace.setOnClickListener {
            DELIVER_TYPE = 1
            val branch = AppObject.branchItem
            d("branch item --> ${branch}")
            if (branch != null) {
                val arg = extraArguments(argument1 = branch, key1 = ARG_STRING_2)
                navTo(R.id.shopCartCheckoutFragment, arg = arg)
            } else {
                eToast(findString(R.string.noBranchFound))
            }
//			navTo(R.id.branchFragment)
        }
        binding.takeBySelf.setOnClickListener {
            DELIVER_TYPE = 2
            val branch = AppObject.branchItem
            d("branch item --> ${branch}")
            if (branch != null) {
                val arg = extraArguments(argument1 = branch, key1 = ARG_STRING_2)
                navTo(R.id.shopCartCheckoutFragment, arg = arg)

            } else {
                eToast(findString(R.string.noBranchFound))
            }
//			navTo(R.id.branchFragment)
        }
        binding.sendToMe.setOnClickListener {
            DELIVER_TYPE = 3
            navTo(R.id.shopCartAddressFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        DELIVER_TYPE = -1
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<ShopCartDecide> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, 2)
            }

            override fun onSuccess(data: ShopCartDecide) {
                mViewModel.shopCartDecide = data
                if (mViewModel.shopCartDecide?.takeBySelf != 1) {
                    binding.takeBySelf.visibility = View.GONE
                }

                if (mViewModel.shopCartDecide?.eatInPlace != 1) {
                    binding.eatInPlace.visibility = View.GONE
                }

                if (mViewModel.shopCartDecide?.sendToMe != 1) {
                    binding.sendToMe.visibility = View.GONE
                }
                if (mViewModel.shopCartDecide != null) {
                    AppObject.branchItem = mViewModel.shopCartDecide!!.branch
                }
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.getData()
            }
        }, 4)
    }
}