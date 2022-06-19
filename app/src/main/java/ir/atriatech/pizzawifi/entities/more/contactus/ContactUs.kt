package ir.atriatech.pizzawifi.entities.more.contactus

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContactUs(
    @SerializedName("location")
    @Expose
    var myLocation: MyLocation = MyLocation(),

    @SerializedName("contact_items")
    @Expose
    var contactItems: MutableList<ContactItem> = mutableListOf()
)