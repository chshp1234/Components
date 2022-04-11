package com.common.base.adapter;

import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author csp
 * @date 2017/12/25
 */
abstract class BaseAdapter<T, K extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<K> {
    protected List<T> mList = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());


    private DiffUtil.ItemCallback<T> diffItem = new DiffUtil.ItemCallback<T>() {
        public boolean areItemsTheSame(T oldItem, T newItem) {
            return BaseAdapter.this.areItemsTheSame(oldItem, newItem);
        }

        public boolean areContentsTheSame(T oldItem, T newItem) {
            return BaseAdapter.this.areItemsTheSame(oldItem, newItem);
        }
    };

    private AsyncListDiffer<T> asyncListDiffer = new AsyncListDiffer<T>(this, diffItem);

    protected boolean areItemsTheSame(T oldItem, T newItem) {
        return false;
    }

    protected boolean areContentsTheSame(T oldItem, T newItem) {
        return false;
    }

    public void updateData(List<T> data, RecyclerView rv) {
        asyncListDiffer.submitList(data, () -> rv.scrollToPosition(0));

        getDataSet().clear();
        getDataSet().addAll(data);
    }

    public void appendData(List<T> dataSet) {
        if (mList == null) {
            mList = new ArrayList<>();
        }
        if (dataSet != null && !dataSet.isEmpty()) {
            int startIndex = getItemCount() > 0 ? getItemCount() - 1 : 0;
            int oldCount   = this.mList.size();
            this.mList.addAll(dataSet);
            //            notifyItemRangeChanged(startIndex, this.mList.size() - oldCount);
            asyncNotifyAllDate();
        }
    }

    public List<T> getDataSet() {
        return mList;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void asyncNotifyItemDate(int position) {
        handler.post(() -> notifyItemChanged(position));
    }

    public void asyncNotifyAllDate() {
        final Runnable r = this::notifyDataSetChanged;
        handler.post(r);
    }

    public void asyncNotifyItemInsert(int position) {
        final Runnable r =
                () -> {
                    notifyItemInserted(position);
                    notifyItemRangeChanged(position, mList.size() - position);
                };
        handler.post(r);
    }

    public void asyncNotifyItemRemove(int position) {
        final Runnable r =
                () -> {
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, mList.size() - position);
                };
        handler.post(r);
    }

    public void asyncSmoothScroll(RecyclerView recyclerView, int position) {
        final Runnable r =
                () -> {
                    recyclerView.smoothScrollToPosition(position);
                };
        handler.post(r);
    }

}
