package ir.atriatech.pizzawifi.ui.main.maker.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentMakerProductsBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.home.maker.Maker


class MakerProductsFragment : BaseFragment(), RecyclerViewTools {
    lateinit var binding: FragmentMakerProductsBinding
    private lateinit var mViewModel: MakerProductsFragmentViewModel

    init {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(MakerProductsFragmentViewModel::class.java)
        arguments?.getParcelable<Maker>(ARG_STRING_1)?.let {
            mViewModel.mItem.set(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_maker_products, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
//			updateDessertAndDrinkForMakerProduct()
            val arg = extraArguments(mViewModel.mItem.get()!!, ARG_STRING_1)
            navTo(R.id.makerLastStepFragment, arg = arg)
        }
        binding.btnPrev.setOnClickListener { back() }

        binding.imgHelp.setOnClickListener {
            MaterialDialog(requireContext())
                .apply {
                    title(text = mViewModel.mItem.get()!!.productsTitle)
                    message(text = mViewModel.mItem.get()!!.productsHelp)
                    positiveButton(R.string.close)
                    show()
                }
        }

        //Desserts
        setDessertsAdapter()
        //Drinks
        setDrinksAdapter()
    }

    override fun onDestroy() {
        mViewModel.mItem.get()?.destroyProducts()
//		appDataBase.shopDao().destroyAllFromMakerProducts()
        super.onDestroy()
    }

    override fun <T> onAddToCart(position: Int, view: View, item: T) {
        item as Product
        if (item.entity == 0)
            return

        item.fakeCount++
//		val shopCartItem = ShopCartItem()
//		shopCartItem.apply {
//			productID = item.id
//			productFromMaker = 1
//			name = item.name
//			image = item.image
//			price = item.price
//			discount = item.discountAmount
//			materials = gson.toJson(item.productMaterials)
//			qty = 1
//		}
//		appDataBase.shopDao().saveItem(shopCartItem)
    }

    override fun <T> onIncreaseItem(position: Int, view: View, item: T) {
        item as Product
        item.fakeCount++
//		val shopCartItem = appDataBase.shopDao().findByIdAndMaterialMainThread(item.id, gson.toJson(item.productMaterials))
//		shopCartItem.productFromMaker = 1
//		shopCartItem.qty++
//		appDataBase.shopDao().updateItem(shopCartItem)
    }

    override fun <T> onDecreaseItem(position: Int, view: View, item: T) {
        item as Product
        if (item.fakeCount <= 1) {
            item.fakeCount = 0
            return
        }
        item.fakeCount--
//		val shopCartItem = appDataBase.shopDao().findByIdAndMaterialMainThread(item.id, gson.toJson(item.productMaterials))
//		shopCartItem.productFromMaker = 1
//		shopCartItem.qty--
//		appDataBase.shopDao().updateItem(shopCartItem)
    }

    private fun setDessertsAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = MakerProductAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mItem.get()!!.desserts.toMutableList())
        }
        binding.rvDessert.setHasFixedSize(true)
        binding.rvDessert.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDessert.adapter = mViewModel.mAdapter
    }

    private fun setDrinksAdapter() {
        if (mViewModel.mAdapter2 == null) {
            mViewModel.mAdapter2 = MakerProductAdapter(this)
            mViewModel.mAdapter2!!.addToList(mViewModel.mItem.get()!!.drinks.toMutableList())
        }
        binding.rvDrinks.setHasFixedSize(true)
        binding.rvDrinks.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvDrinks.adapter = mViewModel.mAdapter2
    }

    private fun updateDessertAndDrinkForMakerProduct() {
        mViewModel.mItem.get()!!.desserts.forEach {
            if (it.isInDb) {
                appDataBase.shopDao()
                    .setFromMakerByProductIDAndMaterial(it.id, gson.toJson(it.productMaterials))
            }
        }

        mViewModel.mItem.get()!!.drinks.forEach {
            if (it.isInDb) {
                appDataBase.shopDao()
                    .setFromMakerByProductIDAndMaterial(it.id, gson.toJson(it.productMaterials))
            }
        }
    }
}