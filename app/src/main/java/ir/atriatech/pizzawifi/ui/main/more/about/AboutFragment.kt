package ir.atriatech.pizzawifi.ui.main.more.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentAboutBinding
import ir.atriatech.pizzawifi.entities.GeneralModel


class AboutFragment : BaseFragment() {
    lateinit var binding: FragmentAboutBinding
    private lateinit var mViewModel: AboutFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(AboutFragmentViewModel::class.java)
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mViewModel.getData()) {
            setData()
        }
    }

    private fun setData() {
        binding.wvData.settings.builtInZoomControls = false
        binding.wvData.settings.displayZoomControls = false
        binding.wvData.settings.setSupportZoom(false)
        binding.wvData.isHapticFeedbackEnabled = false
        binding.wvData.settings.javaScriptEnabled = false
        binding.wvData.settings.allowFileAccess = true
        binding.wvData.webChromeClient = WebChromeClient()
        binding.wvData.webViewClient = WebViewClient()
        binding.wvData.setOnLongClickListener { v -> true }
        binding.wvData.isVerticalScrollBarEnabled = false
        binding.wvData.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )

        val descContent = "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0,user-scalable=no'/>" +
                "<style>" +
                "@font-face {font-family: iranyekan;font-style: normal;font-weight: normal;src: url('" + findString(
            R.string.app_font
        ) + "');}" +
                "html,body{font-family: iranyekan !important; direction: rtl; text-align: justify; color: #000;}" +
                "body{overflow-x:hidden !important; color: #000;" +
                "width: 100%;margin:0px; box-sizing:border-box; " +
                "}" +
                ".content{width:100%;box-sizing:border-box; line-height:28px;text-align:justify; color: #000;" +
                "border-radius: 1em;" +
                "padding: 0.8em;" +
                "max-height: 100%;" +
                "overflow-y: scroll;" +
                "background-color: #FFF;" +
                "}" +
                "img {max-width: 100% !important; display: block; height: auto !important; object-fit: content;}" +
                "</style>" +
                "</head><body>" +
                "<div class='content'>" +
                mViewModel.mItem?.description +
                "</div>" +
                "</body></html>"

        binding.wvData.loadDataWithBaseURL(
            "file:///android_asset/",
            descContent,
            "text/html",
            "UTF-8",
            null
        )
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<GeneralModel> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, type = 2)
            }

            override fun onSuccess(data: GeneralModel) {
                mViewModel.mItem = data
                mViewModel.isShowContent.set(true)
                setData()
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.getData()
            }
        }, 4)
    }
}