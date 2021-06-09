package bogdandonduk.androidlibs.popupsandroid.core.compose

import bogdandonduk.androidlibs.popupsandroid.core.base.BasePopupWindow
import bogdandonduk.androidlibs.threadingutilsandroid.ThreadingUtils
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal interface TransientPopupWindow {
    companion object {
        const val KEY_DISMISSAL_TIMER = "key_dismissal_timer"
    }

    var popupWindow: BasePopupWindow
    var transientPopupModel: TransientPopupModel
    var dismissalTime: Long

    fun startDismissalTimer() {
        dismissalTime = transientPopupModel.showDuration + System.currentTimeMillis()

        if(ThreadingUtils.globalCoroutineScopeDefaultJobs[KEY_DISMISSAL_TIMER] == null) {
            ThreadingUtils.globalCoroutineScopeDefaultJobs[KEY_DISMISSAL_TIMER] = ThreadingUtils.globalCoroutineScopeDefault.launch {
                while(System.currentTimeMillis() < dismissalTime) {
                    transientPopupModel.showDuration = dismissalTime - System.currentTimeMillis()

                    delay(5)
                }

                transientPopupModel.showDuration = 0

                withContext(Main.immediate) {
                    popupWindow.dismiss()
                }
            }
        }

    }
}