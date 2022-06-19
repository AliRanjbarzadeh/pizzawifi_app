package ir.atriatech.pizzawifi.ui.main.orders.survey

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
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.android.sToast
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentSurveyBinding
import ir.atriatech.pizzawifi.entities.orders.Order
import ir.atriatech.pizzawifi.entities.orders.survey.Question


class SurveyFragment : BaseFragment(), RecyclerViewTools {
	lateinit var binding: FragmentSurveyBinding
	private lateinit var mViewModel: SurveyFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(SurveyFragmentViewModel::class.java)
		try {
			arguments?.getParcelable<Order>(ARG_STRING_1)?.let { mViewModel.mItem = it }
		} catch (e: Exception) {
		}
		mObserver()
		mAddObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_survey, container, false)

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

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = SurveyAdapter(this)
			mViewModel.mListItems.add(Question())
			mViewModel.mAdapter!!.list.addAll(mViewModel.mListItems)
		}

		try {
			binding.rvSurvey.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		binding.rvSurvey.setHasFixedSize(true)
		binding.rvSurvey.layoutManager = LinearLayoutManager(requireContext())
		binding.rvSurvey.addItemDecoration(MarginItemDecoration(dp2px(8), MarginItemDecoration.TOP))
		binding.rvSurvey.adapter = mViewModel.mAdapter
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		for (mListItem in mViewModel.mListItems) {
			if (mListItem.rating < 1) {
				eToast("امتیاز سوالات نمی تواند کمتر از یک باشد")
				return
			}
		}
		mViewModel.add()
	}

	private fun mObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<Question>> {
				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainView, loading, 2)
				}

				override fun onSuccess(data: MutableList<Question>) {
					mViewModel.mListItems = data
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData()
				}
			},
			4
		)
	}

	private fun mAddObserver() {
		baseObserver(this, mViewModel.mAddObserver, object : RequestConnectionResult<Msg> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, 1)
			}

			override fun onSuccess(data: Msg) {
				sToast(data.msg)
				mViewModel.mItem.survey = 1
				back()
			}
		}, 1)
	}
}