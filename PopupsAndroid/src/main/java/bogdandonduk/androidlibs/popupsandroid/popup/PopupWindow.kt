package bogdandonduk.androidlibs.popuputilsandroid.popup

import android.view.View
import bogdandonduk.androidlibs.popupsandroid.core.base.BasePopupWindow
import bogdandonduk.androidlibs.popupsandroid.core.base.BaseTransientPopupWindow
import bogdandonduk.androidlibs.popupsandroid.core.compose.TransientPopupModel
import bogdandonduk.androidlibs.popupsandroid.core.compose.TransientPopupWindow
import bogdandonduk.androidlibs.popupsandroid.popup.PopupModel

@PublishedApi
internal class PopupWindow(
    contentView: View?,
    width: Int,
    height: Int,
    model: PopupModel
) : BaseTransientPopupWindow(contentView, width, height, model), TransientPopupWindow {
    override var popupWindow: BasePopupWindow = this
    override var transientPopupModel: TransientPopupModel = model
    var fakeDismissal = false
    var defaultAnimationStyle = animationStyle

    init {
        attachToPopupModel()
    }
}