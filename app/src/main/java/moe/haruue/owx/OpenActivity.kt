package moe.haruue.owx

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_open.*
import moe.haruue.owx.adapter.OpenSheetAdapter

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenActivity : AppCompatActivity() {

    private val exactMatches by lazy @SuppressLint("SdCardPath") {
        val queryIntent = Intent(Intent.ACTION_VIEW)
        queryIntent.setDataAndType(intent.data, intent.type)
        packageManager.queryIntentActivities(queryIntent, PackageManager.MATCH_DEFAULT_ONLY)
                .filter { it.activityInfo.exported && it.activityInfo.packageName != packageName }
    }

    private val allMatches by lazy {
        val queryIntent = Intent(Intent.ACTION_VIEW)
        queryIntent.setDataAndType(Uri.parse("file://*.*"), "*/*")
        packageManager.queryIntentActivities(queryIntent, 0)
                .filter { it !in exactMatches && it.activityInfo.exported && it.activityInfo.packageName != packageName }
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)

        contentPanel.setOnDismissedListener {
            finish()
            overridePendingTransition(0, 0)
        }

        list.layoutManager = LinearLayoutManager(this)
        val adapter = OpenSheetAdapter(this::loadExactMatches, this::loadAllMatches, this::startResolveInfo, this::startShare)
        list.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (!contentPanel.isCollapsed) {
            contentPanel.isCollapsed = true
        } else {
            super.onBackPressed()
        }
    }

}