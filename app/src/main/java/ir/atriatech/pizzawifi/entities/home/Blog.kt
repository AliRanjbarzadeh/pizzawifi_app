package ir.atriatech.pizzawifi.entities.home

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Blog() : Parcelable {
    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("type")
    @Expose
    var type: String = ""

    @SerializedName("name")
    @Expose
    var name: String = ""

    @SerializedName("icon")
    @Expose
    var icon: String = ""

    @SerializedName("image")
    @Expose
    var image: String = ""

    @SerializedName("link")
    @Expose
    var link: String? = null

    @SerializedName("internal_link")
    @Expose
    var internalLink: BlogLink? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("slides")
    @Expose
    var slides: MutableList<String> = mutableListOf()

    @SerializedName("videos")
    @Expose
    var videos: MutableList<BlogVideo> = mutableListOf()


    @SerializedName("instagram_campaign_id")
    @Expose
    var instagramCampaignId: Int = 0

    fun isSlider() = type == "slider"

    constructor(source: Parcel) : this(
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
    }

    override fun toString(): String {
        return "Blog(id=$id, type='$type', description=$description)"
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Blog> = object : Parcelable.Creator<Blog> {
            override fun createFromParcel(source: Parcel): Blog = Blog(source)
            override fun newArray(size: Int): Array<Blog?> = arrayOfNulls(size)
        }
    }
}
