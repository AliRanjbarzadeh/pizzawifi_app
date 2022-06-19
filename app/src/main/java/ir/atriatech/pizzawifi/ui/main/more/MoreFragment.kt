package ir.atriatech.pizzawifi.ui.main.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ir.atriatech.core.BuildConfig
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.spannableString
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentMoreBinding
import ir.atriatech.pizzawifi.entities.MyMenuItem
import ir.atriatech.pizzawifi.entities.ShareApp
import ir.atriatech.pizzawifi.ui.MyMenuAdapter
import ir.atriatech.pizzawifi.ui.main.MainActivity


class MoreFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentMoreBinding
	private lateinit var mViewModel: MoreFragmentViewModel
	private lateinit var mActivity: MainActivity


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mActivity = activity as MainActivity
		mViewModel = ViewModelProvider(this).get(MoreFragmentViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
		binding.viewModel = mViewModel

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setAdapter()

		val customerCopyRight = spannableString(
			firstString = "تمام حقوق مادی و معنوی برای ",
			secondString = "پیتزا وای فای",
			secondColor = R.color.colorPrimary,
			secondFont = findString(R.string.app_font_bold),
			thirdString = " محفوظ است"
		)
		binding.txtCustomerCopyRight.text = customerCopyRight

		val atriatechCopyRight = spannableString(
			firstString = "طراحی و اجرا در لابراتوار تخصصی ",
			secondString = "آتریاتک",
			secondFont = findString(R.string.app_font_bold)
		)
		binding.txtAtriatechCopyRight.text = atriatechCopyRight

		val versionText = spannableString(
			firstString = "نسخه نرم افزار ",
			secondString = BuildConfig.APP_VERSION,
			secondColor = R.color.colorPrimary
		)
		binding.txtVersion.text = versionText

		binding.linearBottom.setOnClickListener {
			startAction("https://atriatech.ir")
		}
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		when (position) {
			0 -> {//about
				navTo(R.id.aboutFragment)
			}
			1 -> {//contact
				navTo(R.id.contactUsFragment)
			}
			2 -> {//law
				navTo(R.id.lawFragment)
			}
			3 -> {//branches
				navTo(R.id.moreBranchesFragment)
			}
			4 -> {//area
				navTo(R.id.areaFragment)
			}
//			5 -> {//faq
//				navTo(R.id.faqFragment)
//			}
			5 -> {//share
				if (!mActivity.isOnPause) {
					val shareApp = loadFromSp<ShareApp>("share")
					shareText(shareBody = shareApp.content, shareTitle = "اشتراک گذاری بوسیله")
				}
			}
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = MyMenuAdapter(this, true)

			val titles = resources.getStringArray(R.array.moreItems)
			val icons = resources.obtainTypedArray(R.array.moreIcons)

			for (i in titles.indices) {
				mViewModel.mAdapter!!.list.add(
					MyMenuItem(
						title = titles[i],
						image = icons.getResourceId(i, -1)
					)
				)
			}
			icons.recycle()
		}

		binding.rvMore.setHasFixedSize(true)
		binding.rvMore.layoutManager = LinearLayoutManager(requireContext())
		binding.rvMore.addItemDecoration(
			MarginItemDecoration(
				mHeight = dp2px(2),
				marginPosition = 2,
				isShowOnFirstItem = true
			)
		)
		binding.rvMore.adapter = mViewModel.mAdapter
	}
}