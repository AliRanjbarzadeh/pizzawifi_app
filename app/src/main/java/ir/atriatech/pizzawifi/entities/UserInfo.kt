package ir.atriatech.pizzawifi.entities

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import ir.atriatech.extensions.kotlin.priceFormat

class UserInfo : BaseObservable() {

	@SerializedName("mobile")
	@Expose
	var mobile: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.mobile)
		}

	@SerializedName("name")
	@Expose
	var name: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.name)
		}

	@SerializedName("instagram")
	@Expose
	var instagram: String = ""

	@SerializedName("image")
	@Expose
	var image: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.image)
		}

	@SerializedName("wallet")
	@Expose
	var wallet: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.wallet)
			notifyPropertyChanged(BR.walletFormat)
		}

	@SerializedName("birth_date")
	@Expose
	var birthDate: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.birthDate)
		}

	@SerializedName("wedding_date")
	@Expose
	var weddingDate: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.weddingDate)
		}

	@SerializedName("club")
	@Expose
	var club: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.club)
			notifyPropertyChanged(BR.clubFormat)
		}

	var isLogin: Boolean = false
	var token: String = ""

	@Bindable
	fun getWalletFormat() = wallet.priceFormat()

	@Bindable
	fun getClubFormat() = club.priceFormat("")

	fun reset() {
		mobile = ""
		name = ""
		instagram = ""
		image = ""
		wallet = 0
		birthDate = ""
		weddingDate = ""
		club = 0
		isLogin = false
		token = ""
		notifyChange()
	}

	fun init(userInfo: UserInfo) {
		mobile = userInfo.mobile
		name = userInfo.name
		instagram = userInfo.instagram
		image = userInfo.image
		wallet = userInfo.wallet
		birthDate = userInfo.birthDate
		weddingDate = userInfo.weddingDate
		club = userInfo.club
		notifyChange()
	}

	override fun toString(): String {
		return "UserInfo(mobile='$mobile', name='$name', isLogin=$isLogin, token='$token')"
	}


}