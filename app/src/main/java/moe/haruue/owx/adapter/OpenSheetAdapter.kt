package moe.haruue.owx.adapter

import android.annotation.SuppressLint
import android.content.pm.ResolveInfo
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import moe.haruue.owx.R

/**
 *
 * @author Haruue Icymoon haruue@caoyue.com.cn
 */
class OpenSheetAdapter(
        private val loadExactMatches: (callback: (exactMatches: List<ResolveInfo>) -> Unit) -> Unit,
        private val loadAllMatches: (callback: (allMatches: List<ResolveInfo>) -> Unit) -> Unit,
        private val startResolveInfo: (resolveInfo: ResolveInfo) -> Unit,
        private val startShare: () -> Unit
) : RecyclerView.Adapter<OpenSheetAdapter.BaseViewHolder>() {

    companion object {
        const val VT_RECOMMEND = 3
        const val VT_OTHERS = 4
        const val VT_LOADING_ALL = 7
    }

    private var shouldShowAllMatchesLoadingView = true
        set(value) {
            try {
                field = value
                notifyItemChanged(loadingAllMatchesPosition)
            } catch (e: Exception) {
                Log.e("OpenSheetAdapter", "shouldShowAllMatchesLoadingView@setter: $field", e)
            }
        }

    private inline val exactMatchesCount
        get() = exactMatches.size + 1 // because we use share as the first matched item

    private inline val exactMatchesViewCount
        get() = Math.ceil(exactMatchesCount.toDouble() / 4.0).toInt()

    private inline val allMatchesCount
        get() = allMatches.size

    private inline val allMatchesViewCount
        get() = Math.ceil(allMatchesCount.toDouble() / 4.0).toInt()

    private val exactMatchesStartPosition = 0

    private inline val loadingAllMatchesPosition
        get() = exactMatchesStartPosition + exactMatchesViewCount

    private inline val allMatchesStartPosition
        get() = loadingAllMatchesPosition + 1

    private var exactMatches = mutableListOf<ResolveInfo>()
        set(value) {
            field.addAll(value)
            Log.d("OpenSheetAdapter", "exactMatches\$setter: length=${field.size}")
            notifyItemChanged(exactMatchesStartPosition)
            if (exactMatchesCount > 1) {
                notifyItemRangeInserted(exactMatchesStartPosition + 1, exactMatchesViewCount - 1)
            }
        }

    private var allMatches = mutableListOf<ResolveInfo>()
        set(value) {
            field.addAll(value)
            Log.d("OpenSheetAdapter", "allMatches\$setter: length=${field.size}")
            shouldShowAllMatchesLoadingView = false
            notifyItemRangeInserted(allMatchesStartPosition, allMatchesViewCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VT_RECOMMEND -> RowViewHolder(parent, viewType)
            VT_OTHERS -> RowViewHolder(parent, viewType)
            VT_LOADING_ALL -> LoadingViewHolder(parent, viewType)
            else -> throw IllegalArgumentException("Unexpected view type $viewType in OpenSheetAdapter.")
        }
    }

    private var whatLoading = 0
        set(value) {
            field = value
            when (value) {
                VT_RECOMMEND -> {
                    loadExactMatches {
                        this@OpenSheetAdapter.exactMatches = it.toMutableList()
                        whatLoading = VT_OTHERS
                    }
                }
                VT_OTHERS -> {
                    loadAllMatches {
                        this@OpenSheetAdapter.allMatches = it.toMutableList()
                        whatLoading = -1
                        shouldShowAllMatchesLoadingView = false
                    }
                }
            }
        }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        Log.d("OpenSheetAdapter", "onBindViewHolder: position=$position")
        when (holder) {
            is LoadingViewHolder -> {
                when (holder.viewType) {
                    VT_LOADING_ALL -> {
                        if (shouldShowAllMatchesLoadingView) {
                            holder.itemView.visible = true
                            if (whatLoading == 0) {
                                whatLoading = VT_RECOMMEND
                            }
                        } else {
                            holder.itemView.visible = false
                        }
                    }
                    else -> throw IllegalArgumentException("Unexpected view type ${holder.viewType} in OpenSheetAdapter.")
                }
            }
            is RowViewHolder -> {
                val rowArray = mutableListOf<ResolveInfo?>()
                if (position == exactMatchesStartPosition) {
                    // null for share
                    rowArray.add(null)
                }
                for (i in rowArray.size..3) {
                    when (holder.viewType) {
                        VT_RECOMMEND -> {
                            val row = position - exactMatchesStartPosition
                            /*
                                        0   1   2   3   i
                                    -------------------
                                0   |   s   0   1   2
                                1   |   3   4   5   6
                                2   |   7   8   9   10
                              row   |                   value
                             */
                            val index = row * 4 + i - 1
                            if (index < exactMatches.size) {
                                rowArray.add(exactMatches[index])
                            }
                        }
                        VT_OTHERS -> {
                            val row = position - allMatchesStartPosition
                            val index = row * 4 + i
                            if (index < allMatches.size) {
                                rowArray.add(allMatches[index])
                            }
                        }
                        else -> throw IllegalArgumentException("Unexpected view type ${holder.viewType} in OpenSheetAdapter.")
                    }
                    holder.onBind(rowArray, startResolveInfo, startShare)
                }
            }
            else -> throw IllegalArgumentException("Unexpected holder type ${holder::class.java.name} in OpenSheetAdapter.")
        }
    }

    override fun getItemCount(): Int {
        Log.d("OpenSheetAdapter", "getItemCount: \n" +
                "exactMatchesStartPosition=$exactMatchesStartPosition\n" +
                "exactMatchesViewCount=$exactMatchesViewCount,\n" +
                "loadingAllMatchesPosition=$loadingAllMatchesPosition\n" +
                "allMatchesStartPosition=$allMatchesStartPosition\n" +
                "allMatchesViewCount=$allMatchesViewCount")
        Log.d("OpenSheetAdapter", "getItemCount: count=${exactMatchesViewCount + 1 + allMatchesViewCount}")
        return exactMatchesViewCount + 1 + allMatchesViewCount
    }

    override fun getItemViewType(position: Int): Int {
        val type = when {
            position >= exactMatchesStartPosition && position < exactMatchesStartPosition + exactMatchesViewCount -> VT_RECOMMEND
            position == loadingAllMatchesPosition -> VT_LOADING_ALL
            else -> VT_OTHERS
        }
        Log.d("OpenSheetAdapter", "getItemViewType: position=$position, type=$type")
        return type
    }

    /**
     * Hide a [View] in a [RecyclerView]
     */
    private inline var View.visible: Boolean
        get() {
            return this.visibility == View.VISIBLE
        }
        set(value) {
            val params = layoutParams
            if (value) {
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                visibility = View.VISIBLE
            } else {
                params.height = 0
                params.width = 0
                visibility = View.VISIBLE
            }
            layoutParams = params
        }

    open class BaseViewHolder(itemView: View, val viewType: Int) : RecyclerView.ViewHolder(itemView) {
        constructor(parent: ViewGroup, @LayoutRes layout: Int, viewType: Int)
                : this(LayoutInflater.from(parent.context).inflate(layout, parent, false), viewType)
    }

    class RowViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder(parent, R.layout.row_sheet_app, viewType) {

        init {
            if (viewType == VT_RECOMMEND) {
                itemView.setBackgroundColor(ContextCompat.getColor(parent.context, R.color.material_gray_200))
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(parent.context, R.color.material_white))
            }
        }

        class Item(val itemView: View) {
            constructor(parent: View, @IdRes id: Int): this(parent.findViewById(id))
            val icon by lazy { itemView.findViewById<AppCompatImageView>(android.R.id.icon) }
            val name by lazy { itemView.findViewById<TextView>(android.R.id.text1) }
            val app by lazy { itemView.findViewById<TextView>(android.R.id.text2) }

            inline var visibility : Int
                get() = itemView.visibility
                set(value) { itemView.visibility = value }
        }

        val items by lazy {
            with(itemView) {
                listOf(Item(itemView, R.id.item1),
                       Item(itemView, R.id.item2),
                       Item(itemView, R.id.item3),
                       Item(itemView, R.id.item4))
            }
        }

        fun onBind(rowArray: List<ResolveInfo?>,
                   startResolveInfo: (resolveInfo: ResolveInfo) -> Unit,
                   startShare: () -> Unit) {
            for (i in 0..3) {
                if (i >= rowArray.size) {
                    items[i].visibility = View.INVISIBLE
                    return
                }
                val context = itemView.context
                if (rowArray[i] == null) {
                    // share
                    items[i].icon.setImageDrawable(context.getDrawable(R.drawable.ic_send_black_24dp))
                    items[i].name.text = context.getText(R.string.share)
                    items[i].app.visibility = View.GONE
                    items[i].itemView.setOnClickListener { startShare() }
                    items[i].visibility = View.VISIBLE
                    return
                }
                val pm = context.packageManager
                val info = rowArray[i]!!
                items[i].icon.setImageDrawable(info.loadIcon(pm))
                items[i].name.text = info.loadLabel(pm)
                items[i].app.text = pm.getApplicationLabel(info.activityInfo.applicationInfo)
                items[i].app.visibility = View.VISIBLE
                items[i].itemView.setOnClickListener { startResolveInfo(info) }
                items[i].visibility = View.VISIBLE
            }

        }

    }

    class LoadingViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder(parent, R.layout.item_sheet_loading, viewType)

}