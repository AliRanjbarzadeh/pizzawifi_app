package ir.atriatech.pizzawifi.ui.intro.step1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.extensions.android.finish
import ir.atriatech.extensions.android.goActivity
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentIntroStep1Binding
import ir.atriatech.pizzawifi.ui.login.LoginActivity
import kotlinx.android.synthetic.main.fragment_intro_step1.*

class IntroStep1Fragment : BaseFragment() {
    lateinit var binding: FragmentIntroStep1Binding
    private lateinit var mViewModel: IntroStep1FragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(IntroStep1FragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_intro_step1, container, false)
        binding.viewModel = mViewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnNext.setOnClickListener {
            goActivity<LoginActivity>()
            finish()
        }
    }
}