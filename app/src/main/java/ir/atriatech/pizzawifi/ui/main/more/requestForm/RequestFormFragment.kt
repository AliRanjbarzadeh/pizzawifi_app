package ir.atriatech.pizzawifi.ui.main.more.requestForm

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.customListAdapter
import ir.atriatech.core.base.RequestConnectionResult
import ir.atriatech.core.entities.Msg
import ir.atriatech.core.generateRandomString
import ir.atriatech.core.interfaces.RecyclerViewTools
import ir.atriatech.extensions.android.*
import ir.atriatech.extensions.app.findString
import ir.atriatech.extensions.base.fragment.baseObserver
import ir.atriatech.extensions.base.fragment.hideKeyboard
import ir.atriatech.extensions.base.fragment.setProgressView
import ir.atriatech.extensions.kotlin.d
import ir.atriatech.extensions.kotlin.e
import ir.atriatech.extensions.kotlin.toEnglish
import ir.atriatech.pizzawifi.R
import ir.atriatech.pizzawifi.base.BaseFragment
import ir.atriatech.pizzawifi.databinding.FragmentBranchRequestBinding
import ir.atriatech.pizzawifi.entities.CityModel
import ir.atriatech.pizzawifi.entities.ProvinceModel
import ir.atriatech.pizzawifi.ui.main.MainActivity
import kotlinx.android.synthetic.main.fragment_branch_request.*
import kotlinx.android.synthetic.main.item_doc_img.view.*
import kotlinx.android.synthetic.main.item_more_img.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.FileOutputStream
import java.net.URLEncoder
import java.text.NumberFormat
import java.util.*


class RequestFormFragment : BaseFragment() {
    lateinit var binding: FragmentBranchRequestBinding
    private lateinit var mViewModel: RequestFormViewModel
    private lateinit var numberFormat: NumberFormat
    private var mdProvince: MaterialDialog? = null

    private var generatedTempFile = mutableListOf<File>()

    //Layout Inflater
    private var inflater: LayoutInflater? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this).get(RequestFormViewModel::class.java)
        mProvinceObserver()
        mObserver()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_branch_request,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.toolbarId.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarId.setNavigationOnClickListener { requireActivity().onBackPressed() }
        binding.viewModel = mViewModel

        hideKeyboard(binding.mainView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Layout Inflater
        inflater = requireActivity().layoutInflater
        numberFormat = NumberFormat.getNumberInstance(Locale.US)
        mViewModel.typeface =
            Typeface.createFromAsset(context!!.assets, getString(R.string.app_font))

        mViewModel.toolsAdapter = ToolsAdapter(onRecyclerItemClick)

        mViewModel.getData()

        binding.rbYes.setOnClickListener {

            mViewModel.restaurantSkillFlag = true
            mViewModel.knowAboutRestaurant = "yes"
            binding.viewModel = mViewModel
        }
        binding.rbNo.setOnClickListener {
            mViewModel.restaurantSkillFlag = false
            mViewModel.knowAboutRestaurant = "no"
            binding.viewModel = mViewModel
        }

        initSituationList()

        binding.etProvince.setOnClickListener {
            hideInputMethod()
            if ((mdProvince != null && mdProvince!!.isShowing) || requireActivity().isFinishing ||
                (requireActivity() is MainActivity && (requireActivity() as MainActivity).isOnPause)
            ) {
                return@setOnClickListener
            }
            showProvinceDialog()
        }

        binding.etCity.setOnClickListener {

            hideInputMethod()
            if (mViewModel.provinceSelPosition == -1) {
                eToast(findString(R.string.pleaseSelectProvinceFirst))
                return@setOnClickListener
            }

            if ((mdProvince != null && mdProvince!!.isShowing) || requireActivity().isFinishing ||
                (requireActivity() is MainActivity && (requireActivity() as MainActivity).isOnPause)
            ) {
                return@setOnClickListener
            }
            showCityDialog()
        }

        if (mViewModel.situationSelPosition == 0) {
            binding.etSituation.setText(mViewModel.mSituationList[0].name)
            binding.tilAddress.visibility = View.VISIBLE
            binding.tilAreaProperty.visibility = View.VISIBLE
        } else {
            binding.tilAddress.visibility = View.GONE
            binding.tilAreaProperty.visibility = View.GONE

            binding.etSituation.setText(mViewModel.mSituationList[mViewModel.situationSelPosition].name)
        }

        binding.etSituation.setOnClickListener {
            hideInputMethod()
            if ((mdProvince != null && mdProvince!!.isShowing) || requireActivity().isFinishing ||
                (requireActivity() is MainActivity && (requireActivity() as MainActivity).isOnPause)
            ) {
                return@setOnClickListener
            }
            if (mViewModel.mSituationList.isNotEmpty()) {
                showSituationDialog()
            }

        }

        val constMoreDoc = layoutInflater.inflate(
            R.layout.item_more_img,
            linearUploadDocs,
            false
        ) as FrameLayout

        constMoreDoc.txtBillImage.setOnClickListener {
            mViewModel.flagDocumentImg = true
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/msword",
                    "application/pdf",
                    "application/zip",
                    "application/rar",
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            try {
                startActivityForResult(
                    Intent.createChooser(intent, "انتخاب فایل جهت آپلود"),
                    mViewModel.REQUEST_CODE_DOC
                )
            } catch (ex: Exception) {
                d(
                    "browseClick :${ex}",
                    "Tag Choose upload file"
                );
//            pickImage()
            }
        }
        binding.linearUploadDocs.addView(constMoreDoc)

        val constMoreResume = layoutInflater.inflate(
            R.layout.item_more_img,
            linearResumeUploads,
            false
        ) as FrameLayout

        constMoreResume.txtBillImage.setOnClickListener {
            mViewModel.flagResumeLogoImg = true
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf(
                    "application/msword",
                    "application/pdf",
                    "application/zip",
                    "application/rar",
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
            try {
                startActivityForResult(
                    Intent.createChooser(intent, "انتخا فایل جهت آپلود"),
                    mViewModel.REQUEST_CODE_RESUME
                )
            } catch (ex: Exception) {
                d(
                    "browseClick :${ex}",
                    "Tag Choose upload file"
                );//android.content.ActivityNotFoundException ex
            }
//            pickImage()
        }
        binding.linearResumeUploads.addView(constMoreResume)



        binding.btnSend.setOnClickListener {
            mViewModel.name = binding.etName.text?.trim().toString()
            mViewModel.lastName = binding.etLastName.text?.trim().toString()
            mViewModel.fixPhoneNUmber = binding.etFixNumber.text?.trim().toString()
            mViewModel.mobilePhoneNumber = binding.etMobileNumber.text?.trim().toString()
            mViewModel.email = binding.etEmail.text?.trim().toString()
            if (binding.etAge.text?.trim().toString().isNotEmpty()) {
                mViewModel.age = binding.etAge.text?.trim().toString().toInt()
            }

//            mViewModel.province = binding.etProvince.text?.trim().toString()
//            mViewModel.city = binding.etCity.text?.trim().toString()
            mViewModel.exactAddress = binding.etAddress.text?.trim().toString()

            if (binding.etAreaProperty.text?.trim().toString().isNotEmpty()) {
                mViewModel.areaProperty = binding.etAreaProperty.text?.trim().toString().toInt()

            }

            mViewModel.currentJob = binding.etCurrentJob.text?.trim().toString()

            if (binding.rbYes.isChecked) {
                mViewModel.knowAboutRestaurant = "yes"
            } else {
                mViewModel.knowAboutRestaurant = "no"
            }
            mViewModel.trainingCourse = binding.etTrainingCourse.text?.trim().toString()

            mViewModel.description = binding.etDescription.text?.trim().toString()

            //Documents
            for (i in 0 until mViewModel.documentUrisList.size) {
                try {
                    val fileName =
                        FindPath.getFileName(requireContext(), mViewModel.documentUrisList[i])
                            .split(".")
                    val mFileName =
                        generateRandomString() + "." + fileName.last() //keep in array to remove after success
                    val mFile = File(requireActivity().filesDir, mFileName)
                    generatedTempFile.add(mFile)
                    val maxBufferSize = 1 * 1024 * 1024
                    val fileType =
                        requireContext().contentResolver.getType(mViewModel.documentUrisList[i])

                    val inputStream =
                        requireContext().contentResolver.openInputStream(mViewModel.documentUrisList[i])
                    inputStream?.let { mInputStream ->
                        mFile.createNewFile()
                        val bytesAvailable = mInputStream.available()
                        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                        val buffers = ByteArray(bufferSize)
                        val outputStream = FileOutputStream(mFile)
                        var read = 0
                        while (read != -1) {
                            read = inputStream.read(buffers)
                            if (read == -1) {
                                break
                            } else {
                                outputStream.write(buffers, 0, read)
                            }
                        }
                        inputStream.close()
                        outputStream.close()

                        if (fileType != null && fileType.isNotEmpty()) {
                            val requestFile1: RequestBody = RequestBody.create(
                                MediaType.parse(fileType),
                                mFile
                            )
                            val image1: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "documents[]",
                                    URLEncoder.encode(mFileName, "utf-8"),
                                    requestFile1
                                )
                            mViewModel.docMultiPartFiles.add(image1)
                        }
                    }
                } catch (ex: Exception) {
                    e(ex.message, "Ali")
                }
            }

            //Resume
            for (i in 0 until mViewModel.resumeUrisList.size) {
                try {
                    val fileName =
                        FindPath.getFileName(requireContext(), mViewModel.resumeUrisList[i])
                            .split(".")
                    val mFileName =
                        generateRandomString() + "." + fileName.last() //keep in array to remove after success
                    val mFile = File(requireActivity().filesDir, mFileName)
                    generatedTempFile.add(mFile)
                    val maxBufferSize = 1 * 1024 * 1024
                    val fileType =
                        requireContext().contentResolver.getType(mViewModel.resumeUrisList[i])

                    val inputStream =
                        requireContext().contentResolver.openInputStream(mViewModel.resumeUrisList[i])
                    inputStream?.let { mInputStream ->
                        mFile.createNewFile()
                        val bytesAvailable = mInputStream.available()
                        val bufferSize = Math.min(bytesAvailable, maxBufferSize)
                        val buffers = ByteArray(bufferSize)
                        val outputStream = FileOutputStream(mFile)
                        var read = 0
                        while (read != -1) {
                            read = inputStream.read(buffers)
                            if (read == -1) {
                                break
                            } else {
                                outputStream.write(buffers, 0, read)
                            }
                        }
                        inputStream.close()
                        outputStream.close()

                        if (fileType != null && fileType.isNotEmpty()) {
                            val requestFile1: RequestBody = RequestBody.create(
                                MediaType.parse(fileType),
                                mFile
                            )
                            val image1: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "resume[]",
                                    URLEncoder.encode(mFileName, "utf-8"),
                                    requestFile1
                                )
                            mViewModel.resumeMultiPartFiles.add(image1)
                        }
                    }
                } catch (ex: Exception) {
                    e(ex.message, "Ali")
                }
            }

            mViewModel.addResume()
        }

        binding.etMonthlyTurnOver.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().trim().isNotEmpty()) {
                    binding.etMonthlyTurnOver.removeTextChangedListener(this)
                    var value: String = editable.toString().replace(",", "")
                    value = value.toEnglish()
                    if (value.isNotEmpty()) {
                        if (value.length == 1 && value.toLong() == 0L) {
                            binding.etMonthlyTurnOver.setText("")


                        } else if (value.toLong() > 9223372036854775807) {
                            binding.etMonthlyTurnOver.setText(
                                "${
                                    numberFormat.format(
                                        9223372036854775807
                                    )
                                }"
                            )

                        } else {
                            binding.etMonthlyTurnOver.setText(
                                numberFormat.format(

                                    value.toLong()
                                )
                            )

                            mViewModel.monthlyTurnOver = value.toLong()
                        }
                    }
                    binding.etMonthlyTurnOver.setSelection(binding.etMonthlyTurnOver.text.toString().length)
                    binding.etMonthlyTurnOver.addTextChangedListener(this)
                }

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })


        binding.etInvestmentValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                if (editable.toString().trim().isNotEmpty()) {
                    binding.etInvestmentValue.removeTextChangedListener(this);
                    var value: String = editable.toString().replace(",", "");
                    value = value.toEnglish()
                    d("value.toLong() ==> ${value.toLong()}", "value.toLong()")
                    if (value.isNotEmpty()) {
                        if (value.length == 1 && value.toLong() == 0.toLong()) {
                            binding.etInvestmentValue.setText("")

                        } else if (value.toLong() > 9223372036854775807) {
                            binding.etInvestmentValue.setText(
                                "${
                                    numberFormat.format(
                                        9223372036854775807
                                    )
                                }"
                            )

                        } else {
                            binding.etInvestmentValue.setText(
                                numberFormat.format(
                                    value.toLong()
                                )
                            )
                            mViewModel.investmentValue = value.toLong()
                        }
                    }
                    binding.etInvestmentValue.setSelection(binding.etInvestmentValue.text.toString().length)
                    binding.etInvestmentValue.addTextChangedListener(this)
                }

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        if (mViewModel.resumeUrisList.isNotEmpty()) {
            for (i in 0 until mViewModel.resumeUrisList.size) {
                val constNewDoc = layoutInflater.inflate(
                    R.layout.item_doc_img,
                    linearResumeUploads,
                    false
                ) as FrameLayout

                constNewDoc.imgDeleteDoc.setOnClickListener {
                    mViewModel.resumeUrisList.removeAt(i)
                    linearResumeUploads.removeViewAt(i)
                }

                constNewDoc.imgPhotoDoc.setImageResource(R.drawable.ic_doc)
                linearResumeUploads.addView(
                    constNewDoc,
                    binding.linearResumeUploads.childCount - 1
                )

            }
        }

        if (mViewModel.documentUrisList.isNotEmpty()) {
            for (i in 0 until mViewModel.documentUrisList.size) {
                val constNewDoc = layoutInflater.inflate(
                    R.layout.item_doc_img,
                    linearUploadDocs,
                    false
                ) as FrameLayout

                constNewDoc.imgDeleteDoc.setOnClickListener {
                    mViewModel.documentUrisList.removeAt(i)
                    linearUploadDocs.removeViewAt(i)
                }

                constNewDoc.imgPhotoDoc.setImageResource(R.drawable.ic_doc)
                linearUploadDocs.addView(constNewDoc, binding.linearUploadDocs.childCount - 1)

            }
        }
    }

    fun initSituationList() {
        mViewModel.mSituationList.clear();
        mViewModel.mSituationList.add(SituationModel(findString(R.string.owner), false))
        mViewModel.mSituationList.add(SituationModel(findString(R.string.mortage), false))
        mViewModel.mSituationList.add(SituationModel(findString(R.string.rent), false))
    }


    fun showSituationDialog() {

        d(
            "mViewModel.mSituationList -> ${mViewModel.mSituationList}",
            "mViewModel.mSituationList"
        )
        var viewProvince: View =
            LayoutInflater.from(context).inflate(R.layout.popup_layout, null)

        var rvptionList: RecyclerView = viewProvince.findViewById(R.id.rvptionList)
        mViewModel.toolsAdapter.list = mViewModel.mSituationList.toMutableList()
        rvptionList.setHasFixedSize(true)
//            rvptionList.layoutManager = LinearLayoutManager(context)
//            rvptionList.adapter = mViewModel.toolsAdapter

        (mViewModel.toolsAdapter.list[mViewModel.situationSelPosition] as SituationModel).selected =
            true


        mdProvince = MaterialDialog(requireContext())
            .apply {
                title(R.string.selectSituation)
                customListAdapter(mViewModel.toolsAdapter, rvptionList.layoutManager)
//                    positiveButton(R.string.logoutTitle)
//                        .positiveButton {
//                        }
//                    negativeButton(R.string._no)
                show()
            }
    }

    private fun showCityDialog() {

        if (mViewModel.provinceList.isNotEmpty() && mViewModel.provinceList[mViewModel.provinceSelPosition] != null &&
            mViewModel.provinceList[mViewModel.provinceSelPosition].cities.isNotEmpty()
        ) {
            var cityList = mViewModel.provinceList[mViewModel.provinceSelPosition].cities


            var viewProvince: View =
                LayoutInflater.from(context).inflate(R.layout.popup_layout, null)

            var rvptionList: RecyclerView = viewProvince.findViewById(R.id.rvptionList)
            mViewModel.toolsAdapter.list = cityList.toMutableList()
            rvptionList.setHasFixedSize(true)
//            rvptionList.layoutManager = LinearLayoutManager(context)
//            rvptionList.adapter = mViewModel.toolsAdapter

            if (mViewModel.citySelPosition != -1) {
                (mViewModel.toolsAdapter.list[mViewModel.citySelPosition] as CityModel).isSelected =
                    true
            }


            mdProvince = MaterialDialog(requireContext())
                .apply {
                    title(R.string.selectCity)
                    customListAdapter(mViewModel.toolsAdapter, rvptionList.layoutManager)
//                    positiveButton(R.string.logoutTitle)
//                        .positiveButton {
//                        }
//                    negativeButton(R.string._no)
                    show()
                }
        }

//        flagShowCityDialog = false
    }

    fun showProvinceDialog() {

        if (mViewModel.provinceList.isNotEmpty()) {

            var viewProvince: View =
                LayoutInflater.from(context).inflate(R.layout.popup_layout, null)
            var rvptionList: RecyclerView = viewProvince.findViewById(R.id.rvptionList)
//
            mViewModel.toolsAdapter.list = mViewModel.provinceList.toMutableList()
            rvptionList.setHasFixedSize(true)
//            rvptionList.layoutManager = LinearLayoutManager(context)
//            rvptionList.adapter = mViewModel.toolsAdapter

            if (mViewModel.provinceSelPosition != -1) {
                (mViewModel.toolsAdapter.list[mViewModel.provinceSelPosition] as ProvinceModel).selected =
                    true
            }

            mdProvince = MaterialDialog(requireContext())
                .apply {
                    title(R.string.selectProvince)
                    customListAdapter(mViewModel.toolsAdapter, rvptionList.layoutManager)
//                    customView(R.layout.popup_layout)
//                    positiveButton(R.string.logoutTitle)
//                        .positiveButton {
//                        }
//                    negativeButton(R.string._no)
                    show()
                }

        }
//        flagShowProvinceDialog = false
    }


    private var onRecyclerItemClick = object : RecyclerViewTools {
        override fun <T> onItemClick(position: Int, view: View, item: T) {
            when (mViewModel.toolsAdapter.list[position]) {

                is CityModel -> {

                    binding.etCity.setText((mViewModel.toolsAdapter.list[position] as CityModel).name)
                    mViewModel.city = mViewModel.toolsAdapter.list[position] as CityModel
                    (mViewModel.toolsAdapter.list[position] as CityModel).isSelected = true
                    if (mViewModel.citySelPosition != -1) {
                        (mViewModel.toolsAdapter.list[mViewModel.citySelPosition] as CityModel).isSelected =
                            false
                    }
                    mViewModel.citySelPosition = position
                    mdProvince?.dismiss()
                }

                is ProvinceModel -> {

                    if (mViewModel.provinceSelPosition != -1) {
                        mViewModel.provinceList[mViewModel.provinceSelPosition].selected = false
                        (mViewModel.toolsAdapter.list[mViewModel.provinceSelPosition] as ProvinceModel).selected =
                            false
                    }
                    binding.etProvince.setText(mViewModel.provinceList[position].name)

                    mViewModel.provinceList[position].selected = true
                    (mViewModel.toolsAdapter.list[position] as ProvinceModel).selected = true

                    mViewModel.province = mViewModel.provinceList[position]
                    mViewModel.provinceSelPosition = position
                    mViewModel.citySelPosition = -1
                    mViewModel.city = CityModel()
                    etCity.setText("")

                    mdProvince?.dismiss()

                }
                is SituationModel -> {

                    etSituation.setText(mViewModel.mSituationList[position].name)
                    (mViewModel.toolsAdapter.list[position] as SituationModel).selected = true
                    mViewModel.mSituationList[position].selected = true

                    if (mViewModel.situationSelPosition != -1) {
                        (mViewModel.toolsAdapter.list[mViewModel.situationSelPosition] as SituationModel).selected =
                            false
                        mViewModel.mSituationList[mViewModel.situationSelPosition].selected =
                            false
                    }

                    mViewModel.selectedSituation = position + 1
                    d("mViewModel.selectedSituation --> ${mViewModel.selectedSituation}")
                    mViewModel.situationSelPosition = position
                    if (mViewModel.situationSelPosition == 0) {
                        binding.tilAddress.visibility = View.VISIBLE
                        binding.tilAreaProperty.visibility = View.VISIBLE
                    } else {
                        binding.tilAddress.visibility = View.GONE
                        binding.tilAreaProperty.visibility = View.GONE
                    }
                    mdProvince?.dismiss()

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == mViewModel.REQUEST_CODE_DOC) {
            data?.data?.also { uri ->
                mViewModel.documentUrisList.add(uri)

                setImgFlagsFalse()
                val constNewDoc = layoutInflater.inflate(
                    R.layout.item_doc_img,
                    linearUploadDocs,
                    false
                ) as FrameLayout
                constNewDoc.imgDeleteDoc.setOnClickListener {

                    for (i in 0 until mViewModel.documentUrisList.size) {
                        if (mViewModel.documentUrisList[i] == uri) {
                            mViewModel.documentUrisList.removeAt(i)
                            linearUploadDocs.removeViewAt(i)
                            break;
                        }
                    }
                }

                constNewDoc.imgPhotoDoc.setImageResource(R.drawable.ic_doc)
//                constNewDoc.imgPhotoDoc.setImageURI(uri)
                linearUploadDocs.addView(constNewDoc, mViewModel.documentUrisList.size - 1)

            }
        } else if (requestCode == mViewModel.REQUEST_CODE_RESUME) {

            data?.data?.also { uri ->
                mViewModel.resumeUrisList.add(uri)

                setImgFlagsFalse()
                val constNewDoc = layoutInflater.inflate(
                    R.layout.item_doc_img,
                    linearResumeUploads,
                    false
                ) as FrameLayout

//                constNewDoc.txtNewImage.setOnClickListener {
//                    mViewModel.flagDocumentImg = true
//                    pickImage()
//
//                }
                constNewDoc.imgDeleteDoc.setOnClickListener {

                    for (i in 0 until mViewModel.resumeUrisList.size) {
                        if (mViewModel.resumeUrisList[i] == uri) {
                            mViewModel.resumeUrisList.removeAt(i)
                            linearResumeUploads.removeViewAt(i)
                            break;
                        }
                    }
                }

                constNewDoc.imgPhotoDoc.setImageResource(R.drawable.ic_doc)
//                constNewDoc.imgPhotoDoc.setImageURI(uri)
                linearResumeUploads.addView(constNewDoc, mViewModel.resumeUrisList.size - 1)


                /*
                if (mViewModel.flagResumeLogoImg) {
                    setImgFlagsFalse()
                    binding.imgPhotoResume.setImageResource(R.drawable.ic_doc)
                    mViewModel.showResumeLogoImg = true
                    binding.viewModel = mViewModel
                }
                mViewModel.uriResume = uri

                pickImageOnActivityResult(requestCode, data, object : PickImageCallback {
                override fun onSuccess(uri: Uri) {
                    d("uri:${uri}", "TAG URI")
                    val url = createPartFromUri(uri, "image")
//                    toast(url)

                    if (mViewModel.flagResumeLogoImg) {
                        setImgFlagsFalse()
                        binding.imgPhotoResume.setImageURI(uri)
                        mViewModel.showResumeLogoImg = true
                        binding.viewModel = mViewModel
                    }

//                mViewModel.uploadImage(uri)
                }

                override fun onFail(error: Exception) {
                    eToast(findString(R.string.wrongImageValue))
                }
            })
                */
            }

        }

    }

    private fun getFileExtension(file: File): String {
        var fileName: String = file.name
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }


    fun setImgFlagsFalse() {
        mViewModel.flagDocumentImg = false
        mViewModel.flagResumeLogoImg = false

    }


    override fun handleNotification() {
        mDialog?.dismiss()
        removeUntil(R.id.profileFragment)
    }

    override fun onVisibilityChanged(isOpen: Boolean) {
        if (!isOpen) {
            binding.etName.clearFocus()
        }
    }

    fun deleteFiles() {
        if (generatedTempFile.isNotEmpty()) {
            for (i in 0 until generatedTempFile.size) {
                if (generatedTempFile[i].exists()) {
                    d("generatedTempFile[i] is  ==> ${generatedTempFile[i]} ")
                    //todo: delete generated file ===================
                    if (generatedTempFile[i].delete()) {
                        d("generatedTempFile[i] is deleted ==> ${generatedTempFile[i]} ")
                    } else {
                        d("generatedTempFile[i] is not deleted ==> ${generatedTempFile[i]} ")
                    }
                }
            }
            generatedTempFile.clear()
        }
    }

    private fun mObserver() {
        baseObserver(this, mViewModel.mObserver, object : RequestConnectionResult<Msg> {
            override fun onProgress(loading: Boolean) {
//                binding.btnSend.startAnimation()
                d("loading ${loading}-----------------", "Loading")
                setProgressView(mainView, loading)
            }

            override fun onSuccess(data: Msg) {
                d("onSuccess -----------------", "Loading")
//                binding.btnSend.revertAnimation()
                sToast(data.msg)
                deleteFiles()
                back()
            }

            override fun onFailed(errorMessage: String) {
                d("onFailed ${errorMessage}-----------------", "Loading")
//                binding.btnSend.revertAnimation()
            }
        }, 1)
    }

    private fun mProvinceObserver() {
        baseObserver(
            this,
            mViewModel.mProvinceObserver,
            object : RequestConnectionResult<MutableList<ProvinceModel>> {
                override fun onProgress(loading: Boolean) {
                    d("loading ${loading}-----------------", "Loading")
                    setProgressView(mainView, loading)
                }

                override fun onSuccess(data: MutableList<ProvinceModel>) {
                    d("onSuccess -----------------", "Loading")
                    mViewModel.provinceList = data
                }

                override fun onFailed(errorMessage: String) {
                    mViewModel.getData()
//                binding.btnSend.revertAnimation()
                }
            },
            4
        )
    }


// ============================ use dynamic form ===========================================
/*
    private fun initializeFormValue() {

        mViewModel.formElements.clear()
        mViewModel.formElements.add(FormElement(1, 1, findString(R.string.nameHint2), null, 1, mViewModel.INPUT_TEXT, 100, 1, false))   //name
        mViewModel.formElements.add(FormElement(2, 1, findString(R.string.lastName), null, 1, mViewModel.INPUT_TEXT, 100, 1, false))   // jonName
        mViewModel.formElements.add(FormElement(3, 1, findString(R.string.phoneNumber), null, 1, mViewModel.INPUT_NUMBER, 11, 1, false))   // mobile Number
        mViewModel.formElements.add(FormElement(4, 1, findString(R.string.fixNumber), null, 1, mViewModel.INPUT_NUMBER, 11, 1, false))   // mobile Number
        mViewModel.formElements.add(FormElement(5, 1, findString(R.string.email), null, 1, mViewModel.INPUT_EMAIL, 50, 1, false))   // email Address
        mViewModel.formElements.add(FormElement(6, 1, findString(R.string.age), null, 1, mViewModel.INPUT_NUMBER, 50, 1, false))
        mViewModel.formElements.add(FormElement(7, 1, findString(R.string.province), null, 1, mViewModel.INPUT_TEXT, 50, 1, false))
        mViewModel.formElements.add(FormElement(8, 1, findString(R.string.city), null, 1, mViewModel.INPUT_TEXT, 50, 1, false))
        mViewModel.formElements.add(FormElement(9, 1, findString(R.string.situation), null, 1, mViewModel.INPUT_TEXT, 0, 1, false, true))   // job category
        mViewModel.formElements.add(FormElement(10, 1, findString(R.string.address), null, 1, mViewModel.INPUT_TEXT_MULTI_LINE, 500, 3, false))   // Full Address Detail
        mViewModel.formElements.add(FormElement(11, 1, findString(R.string.areaProperty), null, 1, mViewModel.INPUT_TEXT, 50, 1, false))
        mViewModel.formElements.add(FormElement(12, 1, findString(R.string.investmentValue), null, 1, mViewModel.INPUT_NUMBER, 50, 1, false))
        mViewModel.formElements.add(FormElement(13, 1, findString(R.string.yourCurrentJob), null, 1, mViewModel.INPUT_TEXT, 50, 1, false))
        mViewModel.formElements.add(FormElement(14, 1, findString(R.string.trainingCourse), null, 1, mViewModel.INPUT_TEXT, 50, 1, false))
        mViewModel.formElements.add(FormElement(15, 1, findString(R.string.monthlyTurnOver), null, 1, mViewModel.INPUT_NUMBER, 100, 1, false))
        mViewModel.formElements.add(FormElement(16, 1, findString(R.string.description), null, 1, mViewModel.INPUT_TEXT_MULTI_LINE, 500, 3, false))   // Full Address Detail

    }


    private fun generateForm() {
        //Form Elements
        for (position in 0 until mViewModel.formElements.size) {
            val formElement: FormElement = mViewModel.formElements[position]
            var txtInputL: TextInputLayout? = null
            var etxt: TextInputEditText? = null

            when (formElement.type) {
                //EditText
                1 -> {
                    val textViewHolder: View = inflater!!.inflate(R.layout.item_form_etxt, llFormContainer, false)

                    //Set Tag for Root Holder
                    textViewHolder.tag = "${formElement.id},${formElement.type}"

                    //Title
                    txtInputL = textViewHolder.findViewById(R.id.txtInputL)

                    //Input
                    etxt = textViewHolder.findViewById(R.id.etxtL)

                    txtInputL.hint = formElement.title


                    /*
                    etxt.onFocusChangeListener = object : View.OnFocusChangeListener{
                        override fun onFocusChange(v: View?, hasFocus: Boolean) {
                            if (hasFocus){
                                etxt.hint = formElement.title
                            }else{
                                etxt.hint = "test"
                            }
                        }

                    }*/

                    //Set Max Length
                    if (formElement.maxLength > 0) {
                        etxt.filters = arrayOf(InputFilter.LengthFilter(formElement.maxLength))
                    }

                    if (formElement.nullKeyListener) {
                        etxt.keyListener = null
                        etxt.isFocusable = false
                        etxt.isFocusableInTouchMode = false

                        etxt.setOnClickListener {
                            if (formElement.id == 6) {
                                navTo(R.id.categoryFragment)
                            } else if (formElement.id == 7) {
                                navTo(R.id.openHourFragment)

                            } else if (formElement.id == 8) {
                                val arg = extraArguments(
                                    argument1 = mViewModel.countryItem,
                                    key1 = ARG_STRING_1
                                )
                                navTo(R.id.countryFragment, arg = arg)

                            } else if (formElement.id == 10) {

                                val arg = extraArguments(
                                    argument1 = mViewModel.address,
                                    key1 = ARG_STRING_1
                                )
                                navTo(R.id.addressMapFragment, arg = arg)
                            } else if (formElement.id == 11) {
                                val arg = extraArguments(
                                    argument1 = SOCIAL_NETWORK,
                                    key1 = ARG_STRING_1
                                )
                                navTo(R.id.socialNetFragment, arg = arg)

                            } else if (formElement.id == 5) {
                                val arg = extraArguments(
                                    argument1 = JOB_NUMBER,
                                    key1 = ARG_STRING_1
                                )
                                navTo(R.id.socialNetFragment, arg = arg)
                            }

                            eToast("${formElement.title}  ${formElement.value}")
                        }
                    }
                    //Set Edit Text Type
                    when (formElement.subType) {
                        mViewModel.INPUT_TEXT ->
                            etxt.inputType = InputType.TYPE_CLASS_TEXT

                        mViewModel.INPUT_TEXT_MULTI_LINE -> {
                            etxt.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
                            etxt.isSingleLine = false
                        }

                        mViewModel.INPUT_PASSWORD -> {
                            etxt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        }

                        mViewModel.INPUT_EMAIL ->
                            etxt.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

                        mViewModel.INPUT_NUMBER ->
                            etxt.inputType = InputType.TYPE_CLASS_NUMBER

                    }

                    if (formElement.hasChev) {
                        etxt.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(), R.drawable.ic_play_arrow_24dp), null)

                    }

                    if (formElement.maxLine > 1) {
                        etxt.maxLines = formElement.maxLine
                    }

                    //Check Required
                    if (formElement.isRequired()) {

                        etxt.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (s.toString().trim().isEmpty() && mViewModel.btnSubmitHitted) {
                                    txtInputL.isErrorEnabled = true
                                    txtInputL.error = "Enter value please!"
                                } else {
                                    txtInputL.isErrorEnabled = false
                                    txtInputL.error = ""

                                    formElement.value = s.toString().trim()

                                }

                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }
                        })


                        if (formElement.value != null && !formElement.value!!.isEmpty()) {
                            etxt.setText(formElement.value)
                        }

                        //Set Tag for Input
                        etxt.tag = "${formElement.id},${formElement.type}"
                        //Set Input ID
                        etxt.id = formElement.id + formElement.type + mViewModel.parentId + position

                        //AddView
                        llFormContainer.addView(textViewHolder)
                    }
                }

            }
        }
    }

 */


}