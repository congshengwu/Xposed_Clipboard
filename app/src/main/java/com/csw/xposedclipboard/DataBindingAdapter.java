package com.csw.xposedclipboard;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by 丛 on 2018/5/31 0031.
 */
public class DataBindingAdapter {

    @BindingAdapter("srcBytes")
    public static void setIcon(ImageView imageView, byte[] icon) {
        Drawable d = new BitmapDrawable(MainActivity.weakReference.get().getResources(),
                getBitmap(icon));
        imageView.setImageDrawable(d);
    }

    private static Bitmap getBitmap(byte[] data){
        return BitmapFactory.decodeByteArray(data, 0, data.length);//从字节数组解码位图
    }

}
