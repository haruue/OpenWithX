package moe.haruue.owx

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_open.*
import moe.haruue.owx.adapter.OpenSheetAdapter

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenActivity : AppCompatActivity() {

    private val exactMatches by lazy {
        val targetIntent = Intent(Intent.ACTION_VIEW)
        targetIntent.type = intent.type
        packageManager.queryIntentActivities(targetIntent, PackageManager.MATCH_DEFAULT_ONLY)
                .filter { it.resolvePackageName != packageName }
    }

    private val allMatches by lazy {
        val targetIntent = Intent(Intent.ACTION_VIEW)
        packageManager.queryIntentActivities(targetIntent, 0)
                .filter { it.resolvePackageName != packageName && it !in exactMatches }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        list.layoutManager = LinearLayoutManager(this)
        val adapter = OpenSheetAdapter(this::loadExactMatches, this::loadAllMatches)
        list.adapter = adapter
        adapter.notifyDataSetChanged()
        BottomSheetBehavior.from(list).setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do nothing
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                }
            }
        })
    }

    private fun loadExactMatches(callback: (exactMatches: List<ResolveInfo>) -> Unit) {
        Thread {
            val t = exactMatches
            runOnUiThread { callback(t) }
        }.start()
    }

    private fun loadAllMatches(callback: (allMatches: List<ResolveInfo>) -> Unit) {
        Thread {
            val t = allMatches
            runOnUiThread { callback(t) }
        }.start()
    }

}