package com.common.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


/**
 * @author csp
 * @date 2017/9/28
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = setContent(inflater);

        //        mContext = (T) getActivity();
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    protected View setContent(LayoutInflater inflater) {
        Object layout = getContentView();
        View   view   = null;
        if (layout instanceof Integer) {
            view = inflater.inflate((Integer) layout, null);
        } else if (layout instanceof View) {
            view = (View) layout;
        }
        return view;
    }

    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract Object getContentView();

    /** 初始化应用程序，设置一些初始化数据,获取数据等操作 */
    protected abstract void init(View view);
}
