package moe.haruue.owx.preference

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.AttributeSet
import moe.haruue.owx.BuildConfig
import moe.haruue.owx.R
import moe.shizuku.preference.PreferenceViewHolder
import moe.shizuku.preference.SwitchPreference

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class ActivityEnableSwitchPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        SwitchPreference(context, attrs, defStyleAttr, defStyleRes) {

    private val activityClassName: String?

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.ActivityEnableSwitchPreference, defStyleAttr, defStyleRes);
        activityClassName = a.getString(R.styleable.ActivityEnableSwitchPreference_activityClassName)
        a.recycle()
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)
        setOnPreferenceChangeListener { preference, newValue ->
            setActivityEnabledState(activityClassName, newValue as Boolean)
            true
        }
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, R.style.Preference_SwitchPreference_Material)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.switchPreferenceStyle)
    @Suppress("unused") constructor(context: Context) : this(context, null)

    override fun getDefaultValue(): Boolean {
        return getActivityEnabledState(activityClassName)
    }

    private fun getActivityEnabledState(className: String?): Boolean {
        if (className === null) {
            return false
        }
        val cn = ComponentName(BuildConfig.APPLICATION_ID, className)
        val settings = context.packageManager.getComponentEnabledSetting(cn)
        return settings in arrayOf(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
    }

    private fun setActivityEnabledState(className: String?, enabled: Boolean) {
        if (className === null) {
            return
        }
        val cn = ComponentName(BuildConfig.APPLICATION_ID, className)
        context.packageManager.setComponentEnabledSetting(cn,
                if (enabled) { PackageManager.COMPONENT_ENABLED_STATE_ENABLED } else { PackageManager.COMPONENT_ENABLED_STATE_DISABLED },
                PackageManager.DONT_KILL_APP)
    }

}