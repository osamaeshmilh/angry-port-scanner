package osama.com.angryportscanner.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import osama.com.angryportscanner.R

class AppPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.app_preference_fragment, rootKey)
    }

}