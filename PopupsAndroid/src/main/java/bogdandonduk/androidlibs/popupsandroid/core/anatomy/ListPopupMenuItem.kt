package bogdandonduk.androidlibs.popupsandroid.core.anatomy

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.ColorInt

class ListPopupMenuItem(
    var text: String,
    @ColorInt var textColor: Int,
    var icon: Drawable?,
    @ColorInt var iconTintColor: Int?,
    var onClickAction: ((view: View, popup: PopupWindow) -> Unit)?
)