package com.glp.pageload.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.glp.pageload.R;
import com.glp.pageload.base.RecyclerCommonAdapter;
import com.glp.pageload.bean.ArticleBean;

import java.util.List;

/**
 * Created by Guolipeng on 2018/4/8.
 * 文章列表适配器
 */

public class ArticleAdapter extends RecyclerCommonAdapter<ArticleBean> {

    private final Context mContext;

    public ArticleAdapter(Context context, List<ArticleBean> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    public View inflateView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.item_android, parent, false);
    }

    @Override
    public void convert(RecyclerCommonAdapter.RecyclerViewHolder holder, ArticleBean bean) {
        holder.setText(R.id.tv_title, bean.getDesc())
                .setText(R.id.tv_date, bean.getCreatedAt());
    }


    @Override
    public int getViewType(int position) {
        return 0;
    }
}
