package ir.atriatech.pizzawifi.ui.main.home.bloggallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.ARG_STRING_2
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentBlogGalleryFullScreenBinding
import ir.atriatech.pizzawifi.entities.home.Blog


class BlogGalleryFullScreenFragment : BaseFragment(), RecyclerViewTools {

    lateinit var binding: FragmentBlogGalleryFullScreenBinding
    private lateinit var mViewModel: BlogGalleryFullScreenFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(BlogGalleryFullScreenFragmentViewModel::class.java)
        try {
            arguments?.getParcelable<Blog>(ARG_STRING_1)?.let {
                mViewModel.mItem = it
            }
            arguments?.getInt(ARG_STRING_2)?.let {
                mViewModel.currentPosition = it
            }
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_blog_gallery_full_screen,
                container,
                false
            )
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel

        baseFragmentCallback?.changeMenuLock(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    override fun onDestroy() {
        baseFragmentCallback?.changeMenuLock(false)
        super.onDestroy()
    }

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = BlogGalleryFullScreenAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mItem.slides)
        }

        binding.vpGallery.adapter = mViewModel.mAdapter
        binding.vpGallery.setCurrentItem(mViewModel.currentPosition, false)
    }
}