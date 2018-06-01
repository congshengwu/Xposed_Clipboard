package com.csw.xposedclipboard.util;

import java.util.Calendar;

/**
 * Created by 丛 on 2018/5/31 0031.
 */
public class MyTimeUtil {

    public static String formatTime(String timeStr) {
        long timestamp = Long.valueOf(timeStr);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(timestamp);
        Calendar now = Calendar.getInstance();
        if (time.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                time.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                time.get(Calendar.DAY_OF_MONTH) == now.get(Calendar.DAY_OF_MONTH)) { // 今天
            int deltaHour = now.get(Calendar.HOUR_OF_DAY) - time.get(Calendar.HOUR_OF_DAY);
            int deltaMinute = now.get(Calendar.MINUTE) - time.get(Calendar.MINUTE);
            int deltaTime = deltaHour * 60 + deltaMinute;
            // 一小时内
            if (deltaTime < 60) {
                return deltaTime == 0 ? "1" + "分钟前" : deltaTime + "分钟前";
            }
            // 一小时外
            else {
                int hour = time.get(Calendar.HOUR_OF_DAY);
                int minute = time.get(Calendar.MINUTE);
                return (hour < 10 ? "0" + hour : "" + hour)
                        + ":" +
                        (minute < 10 ? "0" + minute : "" + minute);
            }
        } else if (time.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                time.get(Calendar.MONTH) == now.get(Calendar.MONTH) &&
                time.get(Calendar.DAY_OF_MONTH) + 1 == now.get(Calendar.DAY_OF_MONTH)) { // 昨天
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);
            return "昨天" +
                    (hour < 10 ? "0" + hour : "" + hour)
                    + ":" +
                    (minute < 10 ? "0" + minute : "" + minute);
        } else if (time.get(Calendar.YEAR) == now.get(Calendar.YEAR)) { // 昨天之前并且是今年
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);
            return (time.get(Calendar.MONTH) + 1) + "月" + time.get(Calendar.DAY_OF_MONTH) + "日" +
                    (hour < 10 ? "0" + hour : "" + hour)
                    + ":" +
                    (minute < 10 ? "0" + minute : "" + minute);
        } else if (time.get(Calendar.YEAR) != now.get(Calendar.YEAR)) {
            int year = time.get(Calendar.YEAR) - 2000;
            int hour = time.get(Calendar.HOUR_OF_DAY);
            int minute = time.get(Calendar.MINUTE);
            return year + "年" + (time.get(Calendar.MONTH) + 1) + "月" +
                    time.get(Calendar.DAY_OF_MONTH) + "日" +
                    (hour < 10 ? "0" + hour : "" + hour)
                    + ":" +
                    (minute < 10 ? "0" + minute : "" + minute);
        } else { // 默认返回值
            return (time.get(Calendar.MONTH) + 1) + "月" + time.get(Calendar.DAY_OF_MONTH) + "日";
        }
    }

}
