package ir.atriatech.extensions.reactivex

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ir.atriatech.core.BaseMutableLiveData
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e


/**
 * Extension function to convert a Publish subject into a LiveData by subscribing to it.
 **/
fun <T> PublishSubject<T>.toLiveData(compositeDisposable: CompositeDisposable): BaseMutableLiveData<T> {
	val data = BaseMutableLiveData<T>()
	data.disposable = this.subscribe { t: T -> data.value = t }
	compositeDisposable.add(data.disposable!!)
	return data
}

fun <T> PublishSubject<T>.refreshLiveData(compositeDisposable: CompositeDisposable, data: BaseMutableLiveData<T>) {
	data.disposable = this.subscribe { t: T -> data.value = t }
	compositeDisposable.add(data.disposable!!)
}

/**
 * Extension function to push a failed event with an exception to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.failed(e: Throwable) {
	with(this) {
		loading(false)
		onNext(Outcome.failure(e))
//        onFailed(Outcome.onErrorHandler(e))

	}
}

//fun <T> PublishSubject<Outcome<T>>.serverError(serverError: HttpException) {
//    with(this) {
//        onNext(Outcome.serverError(serverError))
//        loading(false)
////        onFailed(Outcome.onErrorHandler(serverError as Throwable))
//
//    }
//}
fun <T> PublishSubject<Outcome<T>>.unauthorized(boolean: Boolean = false) {
	with(this) {

		onNext(Outcome.unauthorized(boolean))
		loading(false)
	}
}

/**
 * Extension function to push  a success event with data to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.success(t: T) {
	with(this) {
		try {
			loading(false)
		} catch (ex: Exception) {
			e(ex.message, "successd")

		}
	}
	with(this) {
		try {
			onNext(Outcome.success(t))
		} catch (ex: Exception) {
			e(ex.message, "successd")
		}

	}

}

/**
 * Extension function to push the loading status to the observing outcome
 * */
fun <T> PublishSubject<Outcome<T>>.loading(isLoading: Boolean) {
	this.onNext(Outcome.loading(isLoading))

}