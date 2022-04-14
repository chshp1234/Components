package com.common.base.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * @author csp
 * @date 2017/12/25
 */
public class LoadMoreScrollListener extends RecyclerView.OnScrollListener {

    private int scrollDistance = 0;

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        //        scrollDistance = getDistance(recyclerView);
        //        LogUtils.i("onScrolled: scrollDistance=" + scrollDistance);
        //                LogUtils.i("onScrolled: dx=" + dx);
        //                LogUtils.i("onScrolled: dy=" + dy);
        RecyclerView.LayoutManager manager                 = recyclerView.getLayoutManager();
        BaseLoadAdapter            adapter                 = (BaseLoadAdapter) recyclerView.getAdapter();
        int                        lastVisibleItemPosition = getLastVisiblePosition(recyclerView);
        if (null == manager) {
            throw new RuntimeException("you should call setLayoutManager() first!!");
        }

        if (adapter != null) {
            if (adapter.hasMore && adapter.getItemCount() - 1 == lastVisibleItemPosition) {
                if (!adapter.isLoading() && adapter.getLoadMoreListener() != null) {
                    adapter.getLoadMoreListener().loadMoreData();
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //        LogUtils.d("onScrollStateChanged: newState=" + newState);
    }

    /**
     * 获取第一条展示的位置
     *
     * @return
     */
    public int getFirstVisiblePosition(RecyclerView recyclerView) {
        int position;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position =
                    ((GridLayoutManager) recyclerView.getLayoutManager())
                            .findFirstVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager =
                    (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] lastPositions =
                    layoutManager.findFirstVisibleItemPositions(
                            new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获取第一条完全可见的item的位置
     *
     * @param recyclerView the recycler view
     * @return the int
     */
    public int getFirstCompleteVisiblePosition(RecyclerView recyclerView) {
        int position;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position =
                    ((GridLayoutManager) recyclerView.getLayoutManager())
                            .findFirstCompletelyVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager =
                    (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] lastPositions =
                    layoutManager.findFirstCompletelyVisibleItemPositions(
                            new int[layoutManager.getSpanCount()]);
            position = getMinPositions(lastPositions);
        } else {
            position = 0;
        }
        return position;
    }

    /**
     * 获取最后一条展示的位置
     *
     * @return
     */
    public int getLastVisiblePosition(RecyclerView recyclerView) {
        int position;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position =
                    ((GridLayoutManager) recyclerView.getLayoutManager())
                            .findLastVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager =
                    (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] lastPositions =
                    layoutManager.findLastVisibleItemPositions(
                            new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = recyclerView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获取最后一条完全可见的item展示的位置
     *
     * @return
     */
    public int getLastCompleteVisiblePosition(RecyclerView recyclerView) {
        int position;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            position =
                    ((LinearLayoutManager) recyclerView.getLayoutManager())
                            .findLastCompletelyVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            position =
                    ((GridLayoutManager) recyclerView.getLayoutManager())
                            .findLastCompletelyVisibleItemPosition();
        } else if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager =
                    (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            int[] lastPositions =
                    layoutManager.findLastCompletelyVisibleItemPositions(
                            new int[layoutManager.getSpanCount()]);
            position = getMaxPosition(lastPositions);
        } else {
            position = recyclerView.getLayoutManager().getItemCount() - 1;
        }
        return position;
    }

    /**
     * 获得当前展示最小的position
     *
     * @param positions
     * @return
     */
    public int getMinPositions(int[] positions) {
        int size        = positions.length;
        int minPosition = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            minPosition = Math.min(minPosition, positions[i]);
        }
        return minPosition;
    }

    /**
     * 获得最大的位置
     *
     * @param positions
     * @return
     */
    public int getMaxPosition(int[] positions) {
        int size        = positions.length;
        int maxPosition = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            maxPosition = Math.max(maxPosition, positions[i]);
        }
        return maxPosition;
    }

    /** 获取RecyclerView滚动距离 */
    public int getDistance(RecyclerView mRecyclerView) {
        if (mRecyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager layoutManager =
                    (StaggeredGridLayoutManager) mRecyclerView.getLayoutManager();
            View firstVisibItem    = mRecyclerView.getChildAt(0);
            int  itemCount         = layoutManager.getItemCount();
            int  recycleViewHeight = mRecyclerView.getHeight();
            int  itemHeight        = firstVisibItem.getHeight();
            int  firstItemBottom   = layoutManager.getDecoratedBottom(firstVisibItem);
            return (itemCount - getFirstVisiblePosition(mRecyclerView) - 1) * itemHeight
                   - recycleViewHeight;
        }
        return 0;
    }
}
