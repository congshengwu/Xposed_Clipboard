package com.csw.xposedclipboard;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * Created by 丛 on 2018/5/28 0028.
 */
public class ClipBean extends BaseObservable implements Serializable {
    private byte[] icon; // 二进制应用图标
    private String name; // 应用名
    private String timeStamp; // 时间戳
    private String time; // 格式化后的时间（如：昨天5:05）
    private String clipText; // 剪切数据
    private String extra; // json格式

    public byte[] getIcon() {
        return this.icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getClipText() {
        return clipText;
    }

    public void setClipText(String clipText) {
        this.clipText = clipText;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
