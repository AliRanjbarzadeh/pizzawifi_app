package ir.atriatech.pizzawifi.ui.main.more.requestForm

import android.graphics.Typeface
import android.net.Uri
import androidx.lifecycle.LiveData
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.entities.Outcome
import ir.atriatech.extensions.android.eToast
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.kotlin.isEmail
import ir.atriatech.extensions.kotlin.isFixPhoneNumber
import ir.atriatech.extensions.kotlin.isMobile
import ir.atriatech.extensions.reactivex.toLiveData
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragmentViewModel
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.ProvinceModel
import ir.atriatech.pizzawifi.entities.more.FormElement
import ir.atriatech.pizzawifi.repository.MainRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import javax.inject.Inject

class RequestFormViewModel : BaseFragmentViewModel() {

    /*val parentId: Int = 100
    val INPUT_TEXT = 1
    val INPUT_PASSWORD = 2
    val INPUT_EMAIL = 3
    val INPUT_NUMBER = 4
    val INPUT_TEXT_MULTI_LINE = 5
     */

    var provinceList: MutableList<ProvinceModel> = mutableListOf()

    val REQUEST_CODE_DOC = 10
    val REQUEST_CODE_RESUME = 11
    lateinit var toolsAdapter: ToolsAdapter
    lateinit var typeface: Typeface

    var documentUrisList: MutableList<Uri> = mutableListOf()
    var resumeUrisList: MutableList<Uri> = mutableListOf()
    var docMultiPartFiles: MutableList<MultipartBody.Part> = ArrayList()
    var resumeMultiPartFiles: MutableList<MultipartBody.Part> = ArrayList()

    var mSituationList: MutableList<SituationModel> = mutableListOf()
    var situationSelPosition: Int = 0
    var citySelPosition: Int = -1
    var provinceSelPosition: Int = -1
    var restaurantSkillFlag = false

    var name = ""
    var lastName = ""
    var fixPhoneNUmber = ""
    var mobilePhoneNumber = ""
    var email = ""
    var age: Int = 0
    var province = ProvinceModel()
    var city = CityModel()
    var selectedSituation = 1
    var exactAddress = ""
    var areaProperty: Int = -1
    var investmentValue: Long = -1
    var currentJob = ""
    var knowAboutRestaurant = ""
    var trainingCourse = ""
    var monthlyTurnOver: Long = 0
    var description = ""


    var uriResume: Uri? = null

    var formElements: MutableList<FormElement> = mutableListOf()

    var showResumeLogoImg: Boolean = false
    var showDocumentImg: Boolean = false

    var flagResumeLogoImg: Boolean = false
    var flagDocumentImg: Boolean = false


    @Inject
    lateinit var repository: MainRepository
    val mObserver: LiveData<Outcome<Msg>> by lazy { repository.branchFormOutcome.toLiveData(bag) }

    val mProvinceObserver: LiveData<Outcome<MutableList<ProvinceModel>>> by lazy {
        repository.provinceOutcome.toLiveData(
            bag
        )
    }

    init {
        component.inject(this)
    }

    fun getData() {
        if (provinceList.isEmpty()) {
            repository.provinceArchive(bag)
        }
    }


    fun addResume() {
        if (name.isEmpty()) {
            eToast(findString(R.string.emptyName))
            return
        }
        if (lastName.isEmpty()) {
            eToast(findString(R.string.emptyLastName))
            return
        }
        if (fixPhoneNUmber.isEmpty()) {
            eToast(findString(R.string.emptyFixNumber))
            return
        }
        if (!fixPhoneNUmber.isFixPhoneNumber()) {
            eToast(findString(R.string.wrongFixPhoneNumber))
            return
        }

        if (mobilePhoneNumber.isEmpty()) {
            eToast(findString(R.string.emptymobilePhoneNumber))
            return
        }
        if (!mobilePhoneNumber.isMobile()) {
            eToast(findString(R.string.wrongMobile))
            return
        }
        if (email.isNotEmpty() && !email.isEmail()) {
            eToast(findString(R.string.wrongEmailAddress))
            return
        }
        if (age == 0) {
            eToast(findString(R.string.emptyage))
            return
        }
        if (province.id == 0) {
            eToast(findString(R.string.emptyprovince))
            return
        }
        if (city.id == 0) {
            eToast(findString(R.string.emptycity))
            return
        }

        if (situationSelPosition == 0 && exactAddress.isEmpty()) {
            eToast(findString(R.string.emptyexactAddress))
            return
        }
        if (situationSelPosition == 0 && areaProperty == -1) {
            eToast(findString(R.string.emptyareaProperty))
            return
        }
        if (investmentValue == 0.toLong()) {
            eToast(findString(R.string.emptyinvestmentValue))
            return
        }
        if (currentJob.isEmpty()) {
            eToast(findString(R.string.emptycurrentJob))
            return
        }
        if (resumeUrisList.isEmpty()) {
            eToast(findString(R.string.emptyResume))
            return
        }

        if (documentUrisList.isEmpty()) {
            eToast(findString(R.string.emptyDocumentUploaded))
            return
        }


        // use if one file is selected not list
//        var resume: MultipartBody.Part? = null
//        if (uriResume != null) {
//            val file = File(uriResume!!.path)
//
//            val requestFile: RequestBody = RequestBody.create(
//                MediaType.parse("image/*"),
//                file
//            )
//            resume = MultipartBody.Part.createFormData("resume", file.name, requestFile)
//            Log.i("TAG", "sendFileToServer: file.getName()" + file.name)
//        }
//

        var reqBody_exactAddress: RequestBody? = null
        var reqBody_areaProperty: RequestBody? = null

        val reqBody_name = RequestBody.create(MediaType.parse("text/plain"), name)
        val reqBody_lastName = RequestBody.create(MediaType.parse("text/plain"), lastName)
        val reqBody_fixPhoneNUmber =
            RequestBody.create(MediaType.parse("text/plain"), fixPhoneNUmber)
        val reqBody_mobilePhoneNumber =
            RequestBody.create(MediaType.parse("text/plain"), mobilePhoneNumber)
        val reqBody_email = RequestBody.create(MediaType.parse("text/plain"), email)
        val reqBody_age = RequestBody.create(MediaType.parse("text/plain"), age.toString())
        val reqBody_province =
            RequestBody.create(MediaType.parse("text/plain"), province.id.toString())
        val reqBody_city = RequestBody.create(MediaType.parse("text/plain"), city.id.toString())

//        @Int (1: Malek - 2: Rahn - 3: Ejareh)
        val reqBody_situation =
            RequestBody.create(MediaType.parse("text/plain"), selectedSituation.toString())
        if (situationSelPosition == 0) {
            reqBody_exactAddress = RequestBody.create(MediaType.parse("text/plain"), exactAddress)
            reqBody_areaProperty =
                RequestBody.create(MediaType.parse("text/plain"), areaProperty.toString())
        }
        val reqBody_investmentValue =
            RequestBody.create(MediaType.parse("text/plain"), investmentValue.toString())
        val reqBody_currentJob = RequestBody.create(MediaType.parse("text/plain"), currentJob)

        val restuarantSkill =
            RequestBody.create(MediaType.parse("text/plain"), knowAboutRestaurant)
        val reqBody_trainingCourse =
            RequestBody.create(MediaType.parse("text/plain"), trainingCourse)
        val reqBody_monthlyTurnOver =
            RequestBody.create(MediaType.parse("text/plain"), monthlyTurnOver.toString())
        val reqBody_description = RequestBody.create(MediaType.parse("text/plain"), description)

        repository.addBranchRequest(
            reqBody_name,
            reqBody_lastName,
            reqBody_mobilePhoneNumber,
            reqBody_fixPhoneNUmber,
            reqBody_email,
            reqBody_age,
            reqBody_province,
            reqBody_city,
            reqBody_situation,
            reqBody_exactAddress,
            reqBody_areaProperty,
            reqBody_currentJob,
            reqBody_investmentValue,
            restuarantSkill,
            reqBody_trainingCourse,
            reqBody_monthlyTurnOver,
            reqBody_description,
            resumeMultiPartFiles,
            docMultiPartFiles,
            bag
        )
    }
}
