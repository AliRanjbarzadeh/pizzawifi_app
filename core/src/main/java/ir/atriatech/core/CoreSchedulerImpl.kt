package ir.atriatech.core

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class CoreSchedulerImpl : CoreScheduler {
	override fun mainThread(): Scheduler {
		return AndroidSchedulers.mainThread()
	}

	override fun io(): Scheduler {
		return Schedulers.io()
	}
}