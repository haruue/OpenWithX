package moe.haruue.owx.settings

import android.content.Context
import android.os.Bundle
import moe.haruue.owx.R
import moe.shizuku.preference.PreferenceFragment

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class MainSettingsFragment : PreferenceFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        preferenceManager.sharedPreferencesMode = Context.MODE_PRIVATE
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onCreateItemDecoration(): DividerDecoration? {
        return DefaultDividerDecoration()
    }

}