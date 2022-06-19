package ir.atriatech.extensions.android

import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.BAD_REQUEST
import ir.atriatech.extensions.UNAUTHORIZED
import ir.atriatech.extensions.network.getDefaultErrorMessage
import ir.atriatech.extensions.network.getServerErrorMessage


interface LiveResult<T> {
	fun onProgress(loading: Boolean)
	fun onSuccess(data: T)
	fun onFailed(errorMessage: String)
	fun onFailed(error: Msg)
	fun onUnauthorized()
}

fun <T> LiveData<Outcome<T>>.observeOutCome(@NonNull owner: LifecycleOwner, liveResult: LiveResult<T>) {
	observe(owner, Observer { outcome ->
		when (outcome) {
			is Outcome.Progress -> {
				liveResult.onProgress(outcome.loading)
			}
			is Outcome.Failure -> {
				when (outcome.e) {
					is HttpException -> {
						when {
							(outcome.e as HttpException).code() == BAD_REQUEST -> {
								val error = (outcome.e as HttpException).getServerErrorMessage()
								when (error) {
									is Msg -> liveResult.onFailed(error)
									is String -> liveResult.onFailed(error)
									else -> liveResult.onFailed((outcome.e).getDefaultErrorMessage())
								}

							}
							(outcome.e as HttpException).code() == UNAUTHORIZED -> liveResult.onUnauthorized()
							else -> liveResult.onFailed((outcome.e).getDefaultErrorMessage())
						}

					}
					else -> liveResult.onFailed((outcome.e).getDefaultErrorMessage())
				}
				liveResult.onProgress(false)
			}
			is Outcome.Success -> {
				liveResult.onProgress(false)
				liveResult.onSuccess(outcome.data)

			}
			is Outcome.Unauthorized -> {
				liveResult.onProgress(false)
				liveResult.onUnauthorized()
			}
		}

	})
}