package ir.atriatech.pizzawifi.ui.main.profile.competition.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.removeUntil
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentCompetitionDetailBinding
import ir.atriatech.pizzawifi.entities.profile.competition.Competition


class CompetitionDetailFragment : BaseFragment() {
    lateinit var binding: FragmentCompetitionDetailBinding
    private lateinit var mViewModel: CompetitionDetailFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(CompetitionDetailFragmentViewModel::class.java)

        try {
            mViewModel.item = arguments?.getParcelable(ARG_STRING_1)
        } catch (e: Exception) {
        }
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_competition_detail,
            container,
            false
        )

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
        removeUntil(R.id.profileFragment)
    }


    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = CompetitionQuestionAdapter(mViewModel.item!!)
            for (question in mViewModel.item!!.questions) {
                for (competitionAnswer in question.competitionAnswers) {
                    if (competitionAnswer.id == question.userSelect) {
                        competitionAnswer.isUserAnswer = true
                    }
                }
            }
            mViewModel.mAdapter!!.addToList(mViewModel.item!!.questions)
        }

        try {
            binding.rvCompetitionDetail.removeItemDecorationAt(0)
        } catch (e: Exception) {
        }

        binding.rvCompetitionDetail.setHasFixedSize(true)
        binding.rvCompetitionDetail.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCompetitionDetail.addItemDecoration(
            MarginItemDecoration(
                dp2px(8),
                marginPosition = MarginItemDecoration.TOP
            )
        )
        binding.rvCompetitionDetail.adapter = mViewModel.mAdapter
        binding.rvCompetitionDetail.setItemViewCacheSize(mViewModel.mListItems.size)
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Competition> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, 2)
            }

            override fun onSuccess(data: Competition) {
                mViewModel.item = data
                setAdapter()
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.getData()
            }
        }, 4)
    }
}