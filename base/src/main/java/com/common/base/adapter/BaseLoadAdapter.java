package com.common.base.adapter;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.common.base.R;

import java.util.List;

/**
 * @author csp
 * @date 2017/12/25
 */
public abstract class BaseLoadAdapter<T, K extends SubViewHolder> extends BaseAdapter<T, SubViewHolder> {
    private static final String                 TIP_ERR_NET = "哎哟，没网了，请检查网络设置";
    protected            OnItemClickListener<T> mOnItemClickListener;

    protected LoadMoreListener loadMoreListener;

    public static final int TYPE_ITEM   = 1;
    public static final int TYPE_BOTTOM = 2;

    public              int loadState;
    public static final int STATE_LOADING         = 101;
    public static final int STATE_LASTED          = 102;
    public static final int STATE_ERROR           = 103;
    public static final int STATE_LOADING_SUCCESS = 104;
    public static final int STATE_GONE            = 105;

    protected Context mContext;

    public        boolean              hasMore             = true;
    public        boolean              isLoading           = false;
    private final View.OnClickListener onItemClickListener =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener == null) {
                        return;
                    }
                    if (v != null && v.getTag() != null && v.getTag() instanceof RecyclerView.ViewHolder) {
                        RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
                        if (holder.getBindingAdapterPosition() == NO_POSITION) {
                            return;
                        }
                        mOnItemClickListener.
                                onItemClick(v, mList.get(holder.getBindingAdapterPosition()));
                    }
                }
            };

    public BaseLoadAdapter(Context context, LoadMoreListener loadMoreListener) {
        mContext = context;
        this.loadMoreListener = loadMoreListener;
    }

    public BaseLoadAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOTTOM) {
            return new SubViewHolder(
                    LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.footer_view, parent, false));
        } else {
            Object contentView = getContentView(parent, viewType);
            Object view;
            if (contentView instanceof Integer) {
                view = LayoutInflater.from(mContext).inflate((int) contentView, parent, false);
            } else {
                view = contentView;
            }

            SubViewHolder viewHolder = createViewHolder(view);
            viewHolder.itemView.setOnClickListener(onItemClickListener);
            viewHolder.itemView.setTag(viewHolder);
            return viewHolder;
        }
    }

    /**
     * item布局ID
     *
     * @return item布局ID
     */
    public abstract Object getContentView(ViewGroup parent, int viewType);

    /**
     * 给view中的控件设置数据
     *
     * @param holder itemHolder
     * @param item   当前item在当前的相对位置
     */
    protected abstract void onBindItemViewHolder(K holder, T item);

    /**
     * Create view holder sub view holder.
     *
     * @param view the view
     * @return the sub view holder
     */
    public abstract K createViewHolder(Object view);

    @Override
    public void onBindViewHolder(final SubViewHolder holder, int position) {
        if (TYPE_BOTTOM == getItemViewType(position)) {

            final View        leftLine       = holder.getView(R.id.left_line);
            final View        rightLine      = holder.getView(R.id.right_line);
            final ProgressBar progressBar    = holder.getView(R.id.progress_bar);
            final TextView    bottomTextView = holder.getView(R.id.progress_text);

            switch (loadState) {
                case STATE_LOADING_SUCCESS:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("继续滑动加载更多");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_LOADING:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("加载中...");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_LASTED:
                    leftLine.setVisibility(View.VISIBLE);
                    rightLine.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText("我也是有底线的~");
                    holder.itemView.setOnClickListener(null);
                    break;
                case STATE_ERROR:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.VISIBLE);
                    bottomTextView.setText(TIP_ERR_NET);
                    holder.itemView.setOnClickListener(
                            v -> {
                                //
                                // progressBar.setVisibility(View.VISIBLE);
                                //                                bottomTextView.setText("加载中...");
                                loadMoreListener.loadMoreData();
                            });
                    break;
                case STATE_GONE:
                    leftLine.setVisibility(View.GONE);
                    rightLine.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    bottomTextView.setVisibility(View.GONE);
                default:
                    break;
            }
        } else {
            onBindItemViewHolder((K) holder, mList.get(holder.getBindingAdapterPosition()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (loadMoreListener != null) {
            if (position == mList.size()) {
                return TYPE_BOTTOM;
            } else {
                return TYPE_ITEM;
            }
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        if (loadMoreListener != null) {
            return mList.size() + 1;
        } else {
            return mList.size();
        }
    }

    public void setErrorStatus() {
        loadState = STATE_ERROR;
        hasMore = true;
        asyncNotifyItemDate(getItemCount() - 1);
        setLoading(false);
    }

    public void setLastedStatus() {
        loadState = STATE_LASTED;
        hasMore = false;
        asyncNotifyItemDate(getItemCount() - 1);
    }

    public void setLoadingGoneStatus() {
        loadState = STATE_GONE;
        hasMore = true;
        asyncNotifyItemDate(getItemCount() - 1);
    }

    @Override
    public void appendData(List<T> dataSet) {
        super.appendData(dataSet);
        loadState = STATE_LOADING_SUCCESS;
        hasMore = true;
        setLoading(false);
    }

    public void refreshList(List<T> newList) {
        this.mList.clear();
        this.mList.addAll(newList);
        asyncNotifyAllDate();
    }

    public interface LoadMoreListener {
        void loadMoreData();
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        if (loadMoreListener != null && loading) {
            loadState = STATE_LOADING;
            hasMore = true;
            asyncNotifyItemDate(getItemCount() - 1);
        }
        isLoading = loading;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public LoadMoreListener getLoadMoreListener() {
        return loadMoreListener;
    }

}
