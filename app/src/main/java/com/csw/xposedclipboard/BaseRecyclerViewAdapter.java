package com.csw.xposedclipboard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by 丛 on 2018/5/28 0028.
 */
public class BaseRecyclerViewAdapter<B> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BindingViewHolder> {
    private final LayoutInflater inflate;
    private ArrayList<B> beanList;
    private int BRId;
    private int layoutId;

    /**
     * 多布局构造方法,当有多个布局时,重写onCreateViewHolder()方法,在里面指定布局
     * @param context 用来会的布局加载器
     */
    public BaseRecyclerViewAdapter(Context context) {
        inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        beanList = new ArrayList<>();
        this.BRId = -1;
    }

    /**
     * 单布局使用此构造方法,在构造方法中就指定布局文件
     * @param context
     * @param BRId bean类
     * @param layoutId item布局资源
     */
    public BaseRecyclerViewAdapter(Context context, int BRId, int layoutId) {
        inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        beanList = new ArrayList<>();
        this.BRId = BRId;
        this.layoutId = layoutId;
    }

    /**
     * 多布局需要重写此方法
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public BindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 绑定试图
        ViewDataBinding binding = DataBindingUtil.inflate(inflate, layoutId, parent, false);
        return new BindingViewHolder(binding);
    }

    /**
     * 多布局需要完全重写此方法
     * 单布局无需重写此方法,如果要重写,可以保留“数据填充”代码部分
     * 多布局BRId为-1,单布局存在正常BRId
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull final BaseRecyclerViewAdapter.BindingViewHolder holder,
                                 final int position) {
        // 数据填充代码
        if (BRId != -1) {
            final B bean = beanList.get(position);
            holder.binding.setVariable(BRId, bean);
            holder.binding.executePendingBindings();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemClick(holder.getAdapterPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null)
                        itemClickListener.onItemLongClick(holder.getAdapterPosition());
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return beanList.size();
    }

    // 默认从尾部添加,不需要更新位置
    public void add(int pos, B bean) {
        beanList.add(pos, bean);
        notifyItemInserted(pos);
    }

    /**从0号位置添加数据,更新位置
     * @param bean
     */
    public void addStart(B bean) {
        beanList.add(0 ,bean);
        notifyItemInserted(0);
    }

    // 从尾部添加,不需要更新位置
    public void addEnd(B bean) {
        beanList.add(bean);
        notifyItemInserted(beanList.size() - 1);
    }

    public void addAll(int pos, ArrayList<B> beanList) {
        this.beanList.addAll(pos, beanList);
        notifyDataSetChanged();
    }

    /**
     * 从0号位置添加数据
     * @param beanList
     */
    public void addAllStart(ArrayList<B> beanList) {
        this.beanList.addAll(0, beanList);
        notifyDataSetChanged();
    }

    public void addAllEnd(ArrayList<B> beanList) {
        this.beanList.addAll(beanList);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        try { // 滑动删除可能报错
            if (beanList.size() == 0) // 防止数组越界异常
                return;
            beanList.remove(position);
            notifyItemRemoved(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (itemClickListener != null)
            itemClickListener.onItemRemove();
    }

    public void removeAll() {
        beanList.clear();
        notifyDataSetChanged();
    }

    public void set(int position, B bean) {
        this.beanList.set(position, bean);
        notifyItemChanged(position);
    }

    public ArrayList<B> getBeanList() {
        return this.beanList;
    }

    protected void setBeanList(ArrayList<B> beanList) {
        this.beanList = beanList;
    }

    protected LayoutInflater getInflate() {
        return this.inflate;
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(int pos);
        void onItemLongClick(int pos);
        void onItemRemove();
    }

    static class BindingViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        BindingViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }
}
