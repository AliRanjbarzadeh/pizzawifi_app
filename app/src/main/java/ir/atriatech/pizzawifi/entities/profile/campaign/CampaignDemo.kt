package ir.atriatech.pizzawifi.entities.profile.campaign


import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CampaignDemo : BaseObservable() {

	@SerializedName("id")
	@Expose
	var id: Int = 0
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.id)
		}

	@SerializedName("name")
	@Expose
	var name: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.name)
		}

	@SerializedName("image")
	@Expose
	var image: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.image)
		}

	@SerializedName("description")
	@Expose
	var description: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.description)
		}

	@SerializedName("upload_description")
	@Expose
	var uploadDescription: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.uploadDescription)
		}

	@SerializedName("started_at")
	@Expose
	var startedAt: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.startedAt)
		}

	@SerializedName("ended_at")
	@Expose
	var endedAt: String = ""
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.endedAt)
		}

	@SerializedName("can_participate")
	@Expose
	var canParticipate: Boolean = false
		@Bindable get
		set(value) {
			field = value
			notifyPropertyChanged(BR.canParticipate)
		}

	fun initialize(data: CampaignDemo) {
		id = data.id
		name = data.name
		image = data.image
		description = data.description
		startedAt = data.startedAt
		endedAt = data.endedAt
		canParticipate = data.canParticipate
		uploadDescription = data.uploadDescription
		notifyChange()
	}
}