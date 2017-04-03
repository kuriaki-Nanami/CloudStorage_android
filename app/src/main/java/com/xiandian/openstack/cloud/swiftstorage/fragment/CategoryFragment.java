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
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
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
 * 展示所有文件的Fragment，内部包含一个ListView。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class CategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SFileListViewAdapter.ItemClickCallable, SFileEditable {


    //Log 信息标签。
    private String TAG = MainFragment.class.getSimpleName();
    //Context
    private Context context;

    //操作，确定和取消，默认隐藏，有操作是显示
    private LinearLayout fileActionBar;
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

    //增加分类类别，分别取导航的显示内容
    private String[] mimeTypes;
    //图片地址
    private String[] imgfiles;
    //下载的id
    int downid=0;
    //进度条
    ProgressBar pb;
    //文件大小
    int   fileSize=0;
    //已下载的文件大小
    int   downLoadFileSize=0;
    /**
     * 缺省构造器。
     */
    public CategoryFragment() {
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
        mimeTypes = getArguments().getStringArray(Constants.CATEGORY_TYPE);

        context = this.getActivity();
        fileIconHelper = new FileIconHelper(context);
        //Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //操作按钮（确认和取消），当移动，复制等操作是出现。
        fileActionBar = (LinearLayout) rootView.findViewById(R.id.layout_operation_bar);

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
     * File保持的名称含有路径，进行分解，只取文件名称。
     *
     * @param path
     * @return the string
     */
    private String cleanName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }
    ///////////////////////////////////////////////

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


    @Override
    public void intoItem(int position) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void search(String fileName) {

    }

    @Override
    public void share() {

    }

    @Override
    public void selectAll() {

    }

    @Override
    public void unselectAll() {

    }

    @Override
    public void openFile(SFile filePath) {

    }

    @Override
    public void createDir(String filePath) {

    }

    @Override
    public void upload() {

    }

    @Override
    public void download() {

    }

    @Override
    public void takePhoto() {

    }

    @Override
    public void recordvideo() {

    }

    @Override
    public void recordaudio() {

    }

    @Override
    public void rename(String oldFilePath, String newFilePath) {

    }

    @Override
    public void copy(String fromPath, String toPath) {

    }

    @Override
    public void move(String fromPath, String toPath) {

    }

    @Override
    public void recycle(String filePath) {

    }

    @Override
    public void sort() {

    }

    @Override
    public void details(int type, boolean ascend) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void restroe() {

    }

    @Override
    public void empty() {

    }
}
