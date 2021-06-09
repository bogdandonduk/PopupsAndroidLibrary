package bogdandonduk.androidlibs.popupsandroid.popup

import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import bogdandonduk.androidlibs.popupsandroid.core.anatomy.ListPopupMenuItem
import bogdandonduk.androidlibs.popupsandroid.core.base.BasePopupModel
import bogdandonduk.androidlibs.popupsandroid.core.compose.PositionedPopupModel
import bogdandonduk.androidlibs.popupsandroid.core.compose.StatefulPopup
import bogdandonduk.androidlibs.popupsandroid.core.compose.TransientPopupModel
import bogdandonduk.androidlibs.popupsandroid.core.config.DisplayMode

@PublishedApi
internal class PopupModel(
    var text: String,
    @ColorInt var textColor: Int,
    var icon: Drawable?,
    @ColorInt var iconTintColor: Int?,
    var onClickAction: ((view: View, popup: PopupWindow) -> Unit)?,

    var listMenuItems: MutableMap<String, ListPopupMenuItem> = mutableMapOf(),

    @ColorInt var backgroundColor: Int,

    var strokeWidth: Int,
    @ColorInt var strokeColor: Int,

    var cornerRadiusTopLeftPx: Int,
    var cornerRadiusTopRightPx: Int,
    var cornerRadiusBottomLeftPx: Int,
    var cornerRadiusBottomRightPx: Int,

    var alpha: Float,
    var outsideTouchable: Boolean,

    var onDismissAction: ((popup: PopupWindow) -> Unit)?,

    override var tag: String
) : BasePopupModel(tag), TransientPopupModel, PositionedPopupModel, StatefulPopup {
    override var showDuration: Long = 0

    @IdRes override var dropdownAnchorViewId: Int? = null
    override var dropdownGravity: Int = Gravity.NO_GRAVITY
    override var dropdownXOff: Int = 0
    override var dropdownYOff: Int = 0

    @IdRes override var locationParentViewId: Int? = null
    override var locationGravity: Int = Gravity.BOTTOM
    override var locationX: Int = 0
    override var locationY: Int = 0

    @IdRes var customAnimationStyleId: Int? = null

    var lastDisplayMode: DisplayMode? = null

    override var mainListState: Parcelable? = null
}