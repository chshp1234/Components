package com.common.base.adapter;

import android.view.View;

/**
 * item点击事件监听
 *
 * @author csp
 * @date 2017/10/12
 */
public interface OnItemClickListener<T> {
    void onItemClick(View itemView, T position);
}
