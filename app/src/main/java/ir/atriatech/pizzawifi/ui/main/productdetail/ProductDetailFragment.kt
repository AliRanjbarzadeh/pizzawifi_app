package ir.atriatech.pizzawifi.ui.main.productdetail

import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.ARG_STRING_3
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.app.findColorStateList
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.AppObject
import ir.atriatech.pizzawifi.common.HOME_FRG
import ir.atriatech.pizzawifi.databinding.FragmentProductDetailBinding
import ir.atriatech.pizzawifi.entities.Product
import ir.atriatech.pizzawifi.entities.ProductMaterial
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import kotlinx.android.synthetic.main.fragment_product_detail.*
import java.lang.reflect.Type


class ProductDetailFragment : BaseFragment() {
	lateinit var binding: FragmentProductDetailBinding
	private lateinit var mViewModel: ProductDetailFragmentViewModel
	private var mTransitionName = ""

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(ProductDetailFragmentViewModel::class.java)
		mViewModel.branchId = AppObject.selectedBranchId
		try {

			arguments?.getParcelable<Product>(ARG_STRING_1)?.let {
				mViewModel.mItem = it
				val listType: Type = object : TypeToken<MutableList<ProductMaterial>>() {}.type
				mViewModel.mListItems = gson.fromJson(it.serverMaterials, listType)
			}
			arguments?.getString(ARG_STRING_2)?.let { mTransitionName = it }
			arguments?.getString(ARG_STRING_3)?.let { mViewModel.tag = it }

		} catch (e: Exception) {
		}
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			binding.imgProduct.transitionName = mTransitionName
			sharedElementEnterTransition =
				TransitionInflater.from(requireContext())
					.inflateTransition(android.R.transition.move)
		}

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		if (mViewModel.tag == HOME_FRG) { // todo: come from home ============
			if (!mViewModel.isLoaded) {
				flBottom?.visibility = View.GONE
				mViewModel.getData()
			} else {
				flBottom?.visibility = View.VISIBLE
			}

		} else {
			flBottom?.visibility = View.VISIBLE
			setMaterialAdapter()
		}

		//fill temp data for materials
//		mViewModel.mItem.setFakeData()

		if (mViewModel.mItem.entity == 0) {
			binding.btnAdd.backgroundTintList = findColorStateList(R.color.grayt)
		}

		//Change decrease icon if count > 1
		if (mViewModel.mItem.shopCount > 1) {
			binding.btnDecrease.setImageResource(R.drawable.ic_baseline_remove_24)
		}

		//Set text animation
		binding.txtCount.inAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_in)
		binding.txtCount.outAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_out)

		//Real price through strike
		binding.txtRealPrice.paintFlags =
			binding.txtRealPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

		val discountPercent = "${mViewModel.mItem.discount_percent}%"
		binding.txtDiscount.text = discountPercent

		//Load image
		imageLoader.loadFitCenter(
			mViewModel.mItem.image.getImageUri(isCustomSize = true, size = "3x"),
			binding.imgProduct
		)

		binding.btnAdd.setOnClickListener {
			mViewModel.mItem as Product
			if (mViewModel.mItem.entity == 0)
				return@setOnClickListener

			val shopCartItem = ShopCartItem()
			shopCartItem.apply {
				productID = mViewModel.mItem.id
				name = mViewModel.mItem.name
				image = mViewModel.mItem.image
				price = mViewModel.mItem.price
				type = mViewModel.mItem.type
				max_choice = mViewModel.mItem.max_choice
				//todo:  add branch id and name to the items ================
				if (AppObject.selectedBranchId != null && AppObject.selectedBranchId != 0) {
					branchId = AppObject.selectedBranchId!!
				} else if (AppObject.branchItem != null) {
					branchId = AppObject.branchItem!!.id
					branchName = AppObject.branchItem!!.name
				}
				discount = mViewModel.mItem.discountAmount
				discount_percent = mViewModel.mItem.discount_percent
				materials = gson.toJson(mViewModel.mItem.productMaterials)
				qty = 1
			}
			d("TAG", "mViewModel.mItem.entity --> ${shopCartItem}")

			appDataBase.shopDao().saveItem(shopCartItem)
		}


		binding.btnIncrease.setOnClickListener {
			val shopCartItem = appDataBase.shopDao()
				.findByIdAndMaterialMainThread(
					mViewModel.mItem.id, mViewModel.mItem.type,
					gson.toJson(mViewModel.mItem.productMaterials)
				)

			if (mViewModel.mItem.type == 3 && shopCartItem.qty == mViewModel.mItem.max_choice) {
				eToast(
					String.format(
						getString(R.string.maxChoiceProduct),
						mViewModel.mItem.max_choice,
						mViewModel.mItem.name
					)
				)
				return@setOnClickListener
			}

			//Change decrease button image
			binding.btnDecrease.setImageResource(R.drawable.ic_baseline_remove_24)

			shopCartItem.qty++
			appDataBase.shopDao().updateItem(shopCartItem)
		}

		binding.btnDecrease.setOnClickListener {
			mViewModel.mItem as Product
			if (mViewModel.mItem.shopCount == 1) {
				appDataBase.shopDao().deleteByProductIDAndMaterial(
					mViewModel.mItem.id,
					gson.toJson(mViewModel.mItem.productMaterials)
				)
				return@setOnClickListener
			}
			val shopCartItem = appDataBase.shopDao()
				.findByIdAndMaterialMainThread(
					mViewModel.mItem.id, mViewModel.mItem.type,
					gson.toJson(mViewModel.mItem.productMaterials)
				)
			shopCartItem.qty--
			appDataBase.shopDao().updateItem(shopCartItem)

			//Change decrease button image
			if (shopCartItem.qty == 1)
				binding.btnDecrease.setImageResource(R.drawable.ic_trash2)
		}

	}

	override fun onDestroy() {
		mViewModel.mItem.reset()
		super.onDestroy()
	}

	private fun setMaterialAdapter() {
		if (mViewModel.materialAdapter == null) {
			mViewModel.materialAdapter = ProductMaterialAdapter(object : RecyclerViewTools {
				override fun <T> onItemClick(position: Int, view: View, item: T) {
					when (item) {
						is Boolean -> {
							mViewModel.mListItems[position].defaultCheck = if (item) {
								1
							} else {
								0
							}
						}
					}
				}
			})
			mViewModel.materialAdapter!!.addToList(mViewModel.mListItems)
		}

//		rvPersonalize.setHasFixedSize(false)
//		rvPersonalize.isNestedScrollingEnabled = false
//		rvPersonalize.layoutManager = LinearLayoutManager(requireContext())
//		rvPersonalize.adapter = mViewModel.materialAdapter
//
//		val decoration = MyItemDecoration(findColor(R.color.colorAA53), dp2px(1))
//		rvPersonalize.addItemDecoration(decoration)
	}


	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Product> {
			override fun onProgress(loading: Boolean) {
				flBottom?.visibility = View.GONE
				setProgressView(binding.mainView, loading, 2)

			}

			override fun onSuccess(data: Product) {
				flBottom?.visibility = View.VISIBLE
				mViewModel.isLoaded = true
				mViewModel.mItem = data
				if (data.type == 0) {
					appDataBase.shopDao()
						.findByIdAndMaterial(data.id, data.type, gson.toJson(data.productMaterials))
						.observeForever {
							if (it != null) {
								data.isInDb = true
								if (data.shopCount != it.qty) {
									data.shopCount = it.qty
								}
							} else {
								data.isInDb = false
								data.shopCount = 0
							}
						}
					if (data.entity == 0) {
						appDataBase.shopDao().deleteByProductID(data.id)
					}
				} else {
					appDataBase.shopDao().findPizzaById(data.id)
						.observeForever {
							if (it != null) {
								data.isInDb = true
								if (data.shopCount != it.qty) {
									data.shopCount = it.qty
								}


							} else {
								data.isInDb = false
								data.shopCount = 0
							}
						}
					if (data.entity == 0) {
						appDataBase.shopDao().deleteByProductID(data.id)
					}
				}

				val listType: Type = object : TypeToken<MutableList<ProductMaterial>>() {}.type
				mViewModel.mListItems = gson.fromJson(data.serverMaterials, listType)
				setMaterialAdapter()
				if (mViewModel.mItem.entity == 0) {
					binding.btnAdd.backgroundTintList = findColorStateList(R.color.grayt)
				}

				binding.viewModel = mViewModel
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 4)
	}


}