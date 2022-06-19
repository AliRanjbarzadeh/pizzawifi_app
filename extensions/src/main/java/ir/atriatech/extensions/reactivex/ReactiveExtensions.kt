package ir.atriatech.extensions.reactivex

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ir.atriatech.core.CoreScheduler


/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Completable]
 * */
fun Completable.performOnBackOutOnMain(coreScheduler: CoreScheduler): Completable {
	return this.subscribeOn(coreScheduler.io())
		.observeOn(coreScheduler.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Flowable]
 * */
fun <T> Flowable<T>.performOnBackOutOnMain(coreScheduler: CoreScheduler): Flowable<T> {
	return this.subscribeOn(coreScheduler.io())
		.observeOn(coreScheduler.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread  for a [Single]
 * */
fun <T> Single<T>.performOnBackOutOnMain(): Single<T> {
	return this.subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread())
}

/**
 * Extension function to subscribe on the background thread and observe on the main thread for a [Observable]
 * */
fun <T> Observable<T>.performOnBackOutOnMain(coreScheduler: CoreScheduler): Observable<T> {
	return this.subscribeOn(coreScheduler.io())
		.observeOn(coreScheduler.mainThread())
}

/**
 * Extension function to add a Disposable to a CompositeDisposable
 */
fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
	compositeDisposable.add(this)
}

/**
 * Extension function to subscribe on the background thread for a Flowable
 * */
fun <T> Flowable<T>.performOnBack(coreScheduler: CoreScheduler): Flowable<T> {
	return this.subscribeOn(coreScheduler.io())
}

/**
 * Extension function to subscribe on the background thread for a Completable
 * */
fun Completable.performOnBack(coreScheduler: CoreScheduler): Completable {
	return this.subscribeOn(coreScheduler.io())
}

/**
 * Extension function to subscribe on the background thread for a Observable
 * */
fun <T> Observable<T>.performOnBack(coreScheduler: CoreScheduler): Observable<T> {
	return this.subscribeOn(coreScheduler.io())
}