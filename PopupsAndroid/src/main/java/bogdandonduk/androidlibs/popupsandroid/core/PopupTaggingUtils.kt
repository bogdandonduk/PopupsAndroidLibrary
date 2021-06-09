package bogdandonduk.androidlibs.popupsandroid.core

import bogdandonduk.androidlibs.popupsandroid.PopupService
import kotlin.random.Random

object PopupTaggingUtils {
    @PublishedApi internal val transientTagRegistry = mutableListOf<String>()

    fun generateRandomTooltipPopupTag() : String {
        var tag = "tooltip_popup_${Random.nextInt(0, 100000000)}"

        while(PopupService.savedPopups.containsKey(tag) || transientTagRegistry.contains(tag)) {
            tag = "tooltip_popup_${Random.nextInt(0, 100000000)}"
        }

        return tag
    }
}