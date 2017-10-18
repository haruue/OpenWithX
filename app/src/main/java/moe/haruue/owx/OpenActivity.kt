package moe.haruue.owx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import moe.haruue.owx.sheet.OpenBottomSheetFragment

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenActivity : AppCompatActivity() {

    val fragment by lazy { OpenBottomSheetFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_open)

        fragment.show(supportFragmentManager, "OpenDialog")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}