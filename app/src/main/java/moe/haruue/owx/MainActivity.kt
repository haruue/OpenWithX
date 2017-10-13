package moe.haruue.owx

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import moe.haruue.owx.settings.MainSettingsFragment

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainSettingsFragment())
                .commit()
    }
}