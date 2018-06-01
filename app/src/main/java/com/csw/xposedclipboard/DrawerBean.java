package com.csw.xposedclipboard;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.graphics.drawable.Drawable;

/**
 * Created by 丛 on 2018/5/29 0029.
 */
public class DrawerBean extends BaseObservable {
    private String name;
    private Drawable icon;
    private boolean isNeedSwitch;
    private boolean isSwitchChecked;

    public DrawerBean() {
        isNeedSwitch = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean getIsNeedSwitch() {
        return isNeedSwitch;
    }

    public void setIsNeedSwitch(boolean isNeedSwitch) {
        this.isNeedSwitch = isNeedSwitch;
    }

    @Bindable
    public boolean getIsSwitchChecked() {
        return isSwitchChecked;
    }

    public void setIsSwitchChecked(boolean isSwitchChecked) {
        this.isSwitchChecked = isSwitchChecked;
        notifyPropertyChanged(BR.isSwitchChecked);
    }
}
