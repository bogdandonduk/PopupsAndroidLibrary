package bogdandonduk.androidlibs.popupsandroid.core.base

import android.view.View
import android.widget.PopupWindow
import bogdandonduk.androidlibs.popupsandroid.core.base.BasePopupModel

internal abstract class BasePopupWindow(
    contentView: View? = null,
    width: Int, height: Int,
    open var baseModel: BasePopupModel
) : PopupWindow(contentView, width, height) {
    fun attachToPopupModel() {
        baseModel.window = this
    }

    fun detachFromPopupModel() {
        baseModel.window = null
    }
}