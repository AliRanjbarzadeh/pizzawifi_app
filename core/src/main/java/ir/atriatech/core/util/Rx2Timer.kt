package ir.atriatech.core.util

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class Rx2Timer private constructor(builder: Builder) {

	private val take: Long
	private val period: Long
	private val initialDelay: Long
	private val unit: TimeUnit
	private val onComplete: Action?
	private val onEmit: Consumer<Long>?
	private val onError: Consumer<Throwable>?

	private var pauseTake = 0L
	private var resumeTake = 0L
	/**
	 * is in pause state
	 */
	var isPause = false
		private set
	private var isStarted = false
	private var disposable: Disposable? = null

	init {
		take = builder.take
		period = builder.period
		initialDelay = builder.initialDelay
		unit = builder.unit
		onComplete = builder.onComplete
		onEmit = builder.onEmit
		onError = builder.onError
	}

	/**
	 * restart timer , all pause states are cleaned
	 */
	fun restart(): Rx2Timer {
		stop()
		return start()
	}

	/**
	 * start timer
	 */
	fun start(): Rx2Timer {
		if (isPause) {
			return restart()
		}
		if (disposable == null || disposable!!.isDisposed) {
			disposable = Observable.interval(initialDelay, period, unit)
				.subscribeOn(Schedulers.single())
				.take(take + 1)
				.map { aLong ->
					pauseTake = aLong
					take - aLong
				}
				.doOnSubscribe { isStarted = true }
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ aLong ->
					onEmit?.accept(aLong)
				}, { throwable ->
					onError?.accept(throwable)
				}, {
					onComplete?.run()
				})
		}
		return this
	}

	/**
	 * stop timer and all pause states are cleaned
	 */
	fun stop() {
		if (disposable != null) {
			disposable!!.dispose()
		}
		if (isPause) {
			cleanPauseState()
		}
	}

	/**
	 * pause timer
	 */
	fun pause() {
		if (isPause || !isStarted) {
			return
		}
		stop()
		isPause = true
		resumeTake += pauseTake
	}

	/**
	 * resume timer
	 */
	fun resume() {
		if (!isPause) {
			return
		}
		isPause = false
		if (disposable == null || disposable!!.isDisposed) {
			disposable = Observable.interval(initialDelay, period, unit)
				.subscribeOn(Schedulers.single())
				.take(take + 1 - resumeTake)
				.map { aLong ->
					pauseTake = aLong
					take - aLong - resumeTake
				}
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ aLong ->
					onEmit?.accept(aLong)
				}, { throwable ->
					cleanPauseState()
					onError?.accept(throwable)
				}, {
					cleanPauseState()
					onComplete?.run()
				})
		}
	}

	/**
	 * clean pause states
	 */
	private fun cleanPauseState() {
		isPause = false
		resumeTake = 0L
		pauseTake = 0L
		isStarted = false
	}

	class Builder internal constructor() {

		var take: Long = 60
		var period: Long = 1
		var initialDelay: Long = 0
		var unit = TimeUnit.SECONDS
		var onComplete: Action? = null
		var onEmit: Consumer<Long>? = null
		var onError: Consumer<Throwable>? = null

		/**
		 * counting number , default value is 60
		 *
		 * @param take take value
		 */
		fun take(take: Long): Builder {
			this.take = take
			return this
		}

		/**
		 * period, default value is 1
		 *
		 * @param period period value
		 */
		fun period(period: Int): Builder {
			this.period = period.toLong()
			return this
		}

		/**
		 * delay to begin , default value is 0
		 *
		 * @param initialDelay delay value
		 */
		fun initialDelay(initialDelay: Int): Builder {
			this.initialDelay = initialDelay.toLong()
			return this
		}

		/**
		 * time unit , default value is TimeUnit.SECONDS
		 *
		 * @param unit unit value
		 */
		fun unit(unit: TimeUnit): Builder {
			this.unit = unit
			return this
		}

		fun onComplete(onComplete: Action): Builder {
			this.onComplete = onComplete
			return this
		}

		fun onEmit(onEmit: Consumer<Long>): Builder {
			this.onEmit = onEmit
			return this
		}

		fun onError(onError: Consumer<Throwable>): Builder {
			this.onError = onError
			return this
		}

		fun build(): Rx2Timer {
			return Rx2Timer(this)
		}
	}

	companion object {

		fun builder(): Builder {
			return Builder()
		}
	}
}
