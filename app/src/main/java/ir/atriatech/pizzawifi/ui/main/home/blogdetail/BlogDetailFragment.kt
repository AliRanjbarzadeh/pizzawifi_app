package ir.atriatech.pizzawifi.ui.main.home.blogdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.getImageUri
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentBlogDetailBinding
import ir.atriatech.pizzawifi.entities.home.Blog


class BlogDetailFragment : BaseFragment(), RecyclerViewTools {

    lateinit var binding: FragmentBlogDetailBinding
    private lateinit var mViewModel: BlogDetailFragmentViewModel

    init {
        component.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(BlogDetailFragmentViewModel::class.java)
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
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_blog_detail, container, false)
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.description.settings.builtInZoomControls = false
        binding.description.settings.displayZoomControls = false
        binding.description.settings.setSupportZoom(false)
        binding.description.isHapticFeedbackEnabled = false
        binding.description.settings.javaScriptEnabled = false
        binding.description.settings.allowFileAccess = false
        binding.description.webChromeClient = WebChromeClient()
        binding.description.webViewClient = WebViewClient()
        binding.description.setOnLongClickListener { v -> true }
        binding.description.isVerticalScrollBarEnabled = false
        binding.description.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            binding.description.isNestedScrollingEnabled = false
//        }

        if (mViewModel.getData()) {
            setAdapter()
        }
    }

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = BlogSliderAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mListItems)
        }
        d(mViewModel.mItem, "blogdetail")

        binding.vpSlider.adapter = mViewModel.mAdapter
        binding.wormDots.setViewPager2(binding.vpSlider)

        imageLoader.load(
            mViewModel.mItem.icon.getImageUri("4x"),
            binding.imgIcon,
            isCenterCrop = false
        )

        val descContent = "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0,user-scalable=no'/>" +
                "<style>" +
                "@font-face {font-family: iranyekan;font-style: normal;font-weight: normal;src: url('" + findString(
            R.string.app_font
        ) + "');}" +
                "html,body{font-family: iranyekan !important; direction: rtl; text-align: justify; color: #BCBCBC;}" +
                "body{overflow-x:hidden !important; color: #BCBCBC;" +
                "width: 100%;margin:0px; box-sizing:border-box; " +
                "}" +
                ".content{width:100%;box-sizing:border-box; line-height:28px;text-align:justify; color: #BCBCBC;" +
                "max-height: 100%;" +
                "overflow-y: scroll;" +
                "}" +
                "img {max-width: 100% !important; display: block; height: auto !important; object-fit: content;}" +
                "</style>" +
                "</head><body>" +
                "<div class='content'>" +
                mViewModel.mItem.description +
                "</div>" +
                "</body></html>"

        binding.description.loadDataWithBaseURL(
            "file:///android_asset/",
            descContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Blog> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, type = 2)
            }

            override fun onSuccess(data: Blog) {
                mViewModel.mItem = data
                mViewModel.mListItems = data.slides
                setAdapter()
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.getData()
            }
        }, 2)
    }
}