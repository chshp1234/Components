package com.common.component;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.common.base.ui.BaseDialogFragment;

/**
 created by dongdaqing 2022/4/26 16:05
 */
public class BottomDialog extends BaseDialogFragment {
    public BottomDialog(FragmentActivity context) {
        super(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(STYLE_NO_TITLE, R.style.BottomDialog);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Object getContentView() {
        return R.layout.dialog_bottom;
    }

    @Override
    protected void init(View view) {

    }

    @Override
    protected void initCustomDialog(Dialog dialog) {
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
