package com.csw.xposedclipboard;

import android.app.AndroidAppHelper;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.csw.xposedclipboard.util.MyToast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by 丛 on 2018/5/22 0022.
 */
public class Xposed implements IXposedHookLoadPackage {
    private final String TAG = "剪切板探测器：";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        XposedHelpers.findAndHookMethod(ClipboardManager.class, "setPrimaryClip",
                ClipData.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        XposedBridge.log(TAG + "hook成功");
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        // 获取剪切板内容
                        ClipData clipData = (ClipData) param.args[0];
                        String clipStr = clipData.getItemAt(0).getText().toString();
                        // 得到应用上下文
                        Context curContext =
                                AndroidAppHelper.currentApplication().getApplicationContext();
                        PackageManager pm = curContext.getPackageManager();
                        // 获取应用名
                        String appName = lpparam.appInfo.loadLabel(pm).toString();
                        // 获取应用图标
                        Drawable icon = lpparam.appInfo.loadIcon(pm);
                        // 准备剪切板内容
                        String showText = "\"" + appName + "\"" + "使用了剪切板。内容为:" + clipStr;
                        // Xposed打印log信息
                        XposedBridge.log(TAG + showText);

                        HashMap<String, String> settingMap = readConfig();

                        // toast提示
                        if (Boolean.valueOf(settingMap.get("isToast")))
                            MyToast.show(curContext, icon, showText);
                        // 将剪切板内容保存到本地
                        if (Boolean.valueOf(settingMap.get("isRecord")))
                            saveDataToLocal(appName, clipStr, icon);
                    }
                });
    }

    private void saveDataToLocal(String appName, String clipStr, Drawable icon) {
        try {
            String dataPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "Android" + File.separator + "data"
                            + File.separator + "com.csw.xposedclipboard";
            ArrayList<ClipBean> clipBeanList;
            File file = new File(dataPath, "clipboard_data.dat");
            if (!file.getParentFile().exists()) { // 文件不存在
                file.getParentFile().mkdirs();
                boolean state = file.createNewFile();
                if (!state) // 文件创建失败
                    return;
                else //文件创建成功
                    clipBeanList = new ArrayList<>(100);
            } else { // 文件存在
                // 读取本地存储的类
                try {
                    FileInputStream fileInput = new FileInputStream(file);
                    ObjectInputStream objInput = new ObjectInputStream(fileInput);
                    Object object = objInput.readObject();
                    if (object == null)
                        clipBeanList = new ArrayList<>(100);
                    else
                        clipBeanList = (ArrayList<ClipBean>) object;
                    objInput.close();
                    fileInput.close();
                } catch (Exception e) { // 读取本地文件失败
                    e.printStackTrace();
                    clipBeanList = new ArrayList<>(100);
                }
            }
            // 构建一条剪切数据
            ClipBean bean = new ClipBean();
            bean.setName(appName);
            bean.setTimeStamp(System.currentTimeMillis() + "");
            bean.setTime(System.currentTimeMillis() + "");
            bean.setClipText(clipStr);
            bean.setIcon(getBytes(icon));
            bean.setExtra("{}");

            if (clipBeanList.size() < 100) {
                clipBeanList.add(0, bean);
            } else {
                clipBeanList.remove(clipBeanList.size() - 1);
                clipBeanList.add(0, bean);
            }
            // 存到本地
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(clipBeanList);
            objOut.flush();
            objOut.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] getBytes(Drawable icon) {
        Bitmap bitmap = drawableToBitmap(icon);
        //实例化字节数组输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);//压缩位图
        return baos.toByteArray();//创建分配字节数组
    }

    private Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

    private HashMap<String, String> readConfig() {
        try {
            String settingPath =
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + "Android" + File.separator + "data"
                            + File.separator + "com.csw.xposedclipboard";
            File file = new File(settingPath, "setting.dat");
            if (!file.exists()) {
                HashMap<String, String> settingMap = new HashMap<>();
                settingMap.put("isRecord", String.valueOf(true));
                settingMap.put("isToast", String.valueOf(true));
                return settingMap;
            }
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objInput = new ObjectInputStream(fileInput);
            Object object = objInput.readObject();
            objInput.close();
            fileInput.close();
            if (object == null) {
                HashMap<String, String> settingMap = new HashMap<>();
                settingMap.put("isRecord", String.valueOf(true));
                settingMap.put("isToast", String.valueOf(true));
                return settingMap;
            } else {
                return (HashMap<String, String>) object;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashMap<String, String> settingMap = new HashMap<>();
        settingMap.put("isRecord", String.valueOf(true));
        settingMap.put("isToast", String.valueOf(true));
        return settingMap;
    }
}
