package ir.atriatech.extensions.android

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import ir.atriatech.extensions.R
import java.util.*

/**
 * Created by Victor on 2017/9/12. (ง •̀_•́)ง
 */

inline fun <reified T : Activity> Fragment.goActivity() =
	startActivity(Intent(activity, T::class.java))

inline fun <reified T : Activity> Fragment.goActivity(requestCode: Int) =
	startActivityForResult(Intent(activity, T::class.java), requestCode)

inline fun <reified T : Service> Fragment.goService() =
	activity?.startService(Intent(activity, T::class.java))

inline fun <reified T : Service> Fragment.goService(
	sc: ServiceConnection,
	flags: Int = Context.BIND_AUTO_CREATE
) =
	activity?.bindService(Intent(activity, T::class.java), sc, flags)

fun Fragment.hideInputMethod() {
	if (activity != null && activity!!.window.peekDecorView() != null) {
		inputMethodManager.hideSoftInputFromWindow(activity!!.window.peekDecorView().windowToken, 0)
		this.view?.requestFocus()
	}
}

fun Fragment.showInputMethod(v: EditText) {
	v.requestFocus()
	inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
}

fun Fragment.finish() = activity?.finish()
fun Fragment.finishAffinity() = activity?.finishAffinity()
fun Fragment.navWithUp(@IdRes resId: Int, arg: Bundle? = null, useAnim: Boolean = true) {
	if (useAnim) {
		val options = NavOptions.Builder()
			.setEnterAnim(R.anim.nav_default_enter_anim)
			.setExitAnim(R.anim.popup_exit)
			.setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
			.setPopExitAnim(R.anim.popup_exit)
			.build()
		NavHostFragment.findNavController(this).navigateUp()
		NavHostFragment.findNavController(this).navigate(resId, arg, options)
	} else {
		val options = NavOptions.Builder()
			.build()
		NavHostFragment.findNavController(this).navigateUp()
		NavHostFragment.findNavController(this).navigate(resId, arg, options)
	}
}

fun Fragment.removeUntil(@IdRes resId: Int, inclusive: Boolean = false) {
	findNavController().popBackStack(resId, inclusive)
}

fun Fragment.back() = findNavController().popBackStack()
fun Fragment.navTo(
	@IdRes resId: Int, arg: Bundle? = null,
	useAnim: Boolean = true,
	navigatorExtras: Navigator.Extras? = null,
	@AnimRes enterAnimRes: Int = 0,
	@AnimRes exitAnimRes: Int = 0,
	@AnimRes popEnterAnimRes: Int = 0,
	@AnimRes popExistAnimRes: Int = 0
) {
	if (useAnim) {
		val options = if (enterAnimRes != 0 && exitAnimRes != 0 && popEnterAnimRes != 0 && popExistAnimRes != 0) {
			NavOptions.Builder()
				.setEnterAnim(enterAnimRes)
				.setExitAnim(exitAnimRes)
				.setPopEnterAnim(popEnterAnimRes)
				.setPopExitAnim(popExistAnimRes)
				.build()
		} else if (enterAnimRes != 0 && exitAnimRes != 0) {
			NavOptions.Builder()
				.setEnterAnim(enterAnimRes)
				.setExitAnim(exitAnimRes)
				.build()
		} else {
			NavOptions.Builder()
				.setEnterAnim(R.anim.nav_default_enter_anim)
				.setExitAnim(R.anim.popup_exit)
				.setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
				.setPopExitAnim(R.anim.popup_exit)
				.build()
		}
		NavHostFragment.findNavController(this).navigate(resId, arg, options, navigatorExtras)
	} else {
		val options = NavOptions.Builder()
			.build()
		NavHostFragment.findNavController(this).navigate(resId, arg, options, navigatorExtras)
	}
}

fun Fragment.navToDeepLink(@NonNull deepLink: Uri) {
	NavHostFragment.findNavController(this).navigate(deepLink)
}

fun Fragment.pickImage(size: Int = 600, aspectX: Int = 1, aspectY: Int = 1, isAspectRatio: Boolean = true) {
	CropImage.activity().apply {
		setGuidelines(CropImageView.Guidelines.ON)
		if (isAspectRatio) {
			setAspectRatio(aspectX, aspectY)
			setFixAspectRatio(true)
		}
		setRequestedSize(size, size)
		start(requireContext(), this@pickImage)
	}
}

fun Fragment.pickImageOnActivityResult(
	requestCode: Int,
	data: Intent?,
	callback: PickImageCallback
) {
	if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
		try {
			callback.onSuccess(CropImage.getActivityResult(data).uri)
		} catch (e: Exception) {
		}
	} else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
		try {
			callback.onFail(CropImage.getActivityResult(data).error)
		} catch (e: Exception) {
		}
	}
}

fun extraArguments(
	argument1: Any,
	key1: String,
	argument2: Any? = null,
	key2: String = "",
	argument3: Any? = null,
	key3: String = "",
	argument4: Any? = null,
	key4: String = ""
): Bundle {
	val arg = Bundle()
	when (argument1) {
		is String -> {
			arg.putString(key1, argument1)
		}
		is Int -> {
			arg.putInt(key1, argument1)
		}
		is Boolean -> {
			arg.putBoolean(key1, argument1)
		}
		is Parcelable -> {
			arg.putParcelable(key1, argument1)
		}
		is ArrayList<*> -> {
			try {
				@Suppress("UNCHECKED_CAST")
				arg.putParcelableArrayList(key1, argument1 as ArrayList<out Parcelable>)
			} catch (e: Exception) {
			}
		}
	}
	when (argument2) {
		is String -> {
			arg.putString(key2, argument2)
		}
		is Int -> {
			arg.putInt(key2, argument2)
		}
		is Boolean -> {
			arg.putBoolean(key2, argument2)
		}
		is Parcelable -> {
			arg.putParcelable(key2, argument2)
		}
		is ArrayList<*> -> {
			try {
				@Suppress("UNCHECKED_CAST")
				arg.putParcelableArrayList(key2, argument2 as ArrayList<out Parcelable>)
			} catch (e: Exception) {
			}
		}
	}
	when (argument3) {
		is String -> {
			arg.putString(key3, argument3)
		}
		is Int -> {
			arg.putInt(key3, argument3)
		}
		is Boolean -> {
			arg.putBoolean(key3, argument3)
		}
		is Parcelable -> {
			arg.putParcelable(key3, argument3)
		}
		is ArrayList<*> -> {
			try {
				@Suppress("UNCHECKED_CAST")
				arg.putParcelableArrayList(key3, argument3 as ArrayList<out Parcelable>)
			} catch (e: Exception) {
			}
		}
	}
	when (argument4) {
		is String -> {
			arg.putString(key4, argument4)
		}
		is Int -> {
			arg.putInt(key4, argument4)
		}
		is Boolean -> {
			arg.putBoolean(key4, argument4)
		}
		is Parcelable -> {
			arg.putParcelable(key4, argument4)
		}
		is ArrayList<*> -> {
			try {
				@Suppress("UNCHECKED_CAST")
				arg.putParcelableArrayList(key4, argument4 as ArrayList<out Parcelable>)
			} catch (e: Exception) {
			}
		}
	}
	return arg
}

//fun <T> Fragment.getExtraArguments() {
//    var type: T? = null
//    var arguemnt:T
//    when {
//        type is String? -> {
//            try {
//                mViewModel.mobile = arguments!!.getString(ARG_STRING_1, "")
//            } catch (e: Exception) {
//            }
//        }
//    }
//
//}

interface PickImageCallback {
	fun onSuccess(uri: Uri)
	fun onFail(error: Exception)
}

