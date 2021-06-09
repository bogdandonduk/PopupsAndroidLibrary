package bogdandonduk.androidlibs.popupsandroid.core.base

import android.view.View
import bogdandonduk.androidlibs.popupsandroid.core.compose.TransientPopupWindow

internal abstract class BaseTransientPopupWindow(
    contentView: View? = null,
    width: Int, height: Int,
    override var baseModel: BasePopupModel
) : BasePopupWindow(contentView, width, height, baseModel), TransientPopupWindow {
    override var dismissalTime: Long = 0
}