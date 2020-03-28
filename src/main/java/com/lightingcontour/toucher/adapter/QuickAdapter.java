package com.lightingcontour.toucher.adapter;



import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by zack on 2018/3/12.
 */

public abstract class QuickAdapter<T> extends BaseQuickAdapter<T> {
    public QuickAdapter(int layoutResId, @Nullable List<T> data) {
        super(layoutResId, data);
    }

    public QuickAdapter(int layoutResId) {
        super(layoutResId);
    }

    protected abstract void convert(VHolder holder, T t);

    protected void convert(VHolder holder, T t, List<Object> payloads) {
        convert(holder,t);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        convert(holder, getItem(position));
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            convert(holder,getItem(position));
        } else {
            convert(holder, getItem(position),payloads);
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
