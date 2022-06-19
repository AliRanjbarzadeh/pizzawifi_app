package ir.atriatech.extensions.base

import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import ir.atriatech.core.base.BaseCoreFragmentViewModel
import ir.atriatech.core.base.TimerObserver
import ir.atriatech.core.util.Rx2Timer
import java.util.concurrent.TimeUnit

fun BaseCoreFragmentViewModel.startTimer(timerObserver: TimerObserver, time: Long = 120) {
	timer = Rx2Timer.builder()
		.initialDelay(0) //default is 0
		.period(1) //default is 1
		.take(time) //default is 60
		.unit(TimeUnit.SECONDS) // default is TimeUnit.SECONDS
		.onEmit(Consumer {
			val min = it / 60
			val sec = it - min * 60
			var minString = min.toString()
			var secString = sec.toString()
			val timer: String
			if (min.toString().length == 1) {
				minString = "0$minString"
			}
			if (sec.toString().length == 1) {
				secString = "0$secString"
			}
			timer = "$minString:$secString"
			timerObserver.onEmit(timer)
			timerObserver.onTick(it)
		})
		.onError(Consumer {
			it.printStackTrace()
		})
		.onComplete(Action {
			timerObserver.onComplete()
		})
		.build()
	timer.start()
}


fun BaseCoreFragmentViewModel.stopTimer() {
	try {
		timer.stop()
	} catch (e: Exception) {
	}
}