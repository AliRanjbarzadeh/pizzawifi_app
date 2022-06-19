package ir.atriatech.pizzawifi.ui.main.profile.editinfo


import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import ir.atriatech.pizzawifi.R
import kotlinx.android.synthetic.main.dialog_fragment_birthday.*


/**
 * A simple [Fragment] subclass.
 */
class DatePickerDialogFragment : DialogFragment() {
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialog = super.onCreateDialog(savedInstanceState)
		dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
		return dialog
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_TITLE, R.style.MyDialog)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.dialog_fragment_birthday, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btnSave?.setOnClickListener {
			val intent = Intent()
			intent.putExtra("date", datePicker.displayPersianDate.persianShortDate)
			targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
			dismiss()
		}

		btnCancel?.setOnClickListener {
			dismiss()
		}
	}
}
