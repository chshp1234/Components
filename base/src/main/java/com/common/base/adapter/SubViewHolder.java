package com.common.base.adapter;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author csp
 * @date 2017/10/5
 */
public class SubViewHolder extends RecyclerView.ViewHolder {

    public SubViewHolder(View itemView) {
        super(itemView);
    }

    public <T extends View> T getView(@IdRes int viewId) {
        return itemView.findViewById(viewId);
    }
}
