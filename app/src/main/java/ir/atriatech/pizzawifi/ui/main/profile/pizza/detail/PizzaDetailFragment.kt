package ir.atriatech.pizzawifi.ui.main.profile.pizza.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionInflater
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.PickImageCallback
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.pickImage
import ir.atriatech.extensions.android.pickImageOnActivityResult
import ir.atriatech.extensions.app.Ext
import ir.atriatech.extensions.app.findColorStateList
import ir.atriatech.extensions.app.findDrawable
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentPizzaDetailBinding
import ir.atriatech.pizzawifi.entities.home.maker.Bread
import ir.atriatech.pizzawifi.entities.pizza.Pizza
import ir.atriatech.pizzawifi.entities.pizza.PizzaMaterial
import ir.atriatech.pizzawifi.entities.shopcart.ShopCartItem


class PizzaDetailFragment : BaseFragment() {
	lateinit var binding: FragmentPizzaDetailBinding
	private lateinit var mViewModel: PizzaDetailFragmentViewModel
	private var mTransitionName = ""

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(PizzaDetailFragmentViewModel::class.java)

		try {
			arguments?.getParcelable<Pizza>(ARG_STRING_1)?.let { mViewModel.mItem = it }
			arguments?.getString(ARG_STRING_2)?.let { mTransitionName = it }
			mViewModel.mItem.pizzaMaterials =
				gson.fromJson(mViewModel.mItem.serverMaterials, Bread::class.java)
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
			DataBindingUtil.inflate(inflater, R.layout.fragment_pizza_detail, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel

		binding.imgProduct.transitionName = mTransitionName
		sharedElementEnterTransition =
			TransitionInflater.from(requireContext())
				.inflateTransition(android.R.transition.move)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		appDataBase.shopDao().findPizzaById(mViewModel.mItem.id).observeForever {
			if (it != null) {
				if (mViewModel.mItem.shopCount != it.qty)
					mViewModel.mItem.shopCount = it.qty
			} else {
				mViewModel.mItem.shopCount = 0
			}
		}

		if (mViewModel.mItem.image.isNotEmpty()) {
			imageLoader.load(
				mViewModel.mItem.image.getImageUri(isCustomSize = true, size = "2x"),
				binding.imgProduct
			)
		}

		//Set text animation
		binding.txtCount.inAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_in)
		binding.txtCount.outAnimation = AnimationUtils.loadAnimation(Ext.ctx, R.anim.bounce_out)

		//Increase & Decrease
		binding.btnAdd.setOnClickListener {
			if (mViewModel.mItem.entity == 0)
				return@setOnClickListener

			val shopCartItem = ShopCartItem()
			shopCartItem.apply {
				productID = mViewModel.mItem.id
				name = mViewModel.mItem.name
				price = mViewModel.mItem.price
				pizza = 1
				materials = gson.toJson(mViewModel.mItem.serverMaterials)
				qty = 1
			}
			appDataBase.shopDao().saveItem(shopCartItem)
		}
		if (mViewModel.mItem.entity == 0) {
			binding.btnAdd.backgroundTintList = findColorStateList(R.color.colorPrimary)
		}
		binding.btnIncrease.setOnClickListener {
			if (mViewModel.mItem.entity == 0)
				return@setOnClickListener

			val shopCartItem = appDataBase.shopDao().findPizzaByIdMainThread(mViewModel.mItem.id)
			shopCartItem.qty++
			appDataBase.shopDao().updateItem(shopCartItem)
		}
		binding.btnDecrease.setOnClickListener {
			if (mViewModel.mItem.shopCount == 1) {
				appDataBase.shopDao().deletePizzaById(mViewModel.mItem.id)
				return@setOnClickListener
			}
			val shopCartItem = appDataBase.shopDao().findPizzaByIdMainThread(mViewModel.mItem.id)
			shopCartItem.qty--
			appDataBase.shopDao().updateItem(shopCartItem)
		}

		binding.btnSendToCustomerMenu.setOnClickListener {
			if (mViewModel.mItem.inMenu != 0) {
				return@setOnClickListener
			}
			MaterialDialog(requireContext())
				.apply {
					title(R.string.sendToMenuTitle)
					message(R.string.sureSendToMenuDescription)
					positiveButton(R.string._yes)
						.positiveButton {
							mViewModel.pizzaSendToMenu()
						}
					negativeButton(R.string._no)
					show()
				}
		}

		binding.selectImage.setOnClickListener { pickImage(size = 1024) }

		setAdapter()
		checkInMenu()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		pickImageOnActivityResult(requestCode, data, object : PickImageCallback {
			override fun onSuccess(uri: Uri) {
				binding.imgProduct.setImageURI(uri)
				mViewModel.uri = uri
			}

			override fun onFail(error: Exception) {
				eToast(findString(R.string.wrongImageValue))
			}
		})
	}

	override fun onDestroy() {
		super.onDestroy()
		binding.btnSendToCustomerMenu.dispose()
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = PizzaDetailMaterialAdapter(object : RecyclerViewTools {})

			val pizzaMaterials = mutableListOf<PizzaMaterial>()
			pizzaMaterials.add(
				PizzaMaterial(
					id = mViewModel.mItem.pizzaMaterials.materialId,
					name = mViewModel.mItem.pizzaMaterials.name,
					image = mViewModel.mItem.pizzaMaterials.icon
				)
			)

			val rightMaterials = mutableListOf<PizzaMaterial>()
			val leftMaterials = mutableListOf<PizzaMaterial>()

			mViewModel.mItem.pizzaMaterials.steps.forEach { step ->
				step.materials.forEach { material ->
					pizzaMaterials.add(
						PizzaMaterial(
							id = material.materialId,
							name = material.name,
							image = material.icon
						)
					)
				}

				step.rightMaterials.forEach { material ->
					rightMaterials.add(
						PizzaMaterial(
							id = material.materialId,
							name = material.name,
							image = material.icon
						)
					)
				}

				step.leftMaterials.forEach { material ->
					leftMaterials.add(
						PizzaMaterial(
							id = material.materialId,
							name = material.name,
							image = material.icon
						)
					)
				}
			}
			mViewModel.mAdapter!!.addToList(pizzaMaterials)

			if (mViewModel.isRight()) {
				mViewModel.mRightAdapter = PizzaDetailMaterialAdapter(object : RecyclerViewTools {})
				mViewModel.mRightAdapter!!.addToList(rightMaterials)
				binding.rvRightMaterials.setHasFixedSize(true)
				binding.rvRightMaterials.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
				binding.rvRightMaterials.adapter = mViewModel.mRightAdapter
			}

			if (mViewModel.isLeft()) {
				mViewModel.mLeftAdapter = PizzaDetailMaterialAdapter(object : RecyclerViewTools {})
				mViewModel.mLeftAdapter!!.addToList(leftMaterials)
				binding.rvLeftMaterials.setHasFixedSize(true)
				binding.rvLeftMaterials.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
				binding.rvLeftMaterials.adapter = mViewModel.mLeftAdapter
			}
		}
		binding.rvMaterials.setHasFixedSize(true)
		binding.rvMaterials.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
		binding.rvMaterials.adapter = mViewModel.mAdapter

	}

	private fun checkInMenu() {
		when (mViewModel.mItem.inMenu) {
			1 -> {
				//Pending
				binding.btnSendToCustomerMenu.background =
					findDrawable(R.drawable.bg_progress_primary)
				binding.description.text = "درخواست شما ثبت شده و در حال بررسی می باشد"
				binding.btnSendToCustomerMenu.text = "در حال بررسی"
			}
			2 -> {
				//Approved
				binding.btnSendToCustomerMenu.background =
					findDrawable(R.drawable.bg_progress_success)
				binding.description.visibility = View.GONE
				binding.btnSendToCustomerMenu.text = "تایید شده"
			}
			3 -> {
				//Declined
				binding.btnSendToCustomerMenu.background = findDrawable(R.drawable.bg_progress_red)
				binding.description.visibility = View.GONE
				binding.btnSendToCustomerMenu.text = "رد شده"
			}
		}
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				if (loading) {
					binding.btnSendToCustomerMenu.startAnimation()
				}
			}

			override fun onSuccess(data: Msg) {
				mViewModel.mItem.inMenu = 1
				binding.btnSendToCustomerMenu.revertAnimation {
					checkInMenu()
				}
			}

			override fun onFailed(errorMessage: String) {
				binding.btnSendToCustomerMenu.revertAnimation()
			}
		}, 1)
	}
}