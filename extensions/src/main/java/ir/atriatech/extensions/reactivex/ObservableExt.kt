package ir.atriatech.extensions.reactivex

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import ir.atriatech.core.entities.Outcome

fun<T> Observable<T>.performOnMainDoInBack(): Observable<T> {
    return subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
}
fun<T> Observable<T>.performOnMainDoInMain(): Observable<T> {
    return subscribeOn(AndroidSchedulers.mainThread())
        .observeOn(AndroidSchedulers.mainThread())
}
fun<T> PublishSubject<T>.outCome(): PublishSubject<Outcome<T>> {
  return PublishSubject.create<Outcome<T>>()
}