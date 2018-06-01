package com.csw.xposedclipboard;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.csw.xposedclipboard.databinding.DrawerItemBinding;

/**
 * Created by 丛 on 2018/5/29 0029.
 */
public class DrawerAdapter extends BaseRecyclerViewAdapter<DrawerBean> {

    public DrawerAdapter(Context context, int BRId, int layoutId) {
        super(context, BRId, layoutId);
    }

    @Override
    public void onBindViewHolder(@NonNull final BindingViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final DrawerItemBinding binding = (DrawerItemBinding) holder.getBinding();
        binding.switchSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int pos = holder.getAdapterPosition();
                    boolean isChecked = binding.switchSetting.isChecked();
                    listener.onSwitchClickListener(getBeanList().get(pos), pos, isChecked);
                }
            }
        });
    }

    private DrawerClickListener listener;

    public void setListener(DrawerClickListener listener) {
        this.listener = listener;
    }

    public interface DrawerClickListener {
        void onSwitchClickListener(DrawerBean bean, int pos, boolean isChecked);
    }

}
