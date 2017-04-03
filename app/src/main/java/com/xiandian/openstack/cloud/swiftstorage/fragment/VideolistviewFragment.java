package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.api.ContainerResource;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.woorea.openstack.swift.model.Objects;
import com.xiandian.openstack.cloud.swiftstorage.AppState;
import com.xiandian.openstack.cloud.swiftstorage.LoginActivity;
import com.xiandian.openstack.cloud.swiftstorage.R;
import com.xiandian.openstack.cloud.swiftstorage.base.TaskResult;
import com.xiandian.openstack.cloud.swiftstorage.fs.OSSFileSystem;
import com.xiandian.openstack.cloud.swiftstorage.fs.SFile;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.utils.Constants;
import com.xiandian.openstack.cloud.swiftstorage.utils.DisplayUtils;
import com.xiandian.openstack.cloud.swiftstorage.utils.FileIconHelper;
import com.xiandian.openstack.cloud.swiftstorage.utils.PromptDialogUtil;
import com.xiandian.openstack.cloud.swiftstorage.utils.Sort_dialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 62566 on 2016/10/9.
 */
public class VideolistviewFragment extends VideoFragment {

    //Log 信息标签。
    private String TAG = MainFragment.class.getSimpleName();
    //Context
    private Context context;
    //下拉刷新
    private SwipeRefreshLayout fileListSwipe;
    //File List View
    private ListView fileListView;
    //图片工具类
    FileIconHelper fileIconHelper;
    //File List View Adapter
    private SFileListViewAdapter fileListViewAdapter;
    //File data model
    List<SFileData> fileListData = new ArrayList<SFileData>();

    //当前展示的文件列表（注意：太多临时变量不容易维护）
    private List<SFile> swiftFiles;
    //图片地址
    private String[] imgfiles;

    /**
     * 缺省构造器。
     */
    public VideolistviewFragment() {
    }

    /**
     * 构造视图。
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //修改(1) 获取参数，分类类型，由MainActivity传入

        context = this.getActivity();
        fileIconHelper = new FileIconHelper(context);
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //下拉刷新
        fileListSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_files);
        fileListSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fileListSwipe.setOnRefreshListener(this);//增加刷新方法

        //文件列表视图
        fileListView = (ListView) rootView.findViewById(R.id.main_list_root);
        //创适配器
        fileListViewAdapter = new SFileListViewAdapter(context, fileListData, this);
        fileListView.setAdapter(fileListViewAdapter);


        return rootView;
    }
    /**
     * 目前APP的状态记录。
     *
     * @return
     */
    private AppState getAppState() {
        return AppState.getInstance();
    }

    /**
     * 服务。
     *
     * @return
     */
    private OpenStackClientService getService() {
        return OpenStackClientService.getInstance();
    }


    /**
     * 改名。
     *
     * @param oldFilePath
     * @param newFilePath
     */
    @Override
    public void rename(String oldFilePath, String newFilePath) {

    }

    @Override
    public void restroe() {

    }

    @Override
    public void intoItem(int position) {
        //选择对应的数据
        SFileData item = fileListData.get(position);
        openFile(this.swiftFiles.get(position));
        //Todo : 如果是文件，直接启动本地打开，如果打开，需要下载，放在后期这里，弹出框提示。
        //        Toast.makeText(this.getActivity(), "File:" + item.getFileName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        fileListSwipe.setRefreshing(true);

        //2秒刷新事件
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fileListSwipe.setRefreshing(false);
            }
        }, 2000);
    }

    /**
     * 获取选择的文件或目录。
     *
     * @return
     */
    private SFile getFirstSelectedFile() {
        ArrayList<SFile> selected = new ArrayList<SFile>();
        for (SFileData fd : fileListData) {
            if (fd.isChecked() && !fd.isFolder()) {
                return swiftFiles.get(fd.getIndex());
            }
        }
        return null;
    }

    /**
     * 获取选择的第一个文件或目录。
     *
     * @return
     */
    private SFile getFirstSelected() {
        for (SFileData fd : fileListData) {
            if (fd.isChecked()) {
                return swiftFiles.get(fd.getIndex());
            }
        }
        return null;
    }

}
