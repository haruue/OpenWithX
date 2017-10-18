package moe.haruue.owx

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.activity_open.*
import moe.haruue.owx.adapter.OpenSheetAdapter

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenActivity : AppCompatActivity() {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<RecyclerView>

    private val exactMatches by lazy @SuppressLint("SdCardPath") {
        val targetIntent = Intent(Intent.ACTION_VIEW)
        targetIntent.setDataAndType(Uri.parse("file://*.*"), intent.type)
        packageManager.queryIntentActivities(targetIntent, PackageManager.MATCH_DEFAULT_ONLY)
                .filter { it.activityInfo.exported && it.activityInfo.packageName != packageName }
    }

    private val allMatches by lazy {
        val targetIntent = Intent(Intent.ACTION_VIEW)
        targetIntent.setDataAndType(Uri.parse("file://*.*"), "*/*")
        packageManager.queryIntentActivities(targetIntent, 0)
                .filter { it !in exactMatches && it.activityInfo.exported && it.activityInfo.packageName != packageName }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_open)
        list.layoutManager = LinearLayoutManager(this)
        bottomSheetBehavior = BottomSheetBehavior.from(list)
        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do nothing
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    finish()
                }
            }
        })
        val adapter = OpenSheetAdapter(this::loadExactMatches, this::loadAllMatches, this::startResolveInfo, this::startShare)
        list.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun startResolveInfo(info: ResolveInfo) {
        val targetIntent = Intent(intent)
        val packageName = info.activityInfo.packageName
        val className = info.activityInfo.name
        targetIntent.`package` = packageName
        targetIntent.setClassName(packageName, className)
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        }
        startActivity(targetIntent)
        finish()
    }

    private fun startShare() {
        val targetIntent = Intent()
        targetIntent.action = Intent.ACTION_SEND
        targetIntent.putExtra(Intent.EXTRA_STREAM, intent.data)
        targetIntent.type = intent.type
        val shareIntent = Intent.createChooser(targetIntent, resources.getText(R.string.share))
        startActivity(shareIntent)
        finish()
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

    override fun onBackPressed() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}