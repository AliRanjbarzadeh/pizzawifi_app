package ir.atriatech.pizzawifi.ui.main.home.customer.detail.comment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentAddCommentBinding
import kotlinx.android.synthetic.main.fragment_add_comment.*


class AddCommentFragment : BaseFragment() {
    lateinit var binding: FragmentAddCommentBinding
    private lateinit var mViewModel: AddCommentFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(AddCommentFragmentViewModel::class.java)
        try {
            arguments?.getInt(ARG_STRING_1)?.let { mViewModel.id = it }
        } catch (e: Exception) {
        }
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_comment, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel

        hideKeyboard(binding.mainView)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageCard.setOnClickListener { pickImage(isAspectRatio = false, size = 1024) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickImageOnActivityResult(requestCode, data, object : PickImageCallback {
            override fun onSuccess(uri: Uri) {
                binding.userSelectedImage.setImageURI(uri)
                binding.fabImage.visibility = View.GONE
                mViewModel.uri = uri
            }

            override fun onFail(error: Exception) {
                eToast(findString(R.string.wrongImageValue))
            }
        })
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (!isOpen) {
            etComment.clearFocus()
        }
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, 1)
            }

            override fun onSuccess(data: Msg) {
                sToast(data.msg)
                back()
            }
        }, 1)
    }
}