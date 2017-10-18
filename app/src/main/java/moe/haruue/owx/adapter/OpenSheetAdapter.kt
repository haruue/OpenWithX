package moe.haruue.owx.adapter

import android.annotation.SuppressLint
import android.content.pm.ResolveInfo
import android.content.res.ColorStateList
import android.support.annotation.LayoutRes
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
        const val VT_HEADER = 1
        const val VT_SHARE = 2
        const val VT_RECOMMEND = 3
        const val VT_OTHERS = 4
        const val VT_LOADING_EXACT = 5
        const val VT_LOAD_ALL = 6
        const val VT_LOADING_ALL = 7
    }

    private var defaultOpenMethod = false

    private var shouldShowExactMatchesLoadingView = true
        set(value) {
            try {
                field = value
                notifyItemChanged(loadingExactMatchesPosition)
            } catch (e: Exception) {
                Log.e("OpenSheetAdapter", "shouldShowExactMatchesLoadingView@setter: $field", e)
            }
        }

    private var shouldShowAllMatchesLoadingView = false
        set(value) {
            try {
                field = value
                notifyItemChanged(loadingAllMatchesPosition)
            } catch (e: Exception) {
                Log.e("OpenSheetAdapter", "shouldShowAllMatchesLoadingView@setter: $field", e)
            }
        }

    private var shouldShowAllMatchesLoadMoreView = true
        set(value) {
            try {
                field = value
                notifyItemChanged(loadAllMatchesPosition)
            } catch (e: Exception) {
                Log.e("OpenSheetAdapter", "shouldShowAllMatchesLoadMoreView@setter: $field", e)
            }
        }

    private val exactMatchesCount
        get() = exactMatches.size

    private val allMatchesCount
        get() = allMatches.size

    private val headerPosition = 0

    private val loadingExactMatchesPosition = 1

    private val exactMatchesStartPosition = 2

    private val sharePosition
        get() = exactMatchesStartPosition + exactMatchesCount

    private val loadAllMatchesPosition
        get() = sharePosition + 1

    private val loadingAllMatchesPosition
        get() = loadAllMatchesPosition + 1

    private val allMatchesStartPosition
        get() = loadingAllMatchesPosition + 1

    private var exactMatches = mutableListOf<ResolveInfo>()
        set(value) {
            field.addAll(value)
            shouldShowExactMatchesLoadingView = false
            notifyItemRangeInserted(exactMatchesStartPosition, exactMatchesCount)
        }

    private var allMatches = mutableListOf<ResolveInfo>()
        set(value) {
            field.addAll(value)
            shouldShowAllMatchesLoadingView = false
            notifyItemRangeInserted(allMatchesStartPosition, allMatchesCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VT_HEADER -> HeaderViewHolder(parent)
            VT_SHARE -> ItemViewHolder(parent, viewType)
            VT_RECOMMEND -> ItemViewHolder(parent, viewType)
            VT_OTHERS -> ItemViewHolder(parent, viewType)
            VT_LOADING_EXACT -> LoadingViewHolder(parent, viewType)
            VT_LOAD_ALL -> ItemViewHolder(parent, viewType)
            VT_LOADING_ALL -> LoadingViewHolder(parent, viewType)
            else -> throw IllegalArgumentException("Unexpected view type $viewType in OpenSheetAdapter.")
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.defaultCheckBox.setOnCheckedChangeListener { _, isChecked -> defaultOpenMethod = isChecked }
            }
            is LoadingViewHolder -> {
                when (holder.viewType) {
                    VT_LOADING_EXACT -> {
                        if (shouldShowExactMatchesLoadingView) {
                            holder.itemView.visible = true
                            loadExactMatches {
                                this@OpenSheetAdapter.exactMatches = it.toMutableList()
                                shouldShowExactMatchesLoadingView = false
                            }
                        } else {
                            holder.itemView.visible = false
                        }
                    }
                    VT_LOADING_ALL -> {
                        if (shouldShowAllMatchesLoadingView) {
                            holder.itemView.visible = true
                            loadAllMatches {
                                this@OpenSheetAdapter.allMatches = it.toMutableList()
                                shouldShowAllMatchesLoadingView = false
                            }
                        } else {
                            holder.itemView.visible = false
                        }
                    }
                }
            }
            is ItemViewHolder -> {
                when (holder.viewType) {
                    VT_RECOMMEND -> {
                        val index = position - exactMatchesStartPosition
                        val pm = holder.itemView.context.packageManager
                        holder.icon.setImageDrawable(exactMatches[index].loadIcon(pm))
                        holder.name.text = exactMatches[index].loadLabel(pm)
                        holder.itemView.setOnClickListener {
                            startResolveInfo(exactMatches[index])
                        }
                    }
                    VT_SHARE -> {
                        val context = holder.itemView.context
                        val res = context.resources
                        holder.icon.setImageDrawable(VectorDrawableCompat.create(res, R.drawable.ic_send_black_24dp, null))
                        holder.icon.supportImageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                        holder.name.text = res.getText(R.string.share)
                        holder.itemView.setOnClickListener {
                            startShare()
                        }
                    }
                    VT_OTHERS -> {
                        val index = position - allMatchesStartPosition
                        val pm = holder.itemView.context.packageManager
                        holder.icon.setImageDrawable(allMatches[index].loadIcon(pm))
                        holder.name.text = allMatches[index].loadLabel(pm)
                        holder.itemView.setOnClickListener {
                            startResolveInfo(allMatches[index])
                        }
                    }
                    VT_LOAD_ALL -> {
                        if (shouldShowAllMatchesLoadMoreView) {
                            val context = holder.itemView.context
                            val res = context.resources
                            holder.icon.setImageDrawable(VectorDrawableCompat.create(res, R.drawable.ic_refresh_black_24dp, null))
                            holder.icon.supportImageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorAccent))
                            holder.name.text = res.getText(R.string.load_all)
                            holder.itemView.setOnClickListener {
                                shouldShowAllMatchesLoadMoreView = false
                                shouldShowAllMatchesLoadingView = true
                            }
                            holder.itemView.visible = true
                        } else {
                            holder.itemView.visible = false
                        }
                    }
                }
            }
            else -> throw IllegalArgumentException("Unexpected holder type ${holder::class.java.name} in OpenSheetAdapter.")
        }
    }

    override fun getItemCount(): Int {
        return allMatchesStartPosition + allMatchesCount
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == headerPosition -> VT_HEADER
            position == loadingExactMatchesPosition -> VT_LOADING_EXACT
            position >= exactMatchesStartPosition && position < exactMatchesStartPosition + exactMatchesCount -> VT_RECOMMEND
            position == sharePosition -> VT_SHARE
            position == 2 + exactMatchesCount + 1 -> VT_LOAD_ALL
            position == 2 + exactMatchesCount + 2 -> VT_LOADING_ALL
            else -> VT_OTHERS
        }
    }

    /**
     * Hide a [View] in a [RecyclerView]
     */
    private var View.visible: Boolean
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

    class HeaderViewHolder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.item_sheet_header, VT_HEADER) {
        val defaultCheckBox: CheckBox = itemView.findViewById(R.id.cb_default)
    }

    class ItemViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder(parent, R.layout.item_sheet_app, viewType) {
        val icon: AppCompatImageView = itemView.findViewById(R.id.iv_app_icon)
        val name: TextView = itemView.findViewById(R.id.tv_app_name)
    }

    class LoadingViewHolder(parent: ViewGroup, viewType: Int) : BaseViewHolder(parent, R.layout.item_sheet_loading, viewType)

}