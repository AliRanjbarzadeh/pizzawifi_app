package ir.atriatech.core

import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.Disposable

class BaseMutableLiveData<T> : MutableLiveData<T>() {
	private var _disposable: Disposable? = null

	var disposable: Disposable?
		get() = _disposable
		set(value) {
			_disposable = value
		}
}