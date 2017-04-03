package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woorea.openstack.swift.model.Object;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.xiandian.openstack.cloud.swiftstorage.AppState;
import com.xiandian.openstack.cloud.swiftstorage.R;
import com.xiandian.openstack.cloud.swiftstorage.base.TaskResult;
import com.xiandian.openstack.cloud.swiftstorage.fs.SFile;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.utils.DisplayUtils;
import com.xiandian.openstack.cloud.swiftstorage.utils.PromptDialogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by 62566 on 2016/10/9.
 */
public class VideoFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        SFileListViewAdapter.ItemClickCallable, SFileEditable, View.OnClickListener {
    Resources resources;
    private ViewPager mPager;
    private ArrayList<Fragment> fragmentsList;
    private TextView tvTabNew, tvTabHot;

    private int currIndex = 0;
    public final static int num = 2 ;
    VideolistviewFragment home1;
    VideogridviewFragment home2;
    //下载的id
    int downid=0;
    //进度条
    ProgressBar pb;
    //文件大小
    int   fileSize=0;
    //已下载的文件大小
    int   downLoadFileSize=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.img_video, null);
        resources = getResources();
        InitTextView(view);
        InitViewPager(view);
        tvTabHot.setTextColor(resources.getColor(R.color.noselected));
        return view;
    }

    private void InitTextView(View parentView) {
        tvTabNew = (TextView) parentView.findViewById(R.id.tv_tab_1);
        tvTabHot = (TextView) parentView.findViewById(R.id.tv_tab_2);

        tvTabNew.setOnClickListener(new MyOnClickListener(0));
        tvTabHot.setOnClickListener(new MyOnClickListener(1));
    }

    private void InitViewPager(View parentView) {
        mPager = (ViewPager) parentView.findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();

        home1 = new VideolistviewFragment();
        home2 = new VideogridviewFragment();

        fragmentsList.add(home1);
        fragmentsList.add(home2);

        mPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentsList));
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
        mPager.setCurrentItem(0);

    }

    @Override
    public void intoItem(int position) {

    }

    @Override
    public void onClick(View v) {

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

    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
                case 0:
                    if (currIndex == 1) {
                        tvTabHot.setTextColor(resources.getColor(R.color.noselected));
                    }
                    tvTabNew.setTextColor(resources.getColor(R.color.selected));
                    break;
                case 1:
                    if (currIndex == 0) {
                        tvTabNew.setTextColor(resources.getColor(R.color.noselected));
                    }
                    tvTabHot.setTextColor(resources.getColor(R.color.selected));
                    break;
            }
            currIndex = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
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
}