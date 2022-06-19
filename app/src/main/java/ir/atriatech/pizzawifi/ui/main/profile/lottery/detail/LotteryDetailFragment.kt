package ir.atriatech.pizzawifi.ui.main.profile.lottery.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.extensions.android.loadFromSp
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.NOTIFICATION_KEY
import ir.atriatech.pizzawifi.databinding.FragmentLotteryDetailBinding
import ir.atriatech.pizzawifi.entities.profile.lottery.LotteryModel


class LotteryDetailFragment : BaseFragment() {
    lateinit var binding: FragmentLotteryDetailBinding
    private lateinit var mViewModel: LotteryDetailFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(LotteryDetailFragmentViewModel::class.java)

        try {
            arguments?.getParcelable<LotteryModel>(ARG_STRING_1)?.let { mViewModel.mItem = it }
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lottery_detail, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun handleNotification() {
        val key = loadFromSp<String>(NOTIFICATION_KEY, "")
        when (key) {
            "message" -> {
                removeUntil(R.id.messagesFragment)
            }

            "orders" -> {
            }

            "" -> {
            }

            else -> removeUntil(R.id.profileFragment)
        }
    }
}