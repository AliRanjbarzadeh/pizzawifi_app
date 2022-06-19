package ir.atriatech.extensions.base

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import ir.atriatech.core.base.BaseCoreRepository
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.extensions.reactivex.*

fun <T> BaseCoreRepository.sendToRequest(subject: PublishSubject<Outcome<T>>, single: Single<T>, disposable: CompositeDisposable) {
	subject.loading(true)
	single.performOnBackOutOnMain()
		.subscribe(
			{ answer ->
				subject.success(answer)
			}
			, { error ->
				e(error, "requestNetwork")
				subject.failed(error)
			}
		).addTo(disposable)
}

fun <T> BaseCoreRepository.outCome(): PublishSubject<Outcome<T>> {
	return PublishSubject.create<Outcome<T>>()
}