package moe.haruue.owx.sheet

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.sheet_open.*
import moe.haruue.owx.R
import moe.haruue.owx.adapter.OpenSheetAdapter

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenBottomSheetFragment : BottomSheetDialogFragment() {

    private val intent
        get() = activity.intent

    private val packageManager
        get() = activity.packageManager

    private val packageName
        get() = activity.packageName

    private fun finish() {
        activity.finish()
    }

    private fun runOnUiThread(r: () -> Unit) {
        activity.runOnUiThread(r)
    }

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
            = inflater.inflate(R.layout.sheet_open, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list.layoutManager = LinearLayoutManager(activity)
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

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        finish()
    }
}