package ir.atriatech.core.base


import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.util.Rx2Timer
import java.util.*

open class BaseCoreFragmentViewModel : ViewModel() {
	var viewModelLoading = ObservableBoolean(false)
	var isShowContent = ObservableBoolean(false)
	var isEmptyView = ObservableBoolean(false)
	var angle = 25

	init {
		when (Locale.getDefault().language) {
			"fa" -> angle = -25
			"en" -> angle = 25
		}
	}

	val bag = CompositeDisposable()
	lateinit var timer: Rx2Timer

	override fun onCleared() {
		super.onCleared()
		if (::timer.isInitialized) {
			timer.stop()
		}
		bag.clear()
	}

}


interface TimerObserver {
	fun onEmit(time: String) {}
	fun onTick(time: Long) {}
	fun onComplete()
}

