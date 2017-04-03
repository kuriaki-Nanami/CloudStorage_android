package com.xiandian.openstack.cloud.swiftstorage.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.xiandian.openstack.cloud.swiftstorage.R;

public class Sort_dialog extends Dialog{
    Context context;
    public Sort_dialog(Context context) {
        super(context);
        this.context=context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_sort_dialog);
    }
    protected Sort_dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
