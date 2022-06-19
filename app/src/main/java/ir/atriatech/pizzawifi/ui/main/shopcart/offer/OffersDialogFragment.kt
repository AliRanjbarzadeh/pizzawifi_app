package ir.atriatech.pizzawifi.ui.main.shopcart.offer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.muddzdev.styleabletoast.StyleableToast
import com.orhanobut.logger.Logger
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.app.app
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.reactivex.performOnBackOutOnMain
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseDialogFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.dagger.AppDH
import ir.atriatech.pizzawifi.common.dagger.base_app.database.AppDataBase
import ir.atriatech.pizzawifi.common.network.RequestService
import ir.atriatech.pizzawifi.databinding.FragmentOffersProductBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import kotlinx.android.synthetic.main.fragment_offers_product.*
import javax.inject.Inject

class OffersDialogFragment : BaseDialogFragment(), RecyclerViewTools {

    //region Dagger
    protected val component by lazy { AppDH.baseComponent() }
    lateinit var onSkipClick: View.OnClickListener
    private lateinit var mViewModel: OfferDialogViewModel

    //Disposable
    private var disposable: Disposable? = null

    @Inject
    lateinit var mContext: Context

    @Inject
    lateinit var requestService: RequestService

    @Inject
    lateinit var appDataBase: AppDataBase

    var productCountToast: StyleableToast? = null

    companion object {
        fun newInstance(): OffersDialogFragment {
            return OffersDialogFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
        mViewModel = ViewModelProvider(this).get(OfferDialogViewModel::class.java)
        mViewModel.branchId = AppObject.selectedBranchId

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onStart() {
        super.onStart()

        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(width, height)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentOffersProductBinding>(
            inflater,
            R.layout.fragment_offers_product,
            container,
            false
        )

        binding.viewModel = mViewModel

        binding.btnSkip.setOnClickListener(onSkipClick)

        initBase(binding.progressBar)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (mViewModel.isLoaded.get()) {
//            setAdapter()
//        } else {
        d("TAG", "mViewModel.branchId ==> ${mViewModel.branchId}")
        if (mViewModel.branchId != null) {
            apiRequest()
        }

//        }
    }

    fun setAdapter() {
        d("mViewModel.mItem!!.products -->>  ${mViewModel.mItem!!.products} ")
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = ProductOfferAdapter(this)
            if (mViewModel.mItem != null) {
                mViewModel.mAdapter!!.list = mViewModel.mItem!!.products
                d("mViewModel.mAdapter!!.list -->>  ${mViewModel.mAdapter!!.list} ")

            }
        }

        rvData?.setHasFixedSize(true)
        rvData?.layoutManager = LinearLayoutManager(requireContext())
        rvData?.addItemDecoration(MarginItemDecoration(dp2px(2), marginPosition = 2))
        rvData?.adapter = mViewModel.mAdapter
    }

    fun showEToast(msg: Any?) {

        if (productCountToast != null) {
            productCountToast?.cancel()
        }
        productCountToast = StyleableToast.makeText(
            app,
            msg.toString(),
            Toast.LENGTH_LONG,
            ir.atriatech.extensions.R.style.eToast
        )
        productCountToast?.show()

    }

    override fun <T> onAddToCart(position: Int, view: View, item: T) {
        item as Product
        if (item.entity == 0)
            return

        val shopCartItem = ShopCartItem()
        shopCartItem.apply {
            productID = item.id
            name = item.name
            image = item.image
            price = item.price
            type = item.type
            max_choice = item.max_choice
            //todo:  add branch id and name to the items ================
            if (AppObject.selectedBranchId != null && AppObject.selectedBranchId != 0) {
                branchId = AppObject.selectedBranchId!!
            } else if (AppObject.branchItem != null) {
                branchId = AppObject.branchItem!!.id
                branchName = AppObject.branchItem!!.name
            }
            discount = item.discountAmount
            discount_percent = item.discount_percent
            materials = mViewModel.gson.toJson(item.productMaterials)
            qty = 1
        }
        appDataBase.shopDao().saveItem(shopCartItem)
    }

    override fun <T> onIncreaseItem(position: Int, view: View, item: T) {
        item as Product
        val shopCartItem: ShopCartItem?

        shopCartItem = appDataBase.shopDao().findByIdAndMaterialMainThread(
            item.id, item.type,
            mViewModel.gson.toJson(item.productMaterials)
        )

        if (shopCartItem.max_choice == 0) {
            shopCartItem.max_choice == item.max_choice
        }

        if (item.type == 3 && shopCartItem.qty == item.max_choice) {
            showEToast(
                String.format(
                    getString(R.string.maxChoiceProduct),
                    item.max_choice,
                    item.name
                )
            )
            return
        }

        shopCartItem.qty++
        appDataBase.shopDao().updateItem(shopCartItem)

    }

    override fun <T> onDecreaseItem(position: Int, view: View, item: T) {
        item as Product
        if (item.shopCount == 1) {
            if (item.type == 3) {
                appDataBase.shopDao().deleteByProductIDAndMaterial(
                    item.id,
                    mViewModel.gson.toJson(item.productMaterials)
                )
            }
            return
        }
        val shopCartItem: ShopCartItem?
        if (item.type == 3) {
            shopCartItem = appDataBase.shopDao().findByIdAndMaterialMainThread(
                item.id, item.type,
                mViewModel.gson.toJson(item.productMaterials)
            )
            shopCartItem.qty--
            appDataBase.shopDao().updateItem(shopCartItem)
        }

    }


    private fun apiRequest() {
        if (!baseFragmentCallback!!.isNetworkAvailable()) {
            mViewModel.connectionError.set(true)
            return
        }

        if (mViewModel.loading.get())
            return

        disposable?.dispose()

        disposable = requestService.productOfferArchive(mViewModel.branchId!!)
            .performOnBackOutOnMain()
            .doOnSubscribe {
                //Enable loading
                mViewModel.loading.set(true)

                mViewModel.connectionError.set(false)
            }
            .subscribeWith(object : DisposableSingleObserver<ShopCartOfferModel>() {
                override fun onSuccess(it: ShopCartOfferModel) {
                    //Hide loading
                    mViewModel.loading.set(false)
                    mViewModel.isLoaded.set(true)
                    mViewModel.mItem = it
                    d("product/offers == > ${it} \n ${it.products}")
                    textView.text = it.description
                    setAdapter()

                }

                override fun onError(it: Throwable) {
                    //Hide loading
                    mViewModel.loading.set(false)
                    Logger.e(it.message.toString())

                    when (it) {
                        is HttpException -> {//Handle http exception
                            when (it.code()) {

                                403 -> {//Logout
                                    this@OffersDialogFragment.dialog?.dismiss()
                                    baseFragmentCallback?.logout()
                                }

                                else -> {
                                    mViewModel.connectionError.set(true)
                                }
                            }
                        }

                        else -> {//Handle other type of error
                            mViewModel.connectionError.set(true)
                        }
                    }
                }
            })
    }

}