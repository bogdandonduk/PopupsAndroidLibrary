package bogdandonduk.androidlibs.popupsandroid.popup

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import bogdandonduk.androidlibs.commonpreferencesutilsandroid.GraphicsUtils
import bogdandonduk.androidlibs.popupsandroid.PopupService
import bogdandonduk.androidlibs.popupsandroid.R
import bogdandonduk.androidlibs.popupsandroid.core.PopupTaggingUtils
import bogdandonduk.androidlibs.popupsandroid.core.anatomy.ListPopupMenuItem
import bogdandonduk.androidlibs.popupsandroid.core.compose.TransientPopupWindow
import bogdandonduk.androidlibs.popupsandroid.databinding.LayoutPopupBinding
import bogdandonduk.androidlibs.popupsandroid.core.config.DisplayMode
import bogdandonduk.androidlibs.popupsandroid.core.config.QueueMode
import bogdandonduk.androidlibs.popupsandroid.popup.list.ListPopupMenuAdapter
import bogdandonduk.androidlibs.threadingutilsandroid.ThreadingUtils
import top.defaults.drawabletoolbox.DrawableBuilder
import bogdandonduk.androidlibs.popuputilsandroid.popup.PopupWindow as Popup

class PopupBuilder internal constructor(tag: String) {
    @PublishedApi
    internal companion object {
        const val LOG_MESSAGE_POPUP_ALREADY_SHOWING = "Old ShortMessagePopup with this tag is already showing and new one does nothing because flag DisplayMode.CONTINUE_OLD_IF_SHOWING is set"
    }

    @PublishedApi
    internal var model =
        if(PopupService.getSavedPopupModel(tag) != null) {
            val savedModel = PopupService.getSavedPopupModel(tag)!!

            PopupModel(
                text = savedModel.text,
                textColor = savedModel.textColor,

                icon = null,
                iconTintColor = null,
                onClickAction = null,

                backgroundColor = savedModel.backgroundColor,
                strokeWidth = savedModel.strokeWidth,
                strokeColor = savedModel.strokeColor,

                cornerRadiusTopLeftPx = savedModel.cornerRadiusTopLeftPx,
                cornerRadiusTopRightPx = savedModel.cornerRadiusTopRightPx,
                cornerRadiusBottomLeftPx = savedModel.cornerRadiusBottomLeftPx,
                cornerRadiusBottomRightPx = savedModel.cornerRadiusBottomRightPx,

                alpha = savedModel.alpha,
                outsideTouchable = savedModel.outsideTouchable,

                onDismissAction = savedModel.onDismissAction,

                tag = tag
            ).apply {
                showDuration = savedModel.showDuration

                dropdownAnchorViewId = savedModel.dropdownAnchorViewId
                dropdownGravity = savedModel.dropdownGravity
                dropdownXOff = savedModel.dropdownXOff
                dropdownYOff = savedModel.dropdownYOff

                locationParentViewId = savedModel.locationParentViewId
                locationGravity = savedModel.locationGravity
                locationX = savedModel.locationX
                locationY = savedModel.locationY

                customAnimationStyleId = savedModel.customAnimationStyleId

                locationGravity = savedModel.locationGravity
            }
        } else {
            PopupTaggingUtils.transientTagRegistry.add(tag)

            PopupModel(
                text = "Text",
                textColor = Color.WHITE,

                icon = null,
                iconTintColor = null,
                onClickAction = null,

                backgroundColor = Color.BLACK,
                strokeWidth = 1,
                strokeColor = Color.WHITE,

                cornerRadiusTopLeftPx = PopupService.DEFAULT_POPUP_CORNER_RADIUS,
                cornerRadiusTopRightPx = PopupService.DEFAULT_POPUP_CORNER_RADIUS,
                cornerRadiusBottomLeftPx = PopupService.DEFAULT_POPUP_CORNER_RADIUS,
                cornerRadiusBottomRightPx = PopupService.DEFAULT_POPUP_CORNER_RADIUS,

                alpha = PopupService.DEFAULT_POPUP_ALPHA,
                outsideTouchable = true,

                onDismissAction = null,

                tag = tag
            )
        }

    private fun showAtLocation(
        activity: Activity,
        @IdRes parentViewId: Int,
        displayMode: DisplayMode,
        showDuration: Long = PopupService.DEFAULT_POPUP_SHOW_DURATION,
        queueMode: QueueMode = QueueMode.CONTINUE_OLD_IF_SHOWING,
        saveModel: Boolean = false,
        gravity: Int = Gravity.BOTTOM,
        x: Int = 0,
        y: Int = 0
    ) =
        if(queueMode.name == QueueMode.DISPLAY_NEW.name ||
            PopupService.getSavedPopupModel(model.tag) == null ||
            PopupService.getSavedPopupModel(model.tag)!!.window == null
        ) {
            if(queueMode.name == QueueMode.DISPLAY_NEW.name)
                PopupService.removeSavedPopupModel(model.tag)

            model.showDuration = showDuration

            model.locationParentViewId = parentViewId
            model.locationGravity = gravity
            model.locationX = x
            model.locationY = y

            model.lastDisplayMode = displayMode

            save()

            Popup(null, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, PopupService.getSavedPopupModel(model.tag)!!).run {
                if(model.lastDisplayMode!!.name == DisplayMode.AS_LIST_MENU.name)
                    setupListMenuPopup(
                        activity,
                        this,
                        saveModel
                    )
                else
                    setupShortMessagePopup(
                        activity,
                        this,
                        saveModel
                    )

                isClippingEnabled = true

                isOutsideTouchable = false
                isTouchable = model.onClickAction != null

                animationStyle = model.customAnimationStyleId ?: R.style.ToastPopupAnimationStyle

                showAtLocation(activity.findViewById(model.locationParentViewId!!), model.locationGravity, model.locationX, model.locationY)
            }

            PopupTaggingUtils.transientTagRegistry.remove(model.tag)

            getTag()
        } else {
            Log.d(this::class.java.name, LOG_MESSAGE_POPUP_ALREADY_SHOWING)

            getTag()
        }

    private fun continueAtLocation(
        activity: Activity,
        @IdRes newParentViewId: Int? = null,
        newGravity: Int? = null,
        newX: Int? = null,
        newY: Int? = null,
        saveModel: Boolean = false,
        newShowDuration: Long? = null
    ) {
        PopupService.getSavedPopupModel(model.tag)?.run {
            window?.dismiss()

            if(model.lastDisplayMode != null && model.locationParentViewId != null)
                showAtLocation(
                    activity = activity,
                    displayMode = model.lastDisplayMode!!,
                    parentViewId = newParentViewId ?: model.locationParentViewId!!,
                    showDuration = newShowDuration ?: model.showDuration,
                    queueMode = QueueMode.DISPLAY_NEW,
                    saveModel = saveModel,
                    gravity = newGravity ?: model.locationGravity,
                    x = newX ?: model.locationX,
                    y = newY ?: model.locationY
                )
        }
    }

    private fun showAsDropDown(
        activity: Activity,
        @IdRes anchorViewId: Int,
        displayMode: DisplayMode,
        showDuration: Long = PopupService.DEFAULT_POPUP_SHOW_DURATION,
        queueMode: QueueMode = QueueMode.CONTINUE_OLD_IF_SHOWING,
        saveModel: Boolean = false,
        gravity: Int = Gravity.NO_GRAVITY,
        xOff: Int = 0,
        yOff: Int = 10
    ) =
        if(queueMode.name == QueueMode.DISPLAY_NEW.name ||
            PopupService.getSavedPopupModel(model.tag) == null ||
            PopupService.getSavedPopupModel(model.tag)!!.window == null
        ) {
            if(queueMode.name == QueueMode.DISPLAY_NEW.name)
                PopupService.removeSavedPopupModel(model.tag)

            model.showDuration = showDuration

            model.dropdownAnchorViewId = anchorViewId
            model.dropdownGravity = gravity
            model.dropdownXOff = xOff
            model.dropdownYOff = yOff

            model.lastDisplayMode = displayMode

            save()

            Popup(null, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, PopupService.getSavedPopupModel(model.tag)!!).run {
                if(model.lastDisplayMode!!.name == DisplayMode.AS_LIST_MENU.name)
                    setupListMenuPopup(
                        activity,
                        this,
                        saveModel
                    )
                else
                    setupShortMessagePopup(
                        activity,
                        this,
                        saveModel
                    )

                isOutsideTouchable = model.outsideTouchable
                isTouchable = model.onClickAction != null

                animationStyle = model.customAnimationStyleId ?: defaultAnimationStyle

                isClippingEnabled = false

                contentView.rootView.alpha = 0f

                val anchorView = activity.findViewById<View>(model.dropdownAnchorViewId!!)

                showAsDropDown(anchorView)

                contentView.post {
                    val contentViewHeight = contentView.height

                    fakeDismissal = true
                    dismiss()
                    fakeDismissal = false

                    val displayMetrics = DisplayMetrics()

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                        activity.display?.getMetrics(displayMetrics)
                    else
                        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

                    val anchorBottom: Int

                    IntArray(2).run {
                        anchorView.getLocationInWindow(this)

                        anchorBottom = this[1] + anchorView.height
                    }

                    contentView.rootView.alpha = model.alpha

                    isClippingEnabled = true

                    val requiredSpace = anchorBottom + contentViewHeight + model.dropdownYOff /*+ activity.resources.getIdentifier("navigation_bar_height", "dimen", "android")*/

                    showAsDropDown(anchorView, model.dropdownXOff, if(requiredSpace > displayMetrics.heightPixels) -(contentViewHeight + anchorView.height + yOff) else model.dropdownYOff, model.dropdownGravity)
                }
            }

            PopupTaggingUtils.transientTagRegistry.remove(model.tag)

            getTag()
        } else {
            Log.d(this::class.java.name, LOG_MESSAGE_POPUP_ALREADY_SHOWING)

            getTag()
        }

    private fun continueAsDropDown(
        activity: Activity,
        @IdRes newAnchorViewId: Int? = null,
        newShowDuration: Long? = null,
        saveModel: Boolean = false,
        newGravity: Int? = null,
        newXOff: Int? = null,
        newYOff: Int? = null
    ) {
        PopupService.getSavedPopupModel(model.tag)?.run {
            window?.dismiss()

            if(model.lastDisplayMode != null && model.dropdownAnchorViewId != null)
                showAsDropDown(
                    activity,
                    displayMode = model.lastDisplayMode!!,
                    anchorViewId = newAnchorViewId ?: model.dropdownAnchorViewId!!,
                    showDuration = newShowDuration ?: model.showDuration,
                    queueMode = QueueMode.DISPLAY_NEW,
                    saveModel = saveModel,
                    gravity = newGravity ?: model.dropdownGravity,
                    xOff = newXOff ?: model.dropdownXOff,
                    yOff = newYOff ?: model.dropdownYOff
                )
        }
    }

    fun showAsToast(
        activity: Activity,
        @IdRes parentViewId: Int,
        showDuration: Long = PopupService.DEFAULT_POPUP_SHOW_DURATION,
        queueMode: QueueMode = QueueMode.CONTINUE_OLD_IF_SHOWING,
        gravity: Int = Gravity.BOTTOM,
        saveModel: Boolean = false
    ) {
        showAtLocation(
            activity = activity,
            parentViewId = parentViewId,
            displayMode = DisplayMode.AS_TOAST,
            showDuration = showDuration,
            queueMode = queueMode,
            saveModel = saveModel,
            gravity = gravity,
            y = 150
        )

        PopupService.savedPopups[model.tag]?.run {
            (window as TransientPopupWindow?)?.startDismissalTimer()
        }
    }

    fun continueAsToast(
        activity: Activity,
        @IdRes newParentViewId: Int? = null,
        newGravity: Int? = null,
        newShowDuration: Long? = null,
        saveModel: Boolean = false
    ) {
        PopupService.getSavedPopupModel(model.tag)?.run {
            window?.dismiss()

            if(model.showDuration > 0 && model.locationParentViewId != null)
                showAsToast(
                    activity = activity,
                    parentViewId = newParentViewId ?: model.locationParentViewId!!,
                    showDuration = newShowDuration ?: model.showDuration,
                    queueMode = QueueMode.DISPLAY_NEW,
                    gravity = newGravity ?: model.locationGravity,
                    saveModel = saveModel
                )
        }
    }

    fun showAsTooltip(
        activity: Activity,
        @IdRes anchorViewId: Int,
        showDuration: Long = PopupService.DEFAULT_POPUP_SHOW_DURATION,
        queueMode: QueueMode = QueueMode.CONTINUE_OLD_IF_SHOWING,
        gravity: Int = Gravity.NO_GRAVITY,
        saveModel: Boolean = false
    ) {
        showAsDropDown(
            activity = activity,
            anchorViewId = anchorViewId,
            displayMode = DisplayMode.AS_TOOLTIP,
            showDuration = showDuration,
            queueMode = queueMode,
            saveModel = saveModel,
            gravity = gravity
        )

        PopupService.savedPopups[model.tag]?.run {
            (window as TransientPopupWindow?)?.startDismissalTimer()
        }
    }

    fun continueAsTooltip(
        activity: Activity,
        @IdRes newAnchorViewId: Int? = null,
        newGravity: Int? = null,
        resetShowDuration: Long? = null,
        saveModel: Boolean = false
    ) {
        PopupService.getSavedPopupModel(model.tag)?.run {
            window?.dismiss()

            if(model.showDuration > 0 && model.dropdownAnchorViewId != null)
                showAsTooltip(
                    activity,
                    anchorViewId = newAnchorViewId ?: model.dropdownAnchorViewId!!,
                    showDuration = resetShowDuration ?: model.showDuration,
                    queueMode = QueueMode.DISPLAY_NEW,
                    gravity = newGravity ?: model.dropdownGravity,
                    saveModel = saveModel
                )
        }
    }

    fun showAsListMenu(
        activity: Activity,
        @IdRes anchorViewId: Int,
        showDuration: Long = PopupService.DEFAULT_POPUP_SHOW_DURATION,
        queueMode: QueueMode = QueueMode.CONTINUE_OLD_IF_SHOWING,
        gravity: Int = Gravity.NO_GRAVITY,
        saveModel: Boolean = false
    ) {
        showAsDropDown(
            activity = activity,
            anchorViewId = anchorViewId,
            displayMode = DisplayMode.AS_LIST_MENU,
            showDuration = showDuration,
            queueMode = queueMode,
            saveModel = saveModel,
            gravity = gravity
        )
    }

    fun continueAsListMenu(
        activity: Activity,
        @IdRes newAnchorViewId: Int? = null,
        newGravity: Int? = null,
        resetShowDuration: Long? = null,
        saveModel: Boolean = false
    ) {
        PopupService.getSavedPopupModel(model.tag)?.run {
            window?.dismiss()

            if(model.dropdownAnchorViewId != null)
                showAsListMenu(
                    activity,
                    anchorViewId = newAnchorViewId ?: model.dropdownAnchorViewId!!,
                    showDuration = resetShowDuration ?: model.showDuration,
                    queueMode = QueueMode.DISPLAY_NEW,
                    gravity = newGravity ?: model.dropdownGravity,
                    saveModel = saveModel
                )
        }
    }

    private fun setupListMenuPopup(activity: Activity, popup: Popup, saveModel: Boolean) {
        popup.contentView = LayoutPopupBinding.inflate(activity.layoutInflater, null, false).apply {
            root.let {
                it.alpha = model.alpha

                it.background = DrawableBuilder()
                    .solidColor(model.backgroundColor)
                    .strokeWidth(model.strokeWidth)
                    .strokeColor(model.strokeColor)
                    .cornerRadii(model.cornerRadiusTopLeftPx, model.cornerRadiusTopRightPx, model.cornerRadiusBottomRightPx, model.cornerRadiusBottomLeftPx)
                    .apply {
                        if(model.onClickAction != null) {
                            root.setOnClickListener { rootView ->
                                model.onClickAction!!.invoke(rootView, popup)
                            }

                            ripple()
                            rippleColor(GraphicsUtils.getRippleColor(model.backgroundColor))
                        }
                    }
                    .build()
            }

            layoutPopupOptionsListRecyclerView.run {
                layoutManager = LinearLayoutManager(activity).apply {
                    onRestoreInstanceState(model.mainListState)
                }

                adapter = ListPopupMenuAdapter(mutableListOf<ListPopupMenuItem>().apply {
                    model.listMenuItems.forEach {
                        add(it.value)
                    }
                }, activity, popup)
            }
        }.root

        popup.setOnDismissListener {
            if(!popup.fakeDismissal) {
                PopupService.getSavedPopupModel(model.tag)?.window = null

                ThreadingUtils.globalCoroutineScopeDefaultJobs[TransientPopupWindow.KEY_DISMISSAL_TIMER]?.cancel()

                ThreadingUtils.globalCoroutineScopeDefaultJobs.remove(TransientPopupWindow.KEY_DISMISSAL_TIMER)

                if(!saveModel) PopupService.removeSavedPopupModel(model.tag)

                model.onDismissAction?.invoke(popup)
            }
        }
    }

    private fun setupShortMessagePopup(activity: Activity, popup: Popup, saveModel: Boolean) {
        popup.contentView = LayoutPopupBinding.inflate(activity.layoutInflater, null, false).apply {
            root.let {
                it.alpha = model.alpha

                it.background = DrawableBuilder()
                    .solidColor(model.backgroundColor)
                    .strokeWidth(model.strokeWidth)
                    .strokeColor(model.strokeColor)
                    .cornerRadii(model.cornerRadiusTopLeftPx, model.cornerRadiusTopRightPx, model.cornerRadiusBottomRightPx, model.cornerRadiusBottomLeftPx)
                    .apply {
                        if(model.onClickAction != null) {
                            root.setOnClickListener { rootView ->
                                model.onClickAction!!.invoke(rootView, popup)
                            }

                            ripple()
                            rippleColor(GraphicsUtils.getRippleColor(model.backgroundColor))
                        }
                    }
                    .build()
            }

            layoutPopupOptionsListRecyclerView.run {
                layoutManager = LinearLayoutManager(activity).apply {
                    onRestoreInstanceState(model.mainListState)
                }

                adapter = ListPopupMenuAdapter(mutableListOf<ListPopupMenuItem>().apply {
                    add(ListPopupMenuItem(model.text, model.textColor, model.icon, model.iconTintColor, null))
                }, activity, popup)
            }
        }.root

        popup.setOnDismissListener {
            if(!popup.fakeDismissal) {
                PopupService.getSavedPopupModel(model.tag)?.window = null

                ThreadingUtils.globalCoroutineScopeDefaultJobs[TransientPopupWindow.KEY_DISMISSAL_TIMER]?.cancel()

                ThreadingUtils.globalCoroutineScopeDefaultJobs.remove(TransientPopupWindow.KEY_DISMISSAL_TIMER)

                if(!saveModel) PopupService.removeSavedPopupModel(model.tag)

                model.onDismissAction?.invoke(popup)
            }
        }
    }

    /** TEXT SETTINGS */
            inline fun setText(modification: (oldText: String) -> String) = this.apply {
                modification.invoke(model.text).let {
                    model.text = it
                }
            }

            fun getText() = model.text

            inline fun setTextColor(modification: (oldColor: Int) -> Int) = this.apply {
                modification.invoke(model.textColor).let {
                    model.textColor = it
                }
            }

            fun getTextColor() = model.textColor
    /** */

    /** LIST MENU ITEMS SETTINGS */
            fun addListMenuItem(tag: String, item: ListPopupMenuItem) = this.apply {
                model.listMenuItems[tag] = item
            }

            fun removeListMenuItem(tag: String) = this.apply {
                model.listMenuItems.remove(tag)
            }

            fun getListMenuItems() = model.listMenuItems

            fun getListMenuItem(tag: String) = model.listMenuItems[tag]

            inline fun setListMenuItem(tag: String, modification: (oldItem: ListPopupMenuItem?) -> ListPopupMenuItem) = this.apply {
                modification.invoke(model.listMenuItems[tag]).let {
                    model.listMenuItems[tag] = it
                }
            }

            inline fun setListMenuItems(modification: (oldItems: MutableMap<String, ListPopupMenuItem>) -> MutableMap<String, ListPopupMenuItem>) = this.apply {
                modification.invoke(model.listMenuItems).let {
                    model.listMenuItems = it
                }
            }
    /** */

    /** ICON SETTINGS */
            inline fun setIcon(modification: (oldIcon: Drawable?) -> Drawable?) = this.apply {
                modification.invoke(model.icon).let {
                    model.icon = it
                }
            }

            fun getIcon() = model.icon

            inline fun setIconTintColor(modification: (oldColor: Int?) -> Int?) = this.apply {
                modification.invoke(model.iconTintColor).let {
                    model.iconTintColor = it
                }
            }

            fun getIconTintColor() = model.iconTintColor
    /** */

    /** ONCLICK ACTION SETTINGS */
            fun setOnClickAction(modification: (oldAction: ((view: View, popup: PopupWindow) -> Unit)?) -> ((view: View, popup: PopupWindow) -> Unit)?) = this.apply {
                modification.invoke(model.onClickAction).let {
                    model.onClickAction = it
                }
            }

            fun getOnClickAction() = model.onClickAction
    /** */

    /** BACKGROUND COLOR SETTINGS */
            inline fun setBackgroundColor(modification: (oldBackgroundColor: Int) -> Int) = this.apply {
                modification.invoke(model.backgroundColor).let {
                    model.backgroundColor = it
                }
            }

            fun getBackgroundColor() = model.backgroundColor
    /** */

    /** STROKE SETTINGS */
            inline fun setStrokeWidth(modification: (oldStrokeWidth: Int) -> Int) = this.apply {
                modification.invoke(model.strokeWidth).let {
                    model.strokeWidth = it
                }
            }

            fun getStrokeWidth() = model.strokeWidth

            inline fun setStrokeColor(modification: (oldStrokeColor: Int) -> Int) = this.apply {
                modification.invoke(model.strokeColor).let {
                    model.strokeColor = it
                }
            }

            fun getStrokeColor() = model.strokeColor
    /** */

    /** ALPHA SETTINGS */
            inline fun setAlpha(modification: (oldAlpha: Float) -> Float) = this.apply {
                modification.invoke(model.alpha).let {
                    model.alpha = it
                }
            }

            fun getAlpha() = model.alpha
    /** */

    /** CORNER RADIUS SETTINGS */
            fun setCornerRadii(topLeft: Int, topRight: Int, bottomRight: Int, bottomLeft: Int) = this.apply {
                model.cornerRadiusTopLeftPx = topLeft
                model.cornerRadiusTopRightPx = topRight
                model.cornerRadiusBottomRightPx = bottomRight
                model.cornerRadiusBottomLeftPx = bottomLeft
            }

            inline fun setCornerRadiusTopLeftPx(modification: (oldRadius: Int) -> Int) = this.apply {
                modification.invoke(model.cornerRadiusTopLeftPx).let {
                    model.cornerRadiusBottomLeftPx = it
                }
            }

            fun getCornerRadiusTopLeftPx() = model.cornerRadiusTopLeftPx

            inline fun setCornerRadiusTopRightPx(modification: (oldRadius: Int) -> Int) = this.apply {
                modification.invoke(model.cornerRadiusTopRightPx).let {
                    model.cornerRadiusTopRightPx = it
                }
            }

            fun getCornerRadiusTopRightPx() = model.cornerRadiusTopRightPx

            inline fun setCornerRadiusBottomRightPx(modification: (oldRadius: Int) -> Int) = this.apply {
                modification.invoke(model.cornerRadiusBottomRightPx).let {
                    model.cornerRadiusBottomRightPx = it
                }
            }

            fun getCornerRadiusBottomRightPx() = model.cornerRadiusBottomRightPx


            inline fun setCornerRadiusBottomLeftPx(modification: (oldRadius: Int) -> Int) = this.apply {
                modification.invoke(model.cornerRadiusBottomLeftPx).let {
                    model.cornerRadiusBottomLeftPx = it
                }
            }

            fun getCornerRadiusBottomLeftPx() = model.cornerRadiusBottomLeftPx
    /** */

            inline fun setOutsideTouchable(modification: (oldOutsideTouchable: Boolean) -> Boolean) {
                modification.invoke(model.outsideTouchable).let {
                    model.outsideTouchable = it
                }
            }

            fun getOutsideTouchable() = model.outsideTouchable

            fun getAnchorViewId() = model.dropdownAnchorViewId

            fun getParentViewId() = model.locationParentViewId

            fun setOnDismissAction(modification: (oldAction: ((popup: PopupWindow) -> Unit)?) -> ((popUpWindow: PopupWindow) -> Unit)?) = this.apply {
                modification.invoke(model.onDismissAction).let {
                    model.onDismissAction = it
                }
            }

            fun getOnDismissAction() = model.onDismissAction

            fun getTag() = model.tag

            fun save() = this.apply {
                PopupService.savePopupModel(model.tag, model)
            }
}