package ir.atriatech.core.entities

import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.JsonElement
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Msg {
	@SerializedName("msg")
	@Expose
	var msg: String = ""

	@SerializedName("status_text")
	@Expose
	var statusText: String = ""

	@SerializedName("image")
	@Expose
	var image = ""

	@SerializedName("name")
	@Expose
	var name = ""

	@SerializedName("id")
	@Expose
	var id = -1

	@SerializedName("link")
	@Expose
	var link = ""

	@SerializedName("mobile")
	@Expose
	var mobile = ""

	@SerializedName("email")
	@Expose
	var email = ""

	@SerializedName("token")
	@Expose
	var token: String = ""

	@SerializedName("password")
	@Expose
	var password: String = ""

	@SerializedName("part")
	@Expose
	var part: String = ""

	@SerializedName("data")
	@Expose
	var data: JsonElement? = null

	@SerializedName("percent")
	@Expose
	var percent: Int = 0

	@SerializedName("max_amount")
	@Expose
	var maxAmount: Int = 0

	@SerializedName("payment_status")
	@Expose
	var paymentStatus: Int = 0

	@SerializedName("club")
	@Expose
	var club: Int = 0

	@SerializedName("wallet")
	@Expose
	var wallet: Int = 0

	@SerializedName("instagram")
	@Expose
	var instagram: String = ""

	@SerializedName("birth_date")
	@Expose
	var birthDate: String = ""

	@SerializedName("wedding_date")
	@Expose
	var weddingDate: String = ""

	override fun toString(): String {
		return "Msg(msg='$msg', statusText='$statusText', image='$image', name='$name', id=$id, link='$link', mobile='$mobile', email='$email', token='$token', password='$password', part='$part', percent=$percent, maxAmount=$maxAmount)"
	}


}