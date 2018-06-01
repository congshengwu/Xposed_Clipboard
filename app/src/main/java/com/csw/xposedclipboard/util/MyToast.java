package com.csw.xposedclipboard.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.csw.xposedclipboard.R;

/**
 * Created by 丛 on 2018/5/31 0031.
 */
public class MyToast {

    public static void show(Context context, Drawable icon, String text) {
        float size1Dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                context.getResources().getDisplayMetrics());

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(20 * size1Dp);
        bg.setColor(Color.parseColor("#E91E63")); // colorAccent
        layout.setBackground(bg);

        if (icon != null) {
            ImageView iv = new ImageView(context);
            LinearLayout.LayoutParams ivParams =
                    new LinearLayout.LayoutParams((int) (size1Dp * 30), (int) (size1Dp * 30));
            ivParams.gravity = Gravity.CENTER_VERTICAL;
            ivParams.setMargins((int) (10 * size1Dp), (int) (10 * size1Dp), 0, (int) (10 * size1Dp));
            iv.setLayoutParams(ivParams);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageDrawable(icon);

            layout.addView(iv);
        }

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvParams.gravity = Gravity.CENTER_VERTICAL;
        tvParams.setMargins((int) (10 * size1Dp), (int) (10 * size1Dp), (int) (10 * size1Dp), (int) (10 * size1Dp));
        tv.setLayoutParams(tvParams);
        tv.setTextColor(Color.WHITE);
        tv.setText(text);

        layout.addView(tv);

        Toast toast = new Toast(context);
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

}
