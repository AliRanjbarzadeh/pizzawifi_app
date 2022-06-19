package ir.atriatech.pizzawifi.ui.main.maker.laststep

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.reflect.TypeToken
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.android.showInputMethod
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.app.findColor
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.extensions.kotlin.priceFormat
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.HalfImageTransformation
import ir.atriatech.pizzawifi.base.MyItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentMakerLastStepBinding
import ir.atriatech.pizzawifi.entities.ProductMaterial
import ir.atriatech.pizzawifi.entities.home.maker.Maker
import ir.atriatech.pizzawifi.entities.pizza.PizzaMaterial
import ir.atriatech.pizzawifi.entities.shopcart.BreadJson
import ir.atriatech.pizzawifi.entities.shopcart.MaterialJson
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem
import ir.atriatech.pizzawifi.entities.shopcart.StepJson
import java.lang.reflect.Type

class MakerLastStepFragment : BaseFragment() {
	lateinit var binding: FragmentMakerLastStepBinding
	private lateinit var mViewModel: MakerLastStepFragmentViewModel
	private var shopCartItems = mutableListOf<ShopCartItem>()

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MakerLastStepFragmentViewModel::class.java)
		arguments?.getParcelable<Maker>(ARG_STRING_1)?.let {
			mViewModel.mItem.set(it)
			mViewModel.calculatePrice()
		}

		arguments?.getBoolean(ARG_STRING_2)?.let { isHalf: Boolean -> mViewModel.isHalf = isHalf }
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_maker_last_step, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		hideKeyboard(binding.mainView)
		hideKeyboard(binding.cardMaterials)
		hideKeyboard(binding.rvMaterials)
		hideKeyboard(binding.rvRightMaterials)
		hideKeyboard(binding.rvLeftMaterials)
		hideKeyboard(binding.cardProducts)
		hideKeyboard(binding.rvProducts)

		//Set text animation
		binding.txtCount.inAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_in)
		binding.txtCount.outAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_out)

		binding.btnIncrease.setOnClickListener {
			mViewModel.mQty.set(mViewModel.mQty.get()!! + 1)
			mViewModel.priceFormat.set((mViewModel.mQty.get()!! * mViewModel.mPrice.get()!!).priceFormat())
		}
		binding.btnDecrease.setOnClickListener {
			if (mViewModel.mQty.get() == 1)
				return@setOnClickListener
			mViewModel.mQty.set(mViewModel.mQty.get()!! - 1)
			mViewModel.priceFormat.set((mViewModel.mQty.get()!! * mViewModel.mPrice.get()!!).priceFormat())
		}

		setMaterialAdapter()
		setFrames()
		if (mViewModel.mItem.get()!!.drinks.isNotEmpty()) {
			mViewModel.mListItems.addAll(mViewModel.mItem.get()!!.drinks.filter { it.fakeCount > 0 })
		}
		if (mViewModel.mItem.get()!!.desserts.isNotEmpty()) {
			mViewModel.mListItems.addAll(mViewModel.mItem.get()!!.desserts.filter { it.fakeCount > 0 })
		}

		if (mViewModel.mListItems.isNotEmpty()) {
			val productIDs = mutableListOf<Int>()
			val productMaterials = mutableListOf<String>()
			for (mListItem in mViewModel.mListItems) {
				val listType: Type = object : TypeToken<MutableList<ProductMaterial>>() {}.type
				mListItem.productMaterials = gson.fromJson(mListItem.serverMaterials, listType)

				productIDs.add(mListItem.id)
				productMaterials.add(gson.toJson(mListItem.productMaterials))
			}
			shopCartItems = appDataBase.shopDao().getAll(productIDs, productMaterials)
			setProductsAdapter()
		}
		mViewModel.isEmptyProducts.set(mViewModel.mListItems.isEmpty())

		binding.btnNext.setOnClickListener {
			if (mViewModel.name.isEmpty()) {
				eToast(findString(R.string.emptyPizzaName))
				showInputMethod(binding.etName)
				return@setOnClickListener
			}

			for (mListItem in mViewModel.mListItems) {
				for (shopCartItem in shopCartItems) {
					if (shopCartItem.productID == mListItem.id) {
						shopCartItem.qty += mListItem.fakeCount
						mListItem.isInDb = true
					}
				}
				if (!mListItem.isInDb) {
					val mShopcartItem = ShopCartItem()
					mShopcartItem.apply {
						productID = mListItem.id
						name = mListItem.name
						image = mListItem.image
						price = mListItem.price
						discount = mListItem.discountAmount
						materials = gson.toJson(mListItem.productMaterials)
						qty = mListItem.fakeCount
					}
					shopCartItems.add(mShopcartItem)
				}
			}

			val breadJson = BreadJson()
			mViewModel.mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
				breadJson.apply {
					id = bread.id
					materialId = bread.materialId
				}

				//steps
				bread.steps.forEach { step ->
					val stepJson = StepJson()
					stepJson.id = step.id

					//materials
					if (step.qty > 0) {
						step.materials.forEach { material ->
							if (material.qty > 0) {
								val materialJson = MaterialJson(
									id = material.id,
									materialId = material.materialId,
									qty = material.qty
								)
								//add material to step
								stepJson.materials.add(materialJson)
							}
						}
					}

					//left materials
					if (step.leftQty > 0) {
						step.leftMaterials.forEach { material ->
							if (material.qty > 0) {
								val materialJson = MaterialJson(
									id = material.id,
									materialId = material.materialId,
									qty = material.qty
								)
								//add material to step
								stepJson.leftMaterials.add(materialJson)
							}
						}
					}

					//right materials
					if (step.leftQty > 0) {
						step.rightMaterials.forEach { material ->
							if (material.qty > 0) {
								val materialJson = MaterialJson(
									id = material.id,
									materialId = material.materialId,
									qty = material.qty
								)
								//add material to step
								stepJson.rightMaterials.add(materialJson)
							}
						}
					}

					//add step to bread
					breadJson.steps.add(stepJson)
				}
			}

			val makerShopCartItem = ShopCartItem()
			makerShopCartItem.apply {
				name = mViewModel.name
				price = mViewModel.mPrice.get()!!
				qty = mViewModel.mQty.get()!!
				materials = gson.toJson(breadJson)
				pizza = 1
			}

			shopCartItems.add(makerShopCartItem)
			appDataBase.shopDao().saveItem(*shopCartItems.toTypedArray())
			navTo(R.id.decideFragment)
		}


		binding.etName.postDelayed({
			showInputMethod(binding.etName)
		}, 100)
	}

	override fun onVisibilityChanged(isOpen: Boolean) {
		if (!isOpen) {
			binding.etName.clearFocus()
			binding.mainView.requestFocus()
		}
	}

	private fun setMaterialAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = MakerLastStepMaterialAdapter()
			mViewModel.mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
				mViewModel.mAdapter!!.list.add(PizzaMaterial(name = bread.name, image = bread.icon))
				bread.steps.forEach { step ->
					if (step.qty > 0) {
						step.materials.forEach { material ->
							if (material.qty > 0)
								mViewModel.mAdapter!!.list.add(
									PizzaMaterial(
										name = material.name,
										image = material.icon
									)
								)
						}
					}
				}
			}
		}

		if (mViewModel.mRightAdapter == null) {
			mViewModel.mRightAdapter = MakerLastStepMaterialAdapter()

			mViewModel.mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
				bread.steps.forEach { step ->
					if (step.rightQty > 0) {
						step.rightMaterials.forEach { rightMaterial ->
							if (rightMaterial.qty > 0) {
								mViewModel.mRightAdapter!!.list.add(
									PizzaMaterial(
										name = rightMaterial.name,
										image = rightMaterial.icon,
									)
								)
							}
						}
					}
				}
			}

			if (mViewModel.mRightAdapter!!.list.size == 0) {
				mViewModel.isShowRight.set(false)
			} else {
				binding.rvRightMaterials.adapter = mViewModel.mRightAdapter
			}
		}

		if (mViewModel.mLeftAdapter == null) {
			mViewModel.mLeftAdapter = MakerLastStepMaterialAdapter()

			mViewModel.mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
				bread.steps.forEach { step ->
					if (step.leftQty > 0) {
						step.leftMaterials.forEach { leftMaterial ->
							if (leftMaterial.qty > 0) {
								mViewModel.mLeftAdapter!!.list.add(
									PizzaMaterial(
										name = leftMaterial.name,
										image = leftMaterial.icon,
									)
								)
							}
						}
					}
				}
			}

			if (mViewModel.mLeftAdapter!!.list.size == 0) {
				mViewModel.isShowLeft.set(false)
			} else {
				binding.rvLeftMaterials.adapter = mViewModel.mLeftAdapter
			}
		}

		binding.rvMaterials.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
		binding.rvMaterials.setHasFixedSize(true)
		binding.rvMaterials.adapter = mViewModel.mAdapter
	}

	private fun setFrames() {
		mViewModel.mItem.get()!!.breads.find { it.isSelected }?.let { bread ->
			//Add bread
			val breadImage = AppCompatImageView(requireContext())
			breadImage.layoutParams = FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT
			)
			binding.imgShape.addView(breadImage)
			imageLoader.load(
				bread.plate.getImageUri("2x", true),
				breadImage, false
			)
			bread.steps.forEach { step ->
				if (step.qty > 0) {
					step.materials.forEach { material ->
						if (material.qty > 0) {
							val materialImage = AppCompatImageView(requireContext())
							materialImage.layoutParams = FrameLayout.LayoutParams(
								FrameLayout.LayoutParams.MATCH_PARENT,
								FrameLayout.LayoutParams.MATCH_PARENT
							)
							binding.imgShape.addView(materialImage)

							var image = ""
							when (material.qty) {
								1 -> {
									image = material.plate
								}
								2 -> {
									image = material.plate2
								}
								3 -> {
									image = material.plate3
								}
							}
							//load material plate image
							imageLoader.load(
								image.getImageUri("2x", true),
								materialImage, false
							)
						}
					}
				}

				if (step.rightQty > 0) {
					step.rightMaterials.forEach { rightMaterial ->
						val materialImage = AppCompatImageView(requireContext())
						materialImage.layoutParams = FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.MATCH_PARENT,
							FrameLayout.LayoutParams.MATCH_PARENT
						)
						binding.imgShape.addView(materialImage)

						var image = ""
						when (rightMaterial.qty) {
							1 -> {
								image = rightMaterial.plate
							}
							2 -> {
								image = rightMaterial.plate2
							}
							3 -> {
								image = rightMaterial.plate3
							}
						}
						//load material plate image
						val halfImageTransformation = HalfImageTransformation("right")
						imageLoader.loadWithTransform(
							uri = image.getImageUri("2x", true),
							imageView = materialImage,
							transformation = halfImageTransformation,
							isCenterCrop = false
						)
					}
				}

				if (step.leftQty > 0) {
					step.leftMaterials.forEach { leftMaterial ->
						val materialImage = AppCompatImageView(requireContext())
						materialImage.layoutParams = FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.MATCH_PARENT,
							FrameLayout.LayoutParams.MATCH_PARENT
						)
						binding.imgShape.addView(materialImage)

						var image = ""
						when (leftMaterial.qty) {
							1 -> {
								image = leftMaterial.plate
							}
							2 -> {
								image = leftMaterial.plate2
							}
							3 -> {
								image = leftMaterial.plate3
							}
						}
						//load material plate image
						val halfImageTransformation = HalfImageTransformation("left")
						imageLoader.loadWithTransform(
							uri = image.getImageUri("2x", true),
							imageView = materialImage,
							transformation = halfImageTransformation,
							isCenterCrop = false
						)
					}
				}
			}
		}
	}

	private fun setProductsAdapter() {
		if (mViewModel.mAdapter2 == null) {
			mViewModel.mAdapter2 = MakerLastStepProductlAdapter()
			mViewModel.mAdapter2!!.addToList(mViewModel.mListItems)
		}
		try {
			binding.rvProducts.removeItemDecorationAt(0)
		} catch (ex: Exception) {
		}

		binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
		binding.rvProducts.setHasFixedSize(false)
		binding.rvProducts.isNestedScrollingEnabled = false
		binding.rvProducts.addItemDecoration(
			MyItemDecoration(
				findColor(R.color.colorAA53),
				dp2px(1)
			)
		)
		binding.rvProducts.adapter = mViewModel.mAdapter2
	}
}