package com.xiandian.openstack.cloud.swiftstorage.fragment;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.api.ContainerResource;
import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.Objects;
import com.xiandian.openstack.cloud.swiftstorage.AppState;
import com.xiandian.openstack.cloud.swiftstorage.LoginActivity;
import com.xiandian.openstack.cloud.swiftstorage.MainActivity;
import com.xiandian.openstack.cloud.swiftstorage.R;
import com.xiandian.openstack.cloud.swiftstorage.base.TaskResult;
import com.xiandian.openstack.cloud.swiftstorage.fs.OSSFileSystem;
import com.xiandian.openstack.cloud.swiftstorage.fs.SFile;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.utils.PromptDialogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */

public class GarbageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SFileEditable,SFileListViewAdapter.ItemClickCallable {
    //File data model
    List<SFileData> fileListData = new ArrayList<SFileData>();
    //File List View
    private ListView fileListView;
    //当前展示的文件夹列表（注意：太多临时变量不容易维护）
    private List<SFile> swiftFolders=null;
    //当前展示的文件列表（注意：太多临时变量不容易维护）
    private List<SFile> swiftFiles=null;
    //Log 信息标签。
    private String TAG = MainFragment.class.getSimpleName();
    //File List View Adapter
    private SFileListViewAdapter fileListViewAdapter;
    //Context
    private Context context;
    //下拉刷新
    private SwipeRefreshLayout fileListSwipe;
    //回收站容器名
    String containerName;
    public GarbageFragment() {
        // Required empty public constructor
        containerName="garbage_"+getAppState().getSelectedContainer().getName();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_garbage, container, false);
        context = this.getActivity();

        //(3) 下拉刷新
        fileListSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_files2);
        fileListSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fileListSwipe.setOnRefreshListener(this);//增加刷新方法
        //(4) 文件列表视图
        fileListView = (ListView) rootView.findViewById(R.id.garbage_list_root);
        fileListViewAdapter = new SFileListViewAdapter(context,fileListData,this);
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
     * File保持的名称含有路径，进行分解，只取文件名称。
     *
     * @param path
     * @return the string
     */
    private String cleanName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
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
    /**
     * 获取选择的文件或目录。
     *
     * @return
     */
    private List<SFile> getSelectedFiles() {
        ArrayList<SFile> selected = new ArrayList<SFile>();
        for (SFileData fd : fileListData) {
            if (fd.isChecked()) {
                selected.add(fd.isFolder() ? swiftFolders.get(fd.getIndex()) : swiftFiles.get(fd.getIndex()));
            }
        }
        return null;
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
                return fd.isFolder() ? swiftFolders.get(fd.getIndex()) : swiftFiles.get(fd.getIndex());
            }
        }
        return null;
    }
}