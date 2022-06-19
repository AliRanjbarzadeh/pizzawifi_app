package ir.atriatech.extensions.base.fragment

import android.view.View
import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.mikelau.views.shimmer.ShimmerRecyclerViewX
import ir.atriatech.core.base.BaseCoreFragment
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.dialog.Retry
import ir.atriatech.extensions.dialog.retry

fun <T> BaseCoreFragment.baseListObserver(
	@NonNull owner: LifecycleOwner, live: LiveData<Outcome<MutableList<T>>>,
	connectionResult: RequestConnectionResult<MutableList<T>>, type: Int = 1, view: View? = null
) {
	live.observeOutCome(owner, object : LiveResult<MutableList<T>> {
		override fun onProgress(loading: Boolean) {
			if (shouldHideKeyboard) {
				hideInputMethod()
			}
			isLoading = loading
			connectionResult.onProgress(loading)
		}

		override fun onSuccess(data: MutableList<T>) {
			isRefreshing = false
			if (data.size < limit)
				view?.invisiable()
			smartRefreshLayout?.let {
				it.finishRefresh()
				it.finishLoadMore()
			}
			if (recyclerView is ShimmerRecyclerViewX) {
				try {
					(recyclerView as ShimmerRecyclerViewX).hideShimmerAdapter()
				} catch (e: Exception) {
				}
			}
			connectionResult.onSuccess(data)
			anyList.add(data)
			emptyVP?.let {
				setEmptyView(it, anyList)
			}
		}

		override fun onFailed(errorMessage: String) {
			if (offset >= limit) {
				offset -= limit
			}
			when (type) {
				1 -> {
					eToast(errorMessage)
				}
				2 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage)
				}
				3 -> {
					if (view != null) {
						connectionResult.onFailed(errorMessage)
						view.visiable()
					}
				}
				4 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage, isFinishApp = false)
				}
				5 -> {
					eToast(errorMessage)
					connectionResult.onFailed(errorMessage)
				}
			}
		}

		override fun onFailed(error: Msg) {
			connectionResult.onFailed(error)
		}

		override fun onUnauthorized() {
			if (isForceLogin) {
				unAuthorizedIntent?.let {
					startActivity(it)
				}
				finish()
			} else {
				logOut(true)
			}
		}
	})
}

fun <T> BaseCoreFragment.baseObejectOfListObserver(
	@NonNull owner: LifecycleOwner, live: LiveData<Outcome<T>>,
	connectionResult: RequestConnectionResult<T>,
	type: Int = 1,
	view: View? = null
) {
	live.observeOutCome(owner, object : LiveResult<T> {
		override fun onProgress(loading: Boolean) {
			if (shouldHideKeyboard) {
				hideInputMethod()
			}
			isLoading = loading
			connectionResult.onProgress(loading)
		}

		@SuppressWarnings("Don't forget to set isAllLoaded")
		override fun onSuccess(data: T) {
			isRefreshing = false
//				if (data=size < limit) {
//					isAllLoaded = true
//				}
			view?.invisiable()
			connectionResult.onSuccess(data)
		}

		override fun onFailed(errorMessage: String) {
			if (offset >= limit) {
				offset -= limit
			}
			when (type) {
				1 -> {
					eToast(errorMessage)
				}
				2 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage)
				}
				3 -> {
					if (view != null) {
						connectionResult.onFailed(errorMessage)
						view.visiable()
					}
				}
				4 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage, isFinishApp = false)
				}
				5 -> {
					eToast(errorMessage)
					connectionResult.onFailed(errorMessage)
				}
			}
		}

		override fun onFailed(error: Msg) {
			connectionResult.onFailed(error)
		}

		override fun onUnauthorized() {
			if (isForceLogin) {
				unAuthorizedIntent?.let {
					startActivity(it)
				}
				finish()
			} else {
				logOut(true)
			}
		}
	})

}

fun <T> BaseCoreFragment.baseObserver(
	@NonNull owner: LifecycleOwner, live: LiveData<Outcome<T>>,
	connectionResult: RequestConnectionResult<T>,
	type: Int = 1,
	view: View? = null
) {
	live.observeOutCome(owner, object : LiveResult<T> {
		override fun onProgress(loading: Boolean) {
			if (shouldHideKeyboard) {
				hideInputMethod()
			}
			isLoading = loading
			connectionResult.onProgress(loading)

		}

		override fun onSuccess(data: T) {
			isRefreshing = false
			view?.invisiable()
			connectionResult.onSuccess(data)
		}

		override fun onFailed(errorMessage: String) {
			when (type) {
				1 -> {
					eToast(errorMessage)
					connectionResult.onFailed(errorMessage)
				}
				2 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage)
				}
				3 -> {
					if (view != null) {
						connectionResult.onFailed(errorMessage)
						view.visiable()
					}
				}
				4 -> {
					retry(object : Retry {
						override fun onRetry() {
							connectionResult.onFailed(errorMessage)
						}
					}, errorMessage, isFinishApp = false)
				}
				5 -> {
					eToast(errorMessage)
					connectionResult.onFailed(errorMessage)
				}
			}
		}

		override fun onFailed(error: Msg) {
			connectionResult.onFailed(error)
		}

		override fun onUnauthorized() {
			if (isForceLogin) {
				unAuthorizedIntent?.let {
					startActivity(it)
				}
				finish()
			} else {
				logOut(true)
			}
		}
	})

}
