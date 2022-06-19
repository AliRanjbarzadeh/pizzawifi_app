package ir.atriatech.pizzawifi.ui.main.maker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.squareup.picasso.Callback
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.customlibs.ProgressView
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.extensions.android.navTo
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentMakerStep2Binding
import ir.atriatech.pizzawifi.entities.home.maker.Bread
import ir.atriatech.pizzawifi.entities.home.maker.Maker

class MakerStep2Fragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentMakerStep2Binding
	private lateinit var mViewModel: MakerStep2FragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MakerStep2FragmentViewModel::class.java)

		arguments?.getParcelable<Maker>(ARG_STRING_1)?.let {
			mViewModel.mItem.set(it)
		}

		arguments?.getBoolean(ARG_STRING_2, false)?.let { isHalf: Boolean ->
			mViewModel.isHalf = isHalf
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maker_step2, container, false)
		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.btnNext.setOnClickListener {
			val bread = getSelectedBread()
			if (bread == null) {
				eToast(findString(R.string.selectBread))
				return@setOnClickListener
			}
			if (bread.steps.isEmpty()) {
				eToast(findString(R.string.breadEmptySteps))
				return@setOnClickListener
			}
			val arg = extraArguments(argument1 = mViewModel.mItem.get()!!, key1 = ARG_STRING_1, argument2 = mViewModel.isHalf, key2 = ARG_STRING_2)
			navTo(
				R.id.makerStep3Fragment,
				arg = arg,
				enterAnimRes = R.anim.enter_from_right,
				exitAnimRes = R.anim.exit_to_left,
				popEnterAnimRes = R.anim.enter_from_left,
				popExistAnimRes = R.anim.exit_to_right
			)
		}

		binding.imgHelp.setOnClickListener {
			if (mDialog == null || mDialog?.isShowing == false) {
				mDialog = MaterialDialog(requireContext())
					.apply {
						title(text = mViewModel.mItem.get()!!.breadTitle)
						message(text = mViewModel.mItem.get()!!.breadHelp)
						positiveButton(R.string.close)
						show()
					}
			}
		}
		setAdapter()
	}

	override fun onDestroy() {
		mViewModel.mItem.get()!!.reset()
		super.onDestroy()
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		view as ViewGroup
		item as Bread
		if (item.entity == 0)
			return

		val selectedID = getSelectedBreadID()
		if (selectedID != 0 || selectedID == item.id) {
			return
		}
		val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
		progressView.progress = 1f
		item.isSelected = true
		mViewModel.mCalory.set(item.calory)
		changeOtherMaterials(true)
		imageLoader.loadWithCallBack(
			item.plate.getImageUri("2x", true),
			binding.imgPlateMaterial,
			false,
			object : Callback {
				override fun onSuccess() {
					YoYo.with(Techniques.ZoomIn)
						.duration(300)
						.repeat(0)
						.playOn(binding.imgPlateMaterial)
				}

				override fun onError(e: Exception?) {
				}
			}
		)
	}

	override fun <T> onDeleteClick(position: Int, view: View, item: T) {
		view as ViewGroup
		item as Bread

		val progressView = view.findViewById<ProgressView>(R.id.pvMaterial)
		progressView.progress = 0f
		item.reset()
		mViewModel.mCalory.set(0)
		binding.imgPlateMaterial.setImageResource(0)
		changeOtherMaterials(false)
//		binding.linearRulerFlag.findViewWithTag<View>(item.diameterSize).visibility = View.INVISIBLE
	}

	private fun getSelectedBreadID(): Int {
		var id = 0
		for (i in 0 until mViewModel.mItem.get()!!.breads.size) {
			if (mViewModel.mItem.get()!!.breads[i].isSelected) {
				id = mViewModel.mItem.get()!!.breads[i].id
				break
			}
		}
		return id
	}

	private fun getSelectedBread(): Bread? {
		var bread: Bread? = null
		for (i in 0 until mViewModel.mItem.get()!!.breads.size) {
			if (mViewModel.mItem.get()!!.breads[i].isSelected) {
				bread = mViewModel.mItem.get()!!.breads[i]
				break
			}
		}
		return bread
	}

	private fun changeOtherMaterials(isDisable: Boolean) {
		mViewModel.mItem.get()!!.breads.forEach { bread ->
			bread.isAlpha = !bread.isSelected && isDisable
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = BreadAdapter(this)
			mViewModel.mAdapter!!.addToList(mViewModel.mItem.get()!!.breads as MutableList<Bread>)
		}
		binding.rvMaterials.setHasFixedSize(true)
		binding.rvMaterials.layoutManager = GridLayoutManager(requireContext(), 2)
		binding.rvMaterials.adapter = mViewModel.mAdapter

		val diameters = mViewModel.mItem.get()!!.breads.map { it.diameterSize }.distinct().sorted()
		binding.clRuler.post {
			val itemHeight = binding.clRuler.measuredHeight / diameters.size
			val itemWidth = binding.linearRuler.measuredWidth / 4

			for (i in 0 until diameters.size) {
				val itemView = LayoutInflater.from(requireContext())
					.inflate(R.layout.item_diameter, binding.linearRuler, false)
				val params = LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT
				)
				val itemFalg = LayoutInflater.from(requireContext())
					.inflate(R.layout.item_diameter_flag, binding.linearRulerFlag, false)
				val paramsFlag =
					LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp2px(18))

				val lineView = itemView.findViewById<View>(R.id.vRulerLine)
				lineView.layoutParams.width = itemWidth

				val topMargin = if (i == 0) {
					(itemHeight / 2) - dp2px(18)
				} else {
					itemHeight - dp2px(18)
				}
				params.topMargin = topMargin
				paramsFlag.topMargin = topMargin

				val txtRuler = itemView.findViewById<AppCompatTextView>(R.id.txtRuler)
				txtRuler.text = "${diameters[i]} cm"

				itemView.layoutParams = params
				itemFalg.layoutParams = paramsFlag
				itemFalg.visibility = View.INVISIBLE
				itemFalg.tag = diameters[i]
				itemView.tag = diameters[i]
				binding.linearRuler.addView(itemView)
				binding.linearRulerFlag.addView(itemFalg)
			}
		}

		getSelectedBread()?.let { bread ->
			imageLoader.load(
				bread.plate.getImageUri("2x", true),
				binding.imgPlateMaterial, false
			)
			binding.linearRulerFlag.post {
				binding.linearRulerFlag.findViewWithTag<View>(bread.diameterSize).visibility =
					View.VISIBLE
			}
		}
	}
}