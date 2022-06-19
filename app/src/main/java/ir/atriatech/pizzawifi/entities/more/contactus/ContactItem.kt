package ir.atriatech.pizzawifi.entities.more.contactus

import androidx.annotation.DrawableRes
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContactItem(
	@SerializedName("title")
	@Expose
	var title: String = "",
	@SerializedName("value")
	@Expose
	var value: String = "",
	@SerializedName("type")
	@Expose
	var type: String = "", //tel, email, address, facebook, twitter, instagram, whatsapp, youtube, telegram
	@SerializedName("link")
	@Expose
	var link: String = "",
	@DrawableRes
	var image: Int = 0,
	var isHasTitle: Boolean = false
) {

}