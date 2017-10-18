package moe.haruue.owx

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.util.Log

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        disableFileUriExposedException()
    }

    private fun disableFileUriExposedException() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                val disableDeathOnFileUriExposure = StrictMode::class.java.getDeclaredMethod("disableDeathOnFileUriExposure")
                disableDeathOnFileUriExposure.isAccessible = true
                disableDeathOnFileUriExposure.invoke(null)
            } catch (e: Exception) {
                Log.e("FileUriExposure", "Can't disable death on file uri exposure", e)
            }
        }
    }
}