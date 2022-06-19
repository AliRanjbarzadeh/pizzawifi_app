package ir.atriatech.pizzawifi.ui.main.home.tournament.questions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ir.atriatech.core.ARG_STRING_1
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.dp2px
import ir.atriatech.extensions.android.extraArguments
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.base.MarginItemDecoration
import ir.atriatech.pizzawifi.databinding.FragmentTournamentQuestionBinding
import ir.atriatech.pizzawifi.entities.home.tournament.Answer
import ir.atriatech.pizzawifi.entities.home.tournament.Question


class TournamentQuestionFragment : BaseFragment(), RecyclerViewTools {
    lateinit var binding: FragmentTournamentQuestionBinding
    private lateinit var mViewModel: TournamentQuestionFragmentViewModel

    companion object {
        fun newInstance(question: Question): TournamentQuestionFragment {
            val tournamentQuestionFragment = TournamentQuestionFragment()
            tournamentQuestionFragment.arguments =
                extraArguments(argument1 = question, key1 = ARG_STRING_1)
            return tournamentQuestionFragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(TournamentQuestionFragmentViewModel::class.java)
        try {
            arguments?.getParcelable<Question>(ARG_STRING_1)?.let { mViewModel.mItem.set(it) }
        } catch (ex: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_tournament_question,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
    }

    override fun <T> onItemClick(position: Int, view: View, item: T) {
        item as Answer
        if (mViewModel.lastSelectedPosition > -1) {
            mViewModel.mItem.get()!!.answers[mViewModel.lastSelectedPosition].isSelected = false
        }
        mViewModel.lastSelectedPosition = position
        mViewModel.mItem.get()!!.answers[mViewModel.lastSelectedPosition].isSelected = true
    }

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter = TournamentAnswerAdapter(this)
            mViewModel.mAdapter!!.addToList(mViewModel.mItem.get()!!.answers.toMutableList())
        }
        binding.rvTournamentQuestion.setHasFixedSize(false)
        binding.rvTournamentQuestion.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTournamentQuestion.isNestedScrollingEnabled = false
        binding.rvTournamentQuestion.addItemDecoration(
            MarginItemDecoration(
                dp2px(5),
                MarginItemDecoration.TOP
            )
        )
        binding.rvTournamentQuestion.adapter = mViewModel.mAdapter
    }
}