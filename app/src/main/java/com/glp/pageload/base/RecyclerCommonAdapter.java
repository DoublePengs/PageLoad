package com.glp.pageload.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Guolipeng on 2018/4/8.
 * RecyclerView 的 Adapter 封装
 */

public abstract class RecyclerCommonAdapter<T> extends RecyclerView.Adapter<RecyclerCommonAdapter.RecyclerViewHolder> {

    private final Context mContext;
    private final List<T> mList;
    private OnItemClickListener mItemClickListener;

    public RecyclerCommonAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    // RecyclerView显示的子View.该方法返回是ViewHolder,当有可复用View时,就不再调用
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflateView(parent, viewType);
        return RecyclerViewHolder.get(view);
    }

    /**
     * 根据 viewType 返回 item 的布局
     */
    public abstract View inflateView(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        convert(holder, mList.get(position));
        holder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null)
                    mItemClickListener.onClick(v, position);
            }
        });
    }

    public abstract void convert(RecyclerViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(position);
    }

    public abstract int getViewType(int position);

    public interface OnItemClickListener {
        void onClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }


    // ######################  静态内部类 ViewHolder  ######################

    /**
     * RecyclerView ViewHolder 的封装 [可以方便使用链式调用来绑定数据]
     */
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private SparseArray<View> mViews;
        private View mConvertView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            mConvertView = itemView;
            mViews = new SparseArray<>();
        }

        public static RecyclerViewHolder get(View itemView) {
            return new RecyclerViewHolder(itemView);
        }

        public View getConvertView() {
            return mConvertView;
        }

        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = mConvertView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        /**
         * 设置文字
         */
        public RecyclerViewHolder setText(int viewId, CharSequence text) {
            TextView tv = getView(viewId);
            tv.setText(text);
            return this;
        }

        /**
         * 设置图片
         */
        public RecyclerViewHolder setImageResource(int viewId, int resId) {
            ImageView view = getView(viewId);
            view.setImageResource(resId);
            return this;
        }

        /**
         * 设置图片
         */
        public RecyclerViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
            ImageView view = getView(viewId);
            view.setImageBitmap(bitmap);
            return this;
        }

        /**
         * 设置背景图片
         */
        public RecyclerViewHolder setBackgroundImage(int viewId, Drawable drawable) {
            View view = getView(viewId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
            return this;
        }

        /**
         * 设置控件是否显示
         */
        public RecyclerViewHolder setVisibility(int viewId, boolean show) {
            View view = getView(viewId);
            view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            return this;
        }

        /**
         * 设置文字颜色
         */
        public RecyclerViewHolder setTextColor(int viewId, int resId) {
            TextView view = getView(viewId);
            view.setTextColor(resId);
            return this;
        }

        /**
         * 设置点击事件
         */
        public RecyclerViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
            getView(viewId).setOnClickListener(listener);
            return this;
        }

        /**
         * 设置控件的 select 状态
         */
        public RecyclerViewHolder setSelected(int viewId, boolean selected) {
            getView(viewId).setSelected(selected);
            return this;
        }

    }
}
