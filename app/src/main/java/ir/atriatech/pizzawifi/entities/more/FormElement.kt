package ir.atriatech.pizzawifi.entities.more

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FormElement() {

    @SerializedName("id")
    @Expose
    var id = 0

    @SerializedName("type")
    @Expose
    var type = 0

    @Nullable
    @SerializedName("title")
    @Expose
    var title: String? = null

    @Nullable
    @SerializedName("value")
    @Expose
    var value: String? = null

    @SerializedName("required")
    @Expose
    var required = 0

    @SerializedName("subtype")
    @Expose
    var subType = 0

    @SerializedName("maxlength")
    @Expose
    var maxLength = 0

    @SerializedName("error")
    @Expose
    val error = false

    @SerializedName("msg")
    @Expose
    val msg: String? = null

    @SerializedName("hasChev")
    @Expose
    var hasChev = false

    @SerializedName("nullKeyListener")
    @Expose
    var nullKeyListener = false

    @SerializedName("maxLine")
    @Expose
    var maxLine: Int = 1


    fun isRequired(): Boolean {
        return required == 1
    }

    /**
     * Constructor Form Element
     *
     * @param id        Integer
     * @param type      Integer
     * @param title     String
     * @param value     String
     * @param options   JSONArray
     * @param required  Integer
     * @param subType   Integer
     * @param maxLength Integer
     */

    constructor(
        id: Int,
        type: Int,
        title: String,
        @Nullable value: String?,
        required: Int,
        subType: Int,
        maxLength: Int,
        maxLine: Int,
        hasChev: Boolean,
        nullKeyListener: Boolean = false
    ) : this(
    ) {
        this.id = id
        this.type = type
        this.title = title
        this.value = value
        this.required = required
        this.subType = subType
        this.maxLength = maxLength
        this.maxLine = maxLine
        this.hasChev = hasChev
        this.nullKeyListener = nullKeyListener
    }
}