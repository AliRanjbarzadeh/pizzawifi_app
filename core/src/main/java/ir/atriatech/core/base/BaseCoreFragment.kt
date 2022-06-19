package ir.atriatech.core.base


import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import io.reactivex.disposables.CompositeDisposable
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.interfaces.BaseCoreFragmentListener


open class BaseCoreFragment : Fragment(), BaseCoreFragmentListener {
	var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
	open var limit = 30
	var disposable = CompositeDisposable()
	var unAuthorizedIntent: Intent? = null
	var isForceLogin = false
	var progressView: View? = null
	var disableView: View? = null
	var topBackButton: AppCompatTextView? = null
	var smartRefreshLayout: SmartRefreshLayout? = null
	var emptyVP: ViewGroup? = null
	var progressVP: ViewGroup? = null
	var recyclerView: RecyclerView? = null
	var baseAdapter: BaseAdapter? = null
	var emptyView: View? = null
	var disableViewIsShown = false
	var shouldHideKeyboard = true
	var isLoading = false
	var isAllLoaded = false
	var isFirst = true
	var offset = 0
	var isRefreshing = false
	var anyList = mutableListOf<Any>()
}

interface RequestConnectionResult<T> {
	fun onProgress(loading: Boolean) {}
	fun onSuccess(data: T) {}
	fun onFailed(errorMessage: String) {}
	fun onFailed(msg: Msg) {}
}

interface MainFragmentCallBack {
	fun onRefreshListener() {}
	fun onReachToEndListener() {}
	fun requestToNet() {}
}