package bogdandonduk.androidlibs.popupsandroid.core.compose

interface PositionedPopupModel {
    var dropdownAnchorViewId: Int?
    var dropdownGravity: Int
    var dropdownXOff: Int
    var dropdownYOff: Int

    var locationParentViewId: Int?
    var locationGravity: Int
    var locationX: Int
    var locationY: Int
}