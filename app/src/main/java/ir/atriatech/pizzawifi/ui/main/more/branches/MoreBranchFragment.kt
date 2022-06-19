package ir.atriatech.pizzawifi.ui.main.more.branches

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.Ext.ctx
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentMoreBranchBinding
import ir.atriatech.pizzawifi.entities.Branch
import ir.atriatech.pizzawifi.entities.CityModel

class MoreBranchFragment : BaseFragment(), OnRefreshListener, RecyclerViewTools {
	lateinit var binding: FragmentMoreBranchBinding
	private lateinit var mViewModel: MoreBranchFragmentViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(MoreBranchFragmentViewModel::class.java)
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more_branch, container, false)

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

		//Refresh listener
		binding.refreshLayout.setOnRefreshListener(this)
	}


	override fun onRefresh(refreshLayout: RefreshLayout) {
		mViewModel.isRefreshing = true
		mViewModel.isShowContent.set(false)
		mViewModel.mListItems.clear()
		mViewModel.mAdapter?.list?.clear()
		binding.rvCity.removeAllViews()
		mViewModel.getData()
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = MoreBranchCityAdapter(this)

			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		} else if (mViewModel.isRefreshing) {
			mViewModel.isRefreshing = false
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		try {
			binding.rvCity.removeItemDecorationAt(0)
		} catch (e: Exception) {
		}

		binding.rvCity.setHasFixedSize(true)
		binding.rvCity.layoutManager =
			LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
		binding.rvCity.addItemDecoration(MarginItemDecoration(dp2px(2), MarginItemDecoration.TOP))
		binding.rvCity.adapter = mViewModel.mAdapter
	}

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		val branch = item as Branch
		dialThisNumber(branch.phone)
	}

	@SuppressLint("QueryPermissionsNeeded")
	override fun <T> onItemSelect(position: Int, view: View, item: T) {
		try {
			val branch = item as Branch
			val gmmIntentUri = Uri.parse("geo:0,0?q=" + branch.lat + "," + branch.lng)
			val mapIntent = Intent(Intent.ACTION_VIEW)
			mapIntent.data = gmmIntentUri
			if (mapIntent.resolveActivity(ctx.packageManager) != null) {
				startActivity(mapIntent)
			}

		} catch (e: Exception) {
			eToast(getString(ir.atriatech.extensions.R.string.no_app_found_to_open))
		}
	}

	private fun mObserver() {
		baseObserver(
			this,
			mViewModel.mObserver,
			object : RequestConnectionResult<MutableList<CityModel>> {
				override fun onProgress(loading: Boolean) {
					setProgressView(binding.mainCoordinator, loading, type = 2)
				}

				override fun onSuccess(data: MutableList<CityModel>) {
					binding.refreshLayout.finishRefresh()
					mViewModel.mListItems = data
					mViewModel.isEmptyView.set(data.size == 0)
					setAdapter()
				}

				override fun onFailed(errorMessage: String) {
					mViewModel.getData()
				}
			}, 2
		)
	}
}