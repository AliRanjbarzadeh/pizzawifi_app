package ir.atriatech.core

import io.reactivex.Scheduler

interface CoreScheduler {
	fun mainThread(): Scheduler
	fun io(): Scheduler
}