package ir.atriatech.pizzawifi.ui.main.home.tournament

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.afollestad.materialdialogs.MaterialDialog
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.base.TimerObserver
import ir.atriatech.core.entities.Msg
import ir.atriatech.extensions.android.back
import ir.atriatech.extensions.android.sToast
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.base.startTimer
import ir.atriatech.extensions.base.stopTimer
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.common.IS_COMPETITION_DONE
import ir.atriatech.pizzawifi.databinding.FragmentTournamentBinding
import ir.atriatech.pizzawifi.entities.home.tournament.Tournament


class TournamentFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    lateinit var binding: FragmentTournamentBinding
    private lateinit var mViewModel: TournamentFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(TournamentFragmentViewModel::class.java)
        mObserver()
        mObserverSave()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tournament, container, false)

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_close_white_24dp)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseFragmentCallback?.changeMenuLock(true)
        mViewModel.getData()
        binding.vpQuestions.addOnPageChangeListener(this)

        binding.btnNext.setOnClickListener {
            if (binding.vpQuestions.currentItem < mViewModel.mItem.get()!!.questions.size - 1) {
                binding.vpQuestions.currentItem++
            } else if (binding.vpQuestions.currentItem == mViewModel.mItem.get()!!.questions.size - 1) {
                mViewModel.saveData()
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        startQuestionTimer(position)
        if (position == mViewModel.mItem.get()!!.questions.size - 1) {
            binding.btnNext.setText(R.string.finishCompetition)
        }
    }

    override fun onDestroyView() {
        baseFragmentCallback?.changeMenuLock(false)
        super.onDestroyView()
    }

    override fun sureOnBack() {
        if (mDialog == null || mDialog?.isShowing == false) {
            mDialog = MaterialDialog(requireContext())
                .apply {
                    title(R.string.exitCompetitionTitle)
                    message(R.string.exitCompetitionDescription)
                    positiveButton(R.string._yes)
                        .positiveButton {
                            baseFragmentCallback?.unRegisterFragmentListener(this@TournamentFragment.javaClass.simpleName)
                            IS_COMPETITION_DONE = true
                            back()
                        }
                    negativeButton(R.string._no)
                    show()
                }
        }
    }

    private fun setAdapter() {
        if (mViewModel.mAdapter == null) {
            mViewModel.mAdapter =
                TournamentPagerAdapter(childFragmentManager, mViewModel.mItem.get()!!.questions)
            binding.vpQuestions.offscreenPageLimit = mViewModel.mItem.get()!!.questions.size
        }
        binding.vpQuestions.adapter = mViewModel.mAdapter
        startQuestionTimer(0)
    }

    private fun startQuestionTimer(position: Int) {
        mViewModel.stopTimer()
        val remainTime = mViewModel.mItem.get()!!.questions[position].answerTime
        binding.questionTimer.max = remainTime.toFloat()
        binding.questionTimer.progress = 0f

        mViewModel.startTimer(object : TimerObserver {
            override fun onEmit(time: String) {
                binding.txtTime.setText(time)
            }

            override fun onTick(time: Long) {
                binding.questionTimer.progress = (remainTime - time).toFloat()
            }

            override fun onComplete() {
                binding.btnNext.callOnClick()
            }
        }, remainTime)
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Tournament> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, 2)
            }

            override fun onSuccess(data: Tournament) {
                mViewModel.mItem.set(data)
                mViewModel.isLoaded = true
                setAdapter()
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.getData()
            }
        }, 4)
    }

    private fun mObserverSave() {
        baseObserver(this, mViewModel.mObserverSave, object : RequestConnectionResult<Msg> {
            override fun onProgress(loading: Boolean) {
                setProgressView(binding.mainView, loading, 1)
            }

            override fun onSuccess(data: Msg) {
                sToast(data.msg)
                baseFragmentCallback?.unRegisterFragmentListener(this@TournamentFragment.javaClass.simpleName)
                IS_COMPETITION_DONE = true
                back()
            }

            override fun onFailed(errorMessage: String) {
                mViewModel.saveData()
            }
        }, 4)
    }
}