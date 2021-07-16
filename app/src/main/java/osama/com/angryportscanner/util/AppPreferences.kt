package osama.com.angryportscanner.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.view.View
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager

class AppPreferences {
    val context: Context
    private val resources: Resources
    private val preferences: SharedPreferences

    constructor(context: Context, resources: Resources) {
        this.context = context
        this.resources = resources
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    constructor(view: View) : this(view.context, view.resources)

    constructor(fragment: Fragment) : this(fragment.requireContext(), fragment.resources)

    val hideMacDetails
        get(): Boolean {
            return preferences.getBoolean("hideMacDetails", false)
        }
}