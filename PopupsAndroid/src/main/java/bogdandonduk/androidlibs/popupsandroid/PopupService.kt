package bogdandonduk.androidlibs.popupsandroid

import android.app.Activity
import bogdandonduk.androidlibs.popupsandroid.core.config.DisplayMode
import bogdandonduk.androidlibs.popupsandroid.core.PopupTaggingUtils
import bogdandonduk.androidlibs.popupsandroid.popup.PopupBuilder
import bogdandonduk.androidlibs.popupsandroid.popup.PopupModel
import bogdandonduk.androidlibs.popuputilsandroid.popup.PopupWindow

object PopupService {
    const val DEFAULT_POPUP_CORNER_RADIUS = 1000000
    const val DEFAULT_POPUP_ALPHA = 0.9f

    const val DEFAULT_POPUP_SHOW_DURATION = 2000L

    internal val savedPopups = mutableMapOf<String, PopupModel>()

    @PublishedApi
    @Synchronized
    internal fun savePopupModel(tag: String, model: PopupModel) {
        savedPopups[tag] = model
    }

    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    @Synchronized
    internal fun getSavedPopupModel(tag: String) = savedPopups[tag]
    
    @Synchronized
    fun removeSavedPopupModel(tag: String, dismissPopup: Boolean = true) {
        if(dismissPopup) (savedPopups[tag]?.window as PopupWindow?)?.dismiss()

        savedPopups.remove(tag)
    }

    @Synchronized
    fun dismissShowingPopup(tag: String, deleteModel: Boolean = false) {
        (savedPopups[tag]?.window as PopupWindow?)?.dismiss()

        if(deleteModel) savedPopups.remove(tag)
    }

    @Synchronized
    fun removeAllSavedPopupModels(dismissPopups: Boolean = true) {
        savedPopups.run {
            if(dismissPopups)
                forEach {
                    (it.value.window as PopupWindow?)?.dismiss()
                }

            clear()
        }
    }

    @Synchronized
    fun dismissAllShowingPopups(deleteModels: Boolean = false) {
        savedPopups.run {
            forEach {
                (it.value.window as PopupWindow?)?.dismiss()
            }

            if(deleteModels) clear()
        }
    }

    @Synchronized
    fun continueAllShowingPopups(activity: Activity, saveModels: Boolean = false) {
        savedPopups.forEach {
            buildPopup(it.key).run {
                if(model.lastDisplayMode != null)
                    when(model.lastDisplayMode!!.name) {
                        DisplayMode.AS_LIST_MENU.name -> {
                            continueAsListMenu(activity, saveModel = saveModels)
                        }
                        DisplayMode.AS_TOAST.name -> {
                            continueAsToast(activity, saveModel = saveModels)
                        }
                        DisplayMode.AS_TOOLTIP.name -> {
                            continueAsTooltip(activity, saveModel = saveModels)
                        }
                    }
            }
        }
    }

    fun buildPopup(tag: String = PopupTaggingUtils.generateRandomTooltipPopupTag()) = PopupBuilder(tag)
}