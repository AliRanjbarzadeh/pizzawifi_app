package ir.atriatech.pizzawifi.ui.main.home.blogvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.startAction
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentBlogVideoBinding
import ir.atriatech.pizzawifi.entities.home.Blog
import ir.atriatech.pizzawifi.entities.home.BlogVideo


class BlogVideoFragment : BaseFragment(), RecyclerViewTools {

	lateinit var binding: FragmentBlogVideoBinding
	private lateinit var mViewModel: BlogVideoFragmentViewModel

	init {
		component.inject(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mViewModel = ViewModelProvider(this).get(BlogVideoFragmentViewModel::class.java)
		try {
			arguments?.getParcelable<Blog>(ARG_STRING_1)?.let {
				mViewModel.mItem = it
			}
		} catch (e: Exception) {
		}
		mObserver()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blog_video, container, false)
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

	override fun <T> onItemClick(position: Int, view: View, item: T) {
		val blogVideo = item as BlogVideo
		startAction(blogVideo.link)
	}

	private fun setAdapter() {
		if (mViewModel.mAdapter == null) {
			mViewModel.mAdapter = BlogVideoAdapter(this)
			mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
		}

		binding.rvVideo.layoutManager =
			GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
		binding.rvVideo.adapter = mViewModel.mAdapter
		binding.rvVideo.setHasFixedSize(true)
	}

	private fun mObserver() {
		baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Blog> {
			override fun onProgress(loading: Boolean) {
				setProgressView(binding.mainView, loading, type = 2)
			}

			override fun onSuccess(data: Blog) {
				mViewModel.mItem = data
				mViewModel.mListItems = data.videos
				setAdapter()
			}

			override fun onFailed(errorMessage: String) {
				mViewModel.getData()
			}
		}, 2)
	}
}