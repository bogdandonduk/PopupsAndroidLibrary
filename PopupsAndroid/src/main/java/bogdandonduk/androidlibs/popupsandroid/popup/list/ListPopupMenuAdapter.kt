package bogdandonduk.androidlibs.popupsandroid.popup.list

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import bogdandonduk.androidlibs.commonpreferencesutilsandroid.VibrationUtils
import bogdandonduk.androidlibs.popupsandroid.databinding.LayoutPopupTextItemBinding
import bogdandonduk.androidlibs.popupsandroid.core.anatomy.ListPopupMenuItem
import bogdandonduk.androidlibs.popuputilsandroid.popup.PopupWindow
import bogdandonduk.androidlibs.viewbindingutilsandroid.ViewBinder

internal class ListPopupMenuAdapter(
    val items: MutableList<ListPopupMenuItem>,
    var hostActivity: Activity,
    var popup: PopupWindow
) : RecyclerView.Adapter<ListPopupMenuAdapter.ViewHolder>() {
    lateinit var hostRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        hostRecyclerView = recyclerView
    }

    inner class ViewHolder(
        override val viewBindingInitialization: () -> LayoutPopupTextItemBinding,
        override var viewBinding: LayoutPopupTextItemBinding? = viewBindingInitialization.invoke(),
    ) : RecyclerView.ViewHolder(viewBinding!!.root), ViewBinder<LayoutPopupTextItemBinding> {
        lateinit var item: ListPopupMenuItem

        init {
            getCurrentViewBinding().root.run {
                setOnClickListener {
                    item.onClickAction?.invoke(it, popup)
                }

                setOnLongClickListener {
                    VibrationUtils.vibrateOneShot(hostActivity, 50)

                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ) = ViewHolder(viewBindingInitialization = {
        LayoutPopupTextItemBinding.inflate(hostActivity.layoutInflater, parent, false)
    })

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.run {
            item = items[position]

            getCurrentViewBinding().run {
                if(item.icon != null) {
                    layoutPopupTextIconImageView.visibility = View.VISIBLE
                    layoutPopupTextIconImageView.setImageDrawable(item.icon)

                    if(item.iconTintColor != null) DrawableCompat.setTint(layoutPopupTextIconImageView.drawable, item.iconTintColor!!)
                }

                layoutPopupTextTextTextView.run {
                    setTextColor(item.textColor)
                    text = item.text

                    post {
                        if(height >= lineHeight * 2)
                            root.setPadding(50, 5, 75, 5)
                        else
                            root.setPadding(20, 5, 30, 5)
                    }
                }
            }
        }
    }

    override fun getItemCount() = items.size
}