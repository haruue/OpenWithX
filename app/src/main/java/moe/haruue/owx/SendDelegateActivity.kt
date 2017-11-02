package moe.haruue.owx

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class SendDelegateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent != null) {
            val openIntent = Intent(intent)
            openIntent.action = Intent.ACTION_VIEW
            openIntent.setClass(applicationContext, OpenActivity::class.java)
            openIntent.data = openIntent.getParcelableExtra(Intent.EXTRA_STREAM)
            if (openIntent.data != null) {
                startActivity(openIntent)
            } else {
                Toast.makeText(applicationContext, R.string.file_share_only, Toast.LENGTH_SHORT).show()
                // clear package and component info before re-share
                intent.`package` = null
                intent.component = null
                startActivity(intent)
            }
        }
        finish()
    }

}