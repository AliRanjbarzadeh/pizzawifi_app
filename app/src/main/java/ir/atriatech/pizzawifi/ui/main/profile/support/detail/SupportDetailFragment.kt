package ir.atriatech.pizzawifi.ui.main.profile.support.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.deleteFromSp
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.common.NOTIFICATION_ADDITIONAL
import ir.atriatech.pizzawifi.common.NOTIFICATION_KEY
import ir.atriatech.pizzawifi.databinding.FragmentSupportDetailBinding
import ir.atriatech.pizzawifi.entities.profile.support.Support
import kotlinx.android.synthetic.main.fragment_support_detail.*


class SupportDetailFragment : BaseFragment() {
	lateinit var binding: FragmentSupportDetailBinding
	private lateinit var mViewModel: SupportDetailFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(SupportDetailFragmentViewModel::class.java)

		try {
			arguments?.getParcelable<Support>(ARG_STRING_1)?.let { mViewModel.mItem = it }
		} catch (e: Exception) {
		}
		mObserver()
		mObserverReply()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding =
			DataBindingUtil.inflate(inflater, R.layout.fragment_support_detail, container, false)

		binding.lifecycleOwner = this
		binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
		binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
		binding.viewModel = mViewModel
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		if (mViewModel.getData()) {
			setAdapter()
		}
	}

	override fun handleNotification() {
		val key = loadFromSp<String>(NOTIFICATION_KEY, "")
		val additional = loadFromSp<String>(NOTIFICATION_ADDITIONAL, "")
		when (key) {
			"support" -> {
				try {
					val msg = gson.fromJson<Msg>(additional, Msg::class.java)
					if (msg.id == mViewModel.mItem.id) {
						mViewModel.mListItems.clear()
						mViewModel.mAdapter?.list?.clear()
						mViewModel.getData()
						deleteFromSp(NOTIFICATION_KEY)
						deleteFromSp(NOTIFICATION_ADDITIONAL)
					} else {
						removeUntil(R.id.supportFragment)
					}
				} catch (ex: Exception) {
				}

			}

			"orders" -> {
			}

			"" -> {
			}

			else -> removeUntil(R.id.profileFragment)
		}
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = SupportDetailAdapter()
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (mViewModel.mAdapter!!.list.isEmpty()) {
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		try {
			binding.rvSupportDetail.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		binding.rvSupportDetail.setHasFixedSize(true)
		binding.rvSupportDetail.layoutManager = LinearLayoutManager(requireContext())
		binding.rvSupportDetail.addItemDecoration(
			MarginItemDecoration(
				dp2px(12),
				isShowOnFirstItem = true,
				marginPosition = 2
			)
		)
		binding.rvSupportDetail.adapter = mViewModel.mAdapter

		binding.rvSupportDetail.apply {
			scrollToPosition(mViewModel.mListItems.size - 1)
		}
	}

	private fun mObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Support>> {
				override fun onProgress(loading: Boolean) {
					mViewModel.isShowContent.set(false)
					if (loading) {
						binding.rvSupportDetail.showShimmerAdapter()
					} else {
						binding.rvSupportDetail.hideShimmerAdapter()
					}
				}

				override fun onSuccess(data: MutableList<Support>) {
					mViewModel.mListItems = data
					mViewModel.isShowContent.set(true)
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData()
				}
			},
			4
		)
	}

	private fun mObserverReply() {
		baseObserver(this, mViewModel.mObserverReply, object : RequestConnectionResult<Support> {
			override fun onProgress(loading: Boolean) {
				setProgressView(mainView, loading)
			}

			override fun onSuccess(data: Support) {
				mViewModel.mListItems.add(data)
				mViewModel.mAdapter?.list?.add(data)
				mViewModel.mAdapter?.notifyItemInserted(mViewModel.mListItems.size - 1)

				binding.rvSupportDetail.smoothScrollToPosition(mViewModel.mListItems.size - 1)
				binding.etMessage.setText(null)
				mViewModel.mItem.status = data.status
				mViewModel.mItem.statusText = data.statusText
				mViewModel.sendNotice()
			}

			override fun onFailed(errorMessage: String) {
//                binding.btnSend.revertAnimation()
			}
		})
	}
}