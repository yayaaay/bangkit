package amirlabs.sapasemua.utils

import amirlabs.sapasemua.R
import amirlabs.sapasemua.databinding.LayoutAlertDialogBinding
import amirlabs.sapasemua.databinding.LayoutScheduleDialogBinding
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

object DialogUtils {
    private lateinit var builder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    fun showAlertDialog(
        context: Context,
        title: String,
        positiveActionText: String = "OK",
        negativeActionText: String = "Batalkan",
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null,
        autoDismiss: Boolean = false
    ) {
        val view: LayoutAlertDialogBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_alert_dialog, null as ViewGroup?, false)

        view.tvTitle.text = title
        view.btnPositive.text = positiveActionText
        view.btnPositive.setOnClickListener {
            dialog.dismiss()
            positiveAction?.invoke()
        }
        view.btnNegative.text = negativeActionText
        view.btnNegative.setOnClickListener {
            dialog.dismiss()
            negativeAction?.invoke()
        }
        builder = AlertDialog.Builder(context)
        builder.setView(view.root)
        builder.setCancelable(autoDismiss)
        dialog = builder.create()
        dialog.show()
    }


    @SuppressLint("SetTextI18n")
    fun showAddModuleDialog(
        context: Context,
        saveButtonClicked: (String?, Int?) -> Unit,
        autoDismiss: Boolean = false
    ) {
        val view: LayoutScheduleDialogBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_schedule_dialog, null as ViewGroup?, false)

        view.btnClose.setOnClickListener {
            dialog.dismiss()
        }

        view.etTitle.addTextChangedListener {
            view.btnSave.isEnabled = (view.etTitle.text?.isNotEmpty() == true
                    && view.etDuration.text?.isNotEmpty() == true)
        }
        view.etDuration.addTextChangedListener {
            view.btnSave.isEnabled = (view.etTitle.text?.isNotEmpty() == true
                    && view.etDuration.text?.isNotEmpty() == true)
        }
        view.btnSave.setOnClickListener {
            dialog.dismiss()
            val title = if(view.etTitle.text == null) "" else view.etTitle.text.toString()
            val duration = if(view.etDuration.text == null) -1 else view.etDuration.text.toString().toInt()
            saveButtonClicked(title, duration)
        }

        builder = AlertDialog.Builder(context)
        builder.setView(view.root)
        builder.setCancelable(autoDismiss)
        dialog = builder.create()
        dialog.show()
    }

}
