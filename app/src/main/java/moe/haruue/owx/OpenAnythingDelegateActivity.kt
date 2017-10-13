package moe.haruue.owx

import android.app.Activity
import android.os.Bundle

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

class OpenAnythingDelegateActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            intent.setClass(applicationContext, OpenActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

}