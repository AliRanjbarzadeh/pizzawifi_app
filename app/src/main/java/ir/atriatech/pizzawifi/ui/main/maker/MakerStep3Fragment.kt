package ir.atriatech.pizzawifi.ui.main.maker

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Callback
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.customlibs.ProgressView
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.HalfImageTransformation
import ir.atriatech.pizzawifi.databinding.FragmentMakerStep3Binding
import ir.atriatech.pizzawifi.entities.home.maker.Maker
import ir.atriatech.pizzawifi.entities.home.maker.Material


class MakerStep3Fragment : BaseFragment(), ViewPager.OnPageChangeListener {
	lateinit var binding: FragmentMakerStep3Binding
	private lateinit var mViewModel: MakerStep3FragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MakerStep3FragmentViewModel::class.java)
		arguments?.getParcelable<Maker>(ARG_STRING_1)?.let {
			mViewModel.mItem.set(it)
			for (i in 0 until it.breads.size) {
				val bread = it.breads[i]
				if (bread.isSelected) {
					mViewModel.mBread.set(bread)
					mViewModel.mStep.set(bread.steps[0])
					break
				}
			}
		}

		arguments?.getBoolean(ARG_STRING_2)?.let { mViewModel.isHalf = it }
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maker_step3, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAdapter()
		setFrames()

		binding.vpSteps.addOnPageChangeListener(this)
		binding.btnNext.setOnClickListener {
			val step = mViewModel.mStep.get()!!
			if (step.half == 1 && mViewModel.isHalf) {
				if (step.leftQty < step.minChoice) {
					eToast(
						String.format(
							findString(R.string.minStepLeftMaterialError),
							step.name,
							step.minChoice
						)
					)
					return@setOnClickListener
				}
				if (step.rightQty < step.minChoice) {
					eToast(
						String.format(
							findString(R.string.minStepRightMaterialError),
							step.name,
							step.minChoice
						)
					)
					return@setOnClickListener
				}
			} else {
				if (step.qty < step.minChoice) {
					eToast(
						String.format(
							findString(R.string.minStepMaterialError),
							step.name,
							step.minChoice
						)
					)
					return@setOnClickListener
				}
			}

			if (binding.vpSteps.currentItem < mViewModel.mBread.get()!!.steps.size - 1) {
				binding.vpSteps.setCurrentItem(binding.vpSteps.currentItem + 1, true)
			} else {
				val arg = extraArguments(argument1 = mViewModel.mItem.get()!!, key1 = ARG_STRING_1, argument2 = mViewModel.isHalf, key2 = ARG_STRING_2)
				navTo(R.id.makerLastStepFragment, arg = arg)
//                if (mViewModel.mItem.get()!!.isHasProduct()) {
//                    val arg = extraArguments(mViewModel.mItem.get()!!, ARG_STRING_1)
//                    navTo(R.id.makerProductsFragment, arg = arg)
//                } else {
//                }
			}
		}

		binding.btnPrev.setOnClickListener {
			if (binding.vpSteps.currentItem > 0) {
				binding.vpSteps.currentItem = binding.vpSteps.currentItem - 1
			} else {
				back()
			}
		}

		binding.imgHelp.setOnClickListener {
			if (mDialog == null || mDialog?.isShowing == false) {
				mDialog = MaterialDialog(requireContext())
					.apply {
						title(text = mViewModel.mStep.get()!!.name)
						message(text = mViewModel.mStep.get()!!.description)
						positiveButton(R.string.close)
						show()
					}
			}
		}
	}

	override fun onPageScrollStateChanged(state: Int) {}

	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

	override fun onPageSelected(position: Int) {
		mViewModel.mStep.set(mViewModel.mBread.get()!!.steps[position])
	}

	val makerActions = object : RecyclerViewTools {
		override fun <T> onItemClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			if (item.entity == 0) {
				return
			}
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			when (mViewModel.mStep.get()?.isSingle) {
				0 -> {
					if (isStepOverLap()) {
						if (item.qty == 0 || (item.qty < 3 && item.type != 1)) {
							eToast(
								String.format(
									findString(R.string.stepOverLap),
									mViewModel.mStep.get()!!.name,
									mViewModel.mStep.get()!!.maxChoice
								)
							)
						}
						return
					}

					//be carefull it works only in this project
					if (item.qty == item.type) {
						return
					}

					increaseMaterial(item, progressView)
				}

				1 -> {
					val cMaterial = getCurrentSelectedMaterial()
					if (cMaterial != null && cMaterial != item) {
						eToast(
							String.format(
								findString(R.string.stepSingleMaterialError),
								mViewModel.mStep.get()!!.name
							)
						)
						return
					}

					if (item.type == 1 && cMaterial == item) {
						return
					}

					if (isStepOverLap()) {
						eToast(
							String.format(
								findString(R.string.stepOverLap),
								mViewModel.mStep.get()!!.name,
								mViewModel.mStep.get()!!.maxChoice
							)
						)
						return
					}
					increaseMaterial(item, progressView)
				}

				else -> eToast("step invalid")
			}
			changeOtherMaterials(true)
		}

		override fun <T> onDeleteClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			progressView.progress = 0f
			mViewModel.mStep.get()!!.qty -= item.qty
			when (item.qty) {
				1 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory)
				}
				2 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory2)
				}
				3 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory3)
				}
			}
			item.qty = 0
			changeOtherMaterials(false)

			//remove material plate image
			binding.frameMaterial.findViewWithTag<FrameLayout>(mViewModel.mStep.get())
				?.let { frameLayout ->
					frameLayout.findViewWithTag<AppCompatImageView>("${item.id}-${item.name}-center")?.setImageResource(0)
				}

		}
	}

	val makerRightActions = object : RecyclerViewTools {
		override fun <T> onItemClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			if (item.entity == 0) {
				return
			}
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			when (mViewModel.mStep.get()?.isSingle) {
				0 -> {
					if (isStepOverLapRight()) {
						if (item.qty == 0 || (item.qty < 3 && item.type != 1)) {
							eToast(
								String.format(
									findString(R.string.stepOverLapRight),
									mViewModel.mStep.get()!!.name,
									mViewModel.mStep.get()!!.maxChoice
								)
							)
						}
						return
					}

					//be carefull it works only in this project
					if (item.qty == item.type) {
						return
					}

					increaseRightMaterial(item, progressView)
				}

				1 -> {
					val cMaterial = getCurrentSelectedMaterial("right")
					if (cMaterial != null && cMaterial != item) {
						eToast(
							String.format(
								findString(R.string.stepSingleMaterialError),
								mViewModel.mStep.get()!!.name
							)
						)
						return
					}

					if (item.type == 1 && cMaterial == item) {
						return
					}

					if (isStepOverLapRight()) {
						eToast(
							String.format(
								findString(R.string.stepOverLapRight),
								mViewModel.mStep.get()!!.name,
								mViewModel.mStep.get()!!.maxChoice
							)
						)
						return
					}
					increaseRightMaterial(item, progressView)
				}

				else -> eToast("step invalid")
			}
			changeOtherMaterials(isDisable = true, position = "right")
		}

		override fun <T> onDeleteClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			progressView.progress = 0f
			mViewModel.mStep.get()!!.rightQty -= item.qty
			when (item.qty) {
				1 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory / 2))
				}
				2 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory2 / 2))
				}
				3 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory3 / 2))
				}
			}
			item.qty = 0
			changeOtherMaterials(isDisable = false, position = "right")

			//remove material plate image
			binding.frameMaterial.findViewWithTag<FrameLayout>(mViewModel.mStep.get())
				?.let { frameLayout ->
					frameLayout.findViewWithTag<AppCompatImageView>("${item.id}-${item.name}-right")?.setImageResource(0)
				}

		}
	}

	val makerLeftActions = object : RecyclerViewTools {
		override fun <T> onItemClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			if (item.entity == 0) {
				return
			}
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			when (mViewModel.mStep.get()?.isSingle) {
				0 -> {
					if (isStepOverLapLeft()) {
						if (item.qty == 0 || (item.qty < 3 && item.type != 1)) {
							eToast(
								String.format(
									findString(R.string.stepOverLapLeft),
									mViewModel.mStep.get()!!.name,
									mViewModel.mStep.get()!!.maxChoice
								)
							)
						}
						return
					}

					//be carefull it works only in this project
					if (item.qty == item.type) {
						return
					}

					increaseLeftMaterial(item, progressView)
				}

				1 -> {
					val cMaterial = getCurrentSelectedMaterial("left")
					if (cMaterial != null && cMaterial != item) {
						eToast(
							String.format(
								findString(R.string.stepSingleMaterialError),
								mViewModel.mStep.get()!!.name
							)
						)
						return
					}

					if (item.type == 1 && cMaterial == item) {
						return
					}

					if (isStepOverLapLeft()) {
						eToast(
							String.format(
								findString(R.string.stepOverLapLeft),
								mViewModel.mStep.get()!!.name,
								mViewModel.mStep.get()!!.maxChoice
							)
						)
						return
					}
					increaseLeftMaterial(item, progressView)
				}

				else -> eToast("step invalid")
			}
			changeOtherMaterials(isDisable = true, position = "left")
		}

		override fun <T> onDeleteClick(position: Int, view: View, item: T) {
			item as Material
			view as ViewGroup
			val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
			progressView.progress = 0f
			mViewModel.mStep.get()!!.leftQty -= item.qty
			when (item.qty) {
				1 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory / 2))
				}
				2 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory2 / 2))
				}
				3 -> {
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory3 / 2))
				}
			}
			item.qty = 0
			changeOtherMaterials(isDisable = false, position = "left")

			//remove material plate image
			binding.frameMaterial.findViewWithTag<FrameLayout>(mViewModel.mStep.get())
				?.let { frameLayout ->
					frameLayout.findViewWithTag<AppCompatImageView>("${item.id}-${item.name}-left")?.setImageResource(0)
				}

		}
	}

	private fun increaseMaterial(item: Material, progressView: ProgressView) {
		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory)
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory2)
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - item.calory3)
			}
		}
		item.qty++
		mViewModel.mStep.get()!!.qty++
		when (item.type) {
			1 -> {
				progressView.progress = 1f
			}
			2 -> {
				progressView.progress = item.qty.toFloat() / 2
			}
			3 -> {
				if (item.qty == 3) {
					progressView.progress = 1f
				} else {
					progressView.progress = item.qty.toFloat() / 3
				}
			}
		}

		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + item.calory)
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + item.calory2)
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + item.calory3)
			}
		}

		//load plate image
		setImage(item)
	}

	private fun increaseRightMaterial(item: Material, progressView: ProgressView) {
		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory / 2))
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory2 / 2))
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory3 / 2))
			}
		}
		item.qty++
		mViewModel.mStep.get()!!.rightQty++
		when (item.type) {
			1 -> {
				progressView.progress = 1f
			}
			2 -> {
				progressView.progress = item.qty.toFloat() / 2
			}
			3 -> {
				if (item.qty == 3) {
					progressView.progress = 1f
				} else {
					progressView.progress = item.qty.toFloat() / 3
				}
			}
		}

		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory / 2))
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory2 / 2))
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory3 / 2))
			}
		}

		//load plate image
		setImage(item, "right")
	}

	private fun increaseLeftMaterial(item: Material, progressView: ProgressView) {
		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory / 2))
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory2 / 2))
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! - (item.calory3 / 2))
			}
		}
		item.qty++
		mViewModel.mStep.get()!!.leftQty++
		when (item.type) {
			1 -> {
				progressView.progress = 1f
			}
			2 -> {
				progressView.progress = item.qty.toFloat() / 2
			}
			3 -> {
				if (item.qty == 3) {
					progressView.progress = 1f
				} else {
					progressView.progress = item.qty.toFloat() / 3
				}
			}
		}

		when (item.qty) {
			1 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory / 2))
			}
			2 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory2 / 2))
			}
			3 -> {
				mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (item.calory3 / 2))
			}
		}

		//load plate image
		setImage(item, "left")
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter =
				MakerStepsAdapter(childFragmentManager, mViewModel.mBread.get()!!.steps, mViewModel.isHalf)
			binding.vpSteps.offscreenPageLimit = mViewModel.mBread.get()!!.steps.size
		}
		binding.vpSteps.adapter = mViewModel.mAdapter
		binding.vpSteps.measure(
			ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
			ConstraintLayout.LayoutParams.WRAP_CONTENT
		)
	}

	private fun setFrames() {
		//Add bread
		val breadImage = AppCompatImageView(requireContext())
		breadImage.layoutParams = FrameLayout.LayoutParams(
			FrameLayout.LayoutParams.MATCH_PARENT,
			FrameLayout.LayoutParams.MATCH_PARENT
		)
//		breadImage.setPadding(resources.getDimension(R.dimen._2mdp).toInt())
		binding.frameMaterial.addView(breadImage)
		imageLoader.load(
			mViewModel.mBread.get()!!.plate.getImageUri("2x", true),
			breadImage, false
		)
		//Bread calory
		mViewModel.mCalory.set(mViewModel.mBread.get()!!.calory)

		mViewModel.mBread.get()!!.steps.forEach { step ->
			val frameLayout = FrameLayout(requireContext())
			val params = FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT
			)
			params.gravity = Gravity.CENTER or Gravity.CENTER_VERTICAL or Gravity.CENTER_HORIZONTAL
			frameLayout.layoutParams = params
			frameLayout.tag = step
			binding.frameMaterial.addView(frameLayout)

			step.materials.forEach { material ->
				val materialImage = AppCompatImageView(requireContext())
				materialImage.layoutParams = FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.MATCH_PARENT
				)
//				materialImage.setPadding(resources.getDimension(R.dimen._2mdp).toInt())
				materialImage.tag = "${material.id}-${material.name}-center"
				frameLayout.addView(materialImage)
				if (material.qty > 0) {
					var image = ""
					var calory = 0
					when (material.qty) {
						1 -> {
							image = material.plate
							calory = material.calory
						}
						2 -> {
							image = material.plate2
							calory = material.calory2
						}
						3 -> {
							image = material.plate3
							calory = material.calory3
						}
					}
					//load material plate image
					imageLoader.load(
						image.getImageUri("2x", true),
						materialImage, false
					)
					//Material calory
					mViewModel.mCalory.set(mViewModel.mCalory.get()!! + calory)
				}
			}

			if (step.half == 1 && mViewModel.isHalf) {
				step.rightMaterials.forEach { rightMaterial ->
					val materialImage = AppCompatImageView(requireContext())
					materialImage.layoutParams = FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT
					)
//					materialImage.setPadding(resources.getDimension(R.dimen._2mdp).toInt())
					materialImage.tag = "${rightMaterial.id}-${rightMaterial.name}-right"
					frameLayout.addView(materialImage)
					if (rightMaterial.qty > 0) {
						var image = ""
						var calory = 0
						when (rightMaterial.qty) {
							1 -> {
								image = rightMaterial.plate
								calory = rightMaterial.calory
							}
							2 -> {
								image = rightMaterial.plate2
								calory = rightMaterial.calory2
							}
							3 -> {
								image = rightMaterial.plate3
								calory = rightMaterial.calory3
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
						//Material calory
						mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (calory / 2))
					}
				}

				step.leftMaterials.forEach { leftMaterial ->
					val materialImage = AppCompatImageView(requireContext())
					materialImage.layoutParams = FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT
					)
					materialImage.tag = "${leftMaterial.id}-${leftMaterial.name}-left"
					frameLayout.addView(materialImage)
					if (leftMaterial.qty > 0) {
						var image = ""
						var calory = 0
						when (leftMaterial.qty) {
							1 -> {
								image = leftMaterial.plate
								calory = leftMaterial.calory
							}
							2 -> {
								image = leftMaterial.plate2
								calory = leftMaterial.calory2
							}
							3 -> {
								image = leftMaterial.plate3
								calory = leftMaterial.calory3
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
						//Material calory
						mViewModel.mCalory.set(mViewModel.mCalory.get()!! + (calory / 2))
					}
				}
			}
		}
	}

	private fun getCurrentSelectedMaterial(position: String = "center"): Material? {
		when (position) {
			"left" -> return mViewModel.mStep.get()!!.leftMaterials.find {
				it.qty > 0
			}

			"right" -> return mViewModel.mStep.get()!!.leftMaterials.find {
				it.qty > 0
			}

			else -> return mViewModel.mStep.get()!!.materials.find {
				it.qty > 0
			}
		}
	}

	private fun isStepOverLap(): Boolean {
		return mViewModel.mStep.get()!!.qty >= mViewModel.mStep.get()!!.maxChoice
	}

	private fun isStepOverLapRight(): Boolean {
		return mViewModel.mStep.get()!!.rightQty >= mViewModel.mStep.get()!!.maxChoice
	}

	private fun isStepOverLapLeft(): Boolean {
		return mViewModel.mStep.get()!!.leftQty >= mViewModel.mStep.get()!!.maxChoice
	}

	private fun changeOtherMaterials(isDisable: Boolean, position: String = "center") {
		if (mViewModel.mStep.get()?.half == 1) {
			when (mViewModel.mStep.get()?.isSingle) {
				0 -> {
					when (position) {
						"center" -> {
							if (mViewModel.mStep.get()!!.qty >= mViewModel.mStep.get()!!.maxChoice) {
								changeMaterialAlpha(isDisable = isDisable, position = position)
							} else {
								changeMaterialAlpha(isDisable = isDisable, isReset = true, position = position)
							}
						}

						"left" -> {
							if (mViewModel.mStep.get()!!.leftQty >= mViewModel.mStep.get()!!.maxChoice) {
								changeMaterialAlpha(isDisable = isDisable, position = position)
							} else {
								changeMaterialAlpha(isDisable = isDisable, isReset = true, position = position)
							}
						}

						"right" -> {
							if (mViewModel.mStep.get()!!.rightQty >= mViewModel.mStep.get()!!.maxChoice) {
								changeMaterialAlpha(isDisable = isDisable, position = position)
							} else {
								changeMaterialAlpha(isDisable = isDisable, isReset = true, position = position)
							}
						}
					}

				}

				1 -> changeMaterialAlpha(isDisable = isDisable, position = position)
			}
		} else {
			when (mViewModel.mStep.get()?.isSingle) {
				0 -> {
					if (mViewModel.mStep.get()!!.qty >= mViewModel.mStep.get()!!.maxChoice) {
						changeMaterialAlpha(isDisable)
					} else {
						changeMaterialAlpha(isDisable, true)
					}
				}

				1 -> changeMaterialAlpha(isDisable)
			}
		}
	}

	private fun changeMaterialAlpha(isDisable: Boolean, isReset: Boolean = false, position: String = "center") {
		if (isReset) {
			when (position) {
				"center" -> mViewModel.mStep.get()!!.materials.forEach { material ->
					material.isAlpha = false
				}

				"left" -> mViewModel.mStep.get()!!.leftMaterials.forEach { material ->
					material.isAlpha = false
				}

				"right" -> mViewModel.mStep.get()!!.rightMaterials.forEach { material ->
					material.isAlpha = false
				}
			}

		} else {
			when (position) {
				"center" -> mViewModel.mStep.get()!!.materials.forEach { material ->
					material.isAlpha = material.qty == 0 && isDisable
				}

				"left" -> mViewModel.mStep.get()!!.leftMaterials.forEach { material ->
					material.isAlpha = material.qty == 0 && isDisable
				}

				"right" -> mViewModel.mStep.get()!!.rightMaterials.forEach { material ->
					material.isAlpha = material.qty == 0 && isDisable
				}
			}

		}
	}

	private fun setImage(material: Material, position: String = "center") {
		//remove material plate image
		binding.frameMaterial.findViewWithTag<FrameLayout>(mViewModel.mStep.get())
			?.let { frameLayout ->
				frameLayout.findViewWithTag<AppCompatImageView>("${material.id}-${material.name}-$position")
					?.let { appCompatImageView ->
						//				frameLayout.removeView(appCompatImageView)
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
						when (position) {
							"center" -> {
								imageLoader.loadWithCallBack(
									image.getImageUri("2x", true),
									appCompatImageView,
									false,
									object : Callback {
										override fun onSuccess() {
											YoYo.with(Techniques.ZoomIn)
												.duration(300)
												.repeat(0)
												.playOn(appCompatImageView)
										}

										override fun onError(e: Exception?) {
										}
									}
								)
							}

							"right" -> {
								val makerTransformation = HalfImageTransformation(position)
								imageLoader.loadWithTransform(
									uri = image.getImageUri("2x", true),
									imageView = appCompatImageView,
									transformation = makerTransformation,
									isCenterCrop = false,
								)
							}

							"left" -> {
								val makerTransformation = HalfImageTransformation(position)
								imageLoader.loadWithTransform(
									uri = image.getImageUri("2x", true),
									imageView = appCompatImageView,
									transformation = makerTransformation,
									isCenterCrop = false,
								)
							}
						}
					}
			}
	}
}