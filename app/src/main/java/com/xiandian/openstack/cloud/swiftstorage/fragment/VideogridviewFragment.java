package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 62566 on 2016/10/9.
 */
public class VideogridviewFragment extends VideoFragment
         {
    //Log 信息标签。
    private String TAG = MainFragment.class.getSimpleName();
    //图片工具类
    FileIconHelper fileIconHelper;
    //Context
    private Context context;
    //图片数组
    Bitmap[] img;
    //图片文字数组
    String[] imgtxt;
    //图片地址
    ArrayList<String> imglist=new ArrayList<String>();
    //File data model
    List<SFileData> fileListData = new ArrayList<SFileData>();
    //当前展示的文件列表（注意：太多临时变量不容易维护）
    private List<SFile> swiftFiles;
    //下拉刷新
    private SwipeRefreshLayout fileListSwipe;
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
        context = this.getActivity();
        //(1) Inflate the layout for this fragment
        fileIconHelper=new FileIconHelper(context);
        View rootView = inflater.inflate(R.layout.img_videogridview, container, false);
        //下拉刷新
        fileListSwipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_files2);
        fileListSwipe.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fileListSwipe.setOnRefreshListener(this);//增加刷新方法
        GridView gridView=(GridView)rootView.findViewById(R.id.img_video);
        context = getActivity();
        fileIconHelper=new FileIconHelper(context);
        img=new Bitmap[swiftFiles.size()];
        imgtxt= new String[swiftFiles.size()];

        for(int i=0;i<fileListData.size();i++){
            img[i]=fileListData.get(i).getImage();
        }
        //获取GridView组件//定义并初始化保存图片的数组
        for (int i=0;i<fileListData.size();i++){
            imgtxt[i]=fileListData.get(i).getFileName();
        }
        //定义并初始化保存说明文字的数组

        PictureAdapter adapter = new PictureAdapter(imgtxt, img, getActivity());
        gridView.setAdapter(adapter);//将适配器与gridView关联
        gridView.setOnItemClickListener(new ItemClickListener());
        return rootView;
    }
    class  ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            openFile(swiftFiles.get(position));
        }
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



    //自定义适配器
    class PictureAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<Picture> pictures;

        public PictureAdapter(String[] titles, Bitmap[] images, Context context)
        {
            super();
            pictures = new ArrayList<Picture>();
            inflater = LayoutInflater.from(context);
            for (int i = 0; i < images.length; i++)
            {
                Picture picture = new Picture(img[i],imgtxt[i]);
                pictures.add(picture);
            }
        }

        @Override
        public int getCount()
        {
            if (null != pictures)
            {
                return pictures.size();
            } else
            {
                return 0;
            }
        }

        @Override
        public Object getItem(int position)
        {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder viewHolder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.grad_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.img_name);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.img_bitmap);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(pictures.get(position).getTitle());

            return convertView;
        }

    }
    class ViewHolder
    {
        public TextView title;
        public ImageView image;
    }

    class Picture extends Object {
        private String title;
        private Bitmap imageId;

        public Picture()
        {
            super();
        }

        public Picture(Bitmap imageId, String title)
        {
            super();
            this.imageId = imageId;
            this.title = title;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public Bitmap getImageId()
        {
            return imageId;
        }

        public void setImageId(Bitmap imageId)
        {
            this.imageId = imageId;
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

}

