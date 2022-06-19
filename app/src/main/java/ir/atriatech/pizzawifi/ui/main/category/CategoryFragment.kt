package ir.atriatech.pizzawifi.ui.main.category

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.google.android.material.tabs.TabLayoutMediator
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.databinding.FragmentCategoryBinding
import ir.atriatech.pizzawifi.databinding.TemplateTabItemBinding
import ir.atriatech.pizzawifi.entities.Category
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import kotlinx.android.synthetic.main.fragment_category.*


class CategoryFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentCategoryBinding
	var categories = arrayListOf<Category>()
	private lateinit var mViewModel: CategoryFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(CategoryFragmentViewModel::class.java)

		try {
			arguments?.getParcelableArrayList<Category>(ARG_STRING_1)?.let {
				categories = it
			}
		} catch (e: Exception) {
			e(e.message, "CategoryFragment")
		}
		mViewModel.mAdapter = CategoryAdapter(this, categories)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
		binding.viewModel = mViewModel
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		postponeEnterTransition()
		view.viewTreeObserver
			.addOnPreDrawListener {
				startPostponedEnterTransition()
				true
			}

		binding.categoryViewPager.isSaveEnabled = false
		binding.categoryViewPager.adapter = mViewModel.mAdapter
		TabLayoutMediator(binding.categoryTabLayout, binding.categoryViewPager) { tab, position ->
			val name = categories[position].name
			tab.text = name

			val templateTabItemBinding = DataBindingUtil.inflate<TemplateTabItemBinding>(LayoutInflater.from(requireContext()), R.layout.template_tab_item, null, false)
			tab.customView = templateTabItemBinding.root

			templateTabItemBinding.name = name

			try {
				val icon = categories[position].icon
				imageLoader.load(
					icon.getImageUri(isCustomSize = true, size = ""),
					templateTabItemBinding.imgIcon,
					false,
					R.drawable.ic_pizza
				)
			} catch (e: Exception) {
				imageLoader.load(R.drawable.ic_pizza, templateTabItemBinding.imgIcon)
			}

			templateTabItemBinding.executePendingBindings()
		}.attach()
	}

	override fun onDestroyView() {
		binding.categoryViewPager.adapter = null
		super.onDestroyView()
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		val product = item as Product

		d("product -- > ${product}")
		if (product.type == 0) {
			var extras: FragmentNavigator.Extras? = null
			val transitionName = "productTrans${categoryViewPager.currentItem}${position}"
			val arg = extraArguments(product, ARG_STRING_1, transitionName, ARG_STRING_2)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				val imgProduct = view.findViewById<AppCompatImageView>(R.id.imgProduct)
				imgProduct.transitionName = transitionName
				extras = FragmentNavigatorExtras(
					imgProduct to transitionName
				)
			}
			navTo(R.id.productDetailFragment, arg = arg, navigatorExtras = extras)
		} else {
			val pizza = Pizza()
			pizza.apply {
				id = product.id
				name = product.name
				entity = product.entity
				price = product.price
				shopCount = product.shopCount
				serverMaterials = product.serverMaterials
			}
			var extras: FragmentNavigator.Extras? = null
			val transitionName = "UserPizza${position}"
			val arg = extraArguments(pizza, ARG_STRING_1, transitionName, ARG_STRING_2)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				val imgProduct = view.findViewById<AppCompatImageView>(R.id.imgProduct)
				imgProduct.transitionName = transitionName
				extras = FragmentNavigatorExtras(
					imgProduct to transitionName
				)
			}
			navTo(R.id.pizzaDetailFragment2, arg = arg, navigatorExtras = extras)
		}
	}

	override fun <T> onAddToCart(position: Int, view: View, item: T) {
		item as Product
		d("TAG", "addToCart category111 => ${item.entity} isInDb: ${item.isInDb}")
		if (item.entity == 0)
			return

		d("TAG", "addToCart category => ${item.entity} isInDb: ${item.isInDb}")
		val shopCartItem = ShopCartItem()
		shopCartItem.apply {
			productID = item.id
			name = item.name
			image = item.image
			price = item.price
			type = item.type
			max_choice = item.max_choice
			branchId = AppObject.branchItem!!.id
			branchName = AppObject.branchItem!!.name
			discount = item.discountAmount
			discount_percent = item.discount_percent
			materials = gson.toJson(item.productMaterials)
			qty = 1
		}
		if (item.type == 1) {
			shopCartItem.pizza = 1
			shopCartItem.materials = gson.toJson(item.serverMaterials)
		}
		appDataBase.shopDao().saveItem(shopCartItem)
	}

	override fun <T> onIncreaseItem(position: Int, view: View, item: T) {
		item as Product
		val shopCartItem = if (item.type == 0) {
			appDataBase.shopDao()
				.findByIdAndMaterialMainThread(
					item.id,
					item.type,
					gson.toJson(item.productMaterials)
				)
		} else {
			appDataBase.shopDao().findPizzaByIdMainThread(item.id)
		}


		shopCartItem.qty++
		appDataBase.shopDao().updateItem(shopCartItem)
	}

	override fun <T> onDecreaseItem(position: Int, view: View, item: T) {
		item as Product
		if (item.shopCount == 1) {
			if (item.type == 0) {
				appDataBase.shopDao()
					.deleteByProductIDAndMaterial(item.id, gson.toJson(item.productMaterials))
			} else {
				appDataBase.shopDao().deletePizzaById(item.id)
			}
			return
		}
		val shopCartItem = if (item.type == 0) {
			appDataBase.shopDao()
				.findByIdAndMaterialMainThread(
					item.id,
					item.type,
					gson.toJson(item.productMaterials)
				)
		} else {
			appDataBase.shopDao().findPizzaByIdMainThread(item.id)
		}
		shopCartItem.qty--
		appDataBase.shopDao().updateItem(shopCartItem)
	}
}