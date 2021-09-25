package osama.com.angryportscanner.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.dialog_filter.view.*
import osama.com.angryportscanner.R

private const val SORT_BY = "sort_by"
private const val GROUP_BY = "group_by"

class FilterDialog : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.filterDialogStyle);
    }

    private var listener: FilterDialogListener? = null

    fun setOnApplyListener(onApply: (bundle: Bundle) -> Unit) {
        listener = object : FilterDialogListener {
            override fun onApply(bundle: Bundle) {
                onApply(bundle)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.dialog_filter, container, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)


        val sortByOptions = resources.getStringArray(R.array.sort_by).toList()
        val groupByOptions = resources.getStringArray(R.array.group_by).toList()

        view.sortBySpinner.setSelection(
            sortByOptions.indexOf(
                prefs.getString(
                    SORT_BY,

                    sortByOptions.first()
                )
            )
        )

        view.groupBySpinner.setSelection(
            groupByOptions.indexOf(
                prefs.getString(
                    GROUP_BY,
                    groupByOptions.first()
                )
            )
        )


        view.applyBtn.setOnClickListener {
            val bundle = Bundle().apply {
                putString(SORT_BY, view.sortBySpinner.selectedItem.toString())
                putString(GROUP_BY, view.groupBySpinner.selectedItem.toString())
            }
            prefs.edit().run {
                bundle.keySet().forEach { key ->
                    putString(key, bundle.getString(key))
                }
                commit()
            }
            listener?.onApply(bundle)
            dismiss()
        }

        view.cancelBtn.setOnClickListener {
            dismiss()
        }
        return view
    }
}

interface FilterDialogListener {
    fun onApply(bundle: Bundle)
}