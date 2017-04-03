package com.xiandian.openstack.cloud.swiftstorage.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiandian.openstack.cloud.swiftstorage.R;
import com.xiandian.openstack.cloud.swiftstorage.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件ListView适配器。JavaBean 数据SFileData。
 * 对应组件为MainFragment重的ListView。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */
public class SFileListViewAdapter extends BaseAdapter {

    //列表中的数据
    private List<SFileData> listFileData;
    //对应的context
    private Context context;
    //所在Fragment
    private ItemClickCallable fragment;

    //升序排列
    public static final int SORT_ASCEND = 0;
    //降序排序
    public static final int SORT_DESCEND = 1;

    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_TIME = 1;
    public static final int SORT_BY_SIZE = 2;

    /**
     * 构造器。
     *
     * @param context
     * @param listFileData
     * @param fragment
     */
    public SFileListViewAdapter(Context context, List<SFileData> listFileData, ItemClickCallable fragment) {

        this.listFileData = listFileData;
        this.context = context;
        this.fragment = fragment;
        //默认按照名称进行排序
        sort(true, SORT_BY_NAME, SORT_ASCEND);
    }



    /**
     * 对数据进行排序，创建对象后，应该紧跟调用这个方法，不应该notifyDataSetChanged之后。
     *
     * @param isDiffDirFile 是否区分目录和文件
     * @param type          排序类型，名称，时间，大小
     * @param mode          排序类型，是降序还是升序
     */
    public void sort(boolean isDiffDirFile, int type, int mode) {
        Comparator com = getSortComparator(type, mode);
        if (isDiffDirFile) {
            if (this.listFileData != null) {
                //排序文件夹
                List<SFileData> tempDir = new ArrayList<SFileData>();
                for (SFileData item : listFileData) {
                    if (item.isFolder()) {
                        tempDir.add(item);
                    }
                }
                synchronized (tempDir) {
                    Collections.sort(tempDir, com);
                }
                //排序文件
                List<SFileData> tempFile = new ArrayList<SFileData>();
                for (SFileData item : listFileData) {
                    if (!item.isFolder()) {
                        tempFile.add(item);
                    }
                }
                synchronized (tempFile) {
                    Collections.sort(tempFile, com);
                }
                //排序合并
                this.listFileData.clear();
                this.listFileData.addAll(tempDir);
                this.listFileData.addAll(tempFile);
                //clear to gc
                tempDir.clear();
                tempFile.clear();
            }
        } else {
            synchronized (listFileData) {
                Collections.sort(listFileData, com);
            }
        }

    }

    /**
     * 获得排序器。
     * @param type          排序类型，名称，时间，大小
     * @param mode          排序类型，是降序还是升序
     * @return
     */
    private Comparator getSortComparator(int type, int mode) {
        switch (type) {
            case SORT_BY_NAME:
                return new FileNameComparator(mode);
            case SORT_BY_SIZE:
                return new SizeComparator(mode);
            case SORT_BY_TIME:
                return new TimeComparator(mode);
            default:
                return new FileNameComparator(mode);
        }
    }

    /**
     * 数量。
     *
     * @return
     */
    @Override
    public int getCount() {
        return listFileData.size();
    }

    /**
     * 按照索引号查询。
     *
     * @param index
     * @return
     */
    @Override
    public Object getItem(int index) {

        return listFileData.get(index);
    }

    /**
     * 对象ID同位置一致。
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 获取展示当前Item的视图，可以根据布局创建，也可以自己创建。
     *
     * @param position    索引位置。
     * @param convertView 旧的展示视图，可能为null，如果为null，需要创建。
     * @param parent      父视图。
     */

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //根据布局创建main_list_item.xml
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.main_list_item, null);
        }

        ProgressBar progressBar= (ProgressBar) convertView.findViewById(R.id.down_pb);

        //图标
        ImageView img = (ImageView) convertView.findViewById(R.id.file_icon);
        TextView fileName = (TextView) convertView.findViewById(R.id.file_name);
        TextView modTime = (TextView) convertView.findViewById(R.id.file_lastModified);
        TextView fileSize = (TextView) convertView.findViewById(R.id.file_size);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.file_checked);

        checkBox.setChecked(listFileData.get(position).isChecked());
        if(listFileData.get(position).ispb()==1){
            progressBar.setVisibility(View.VISIBLE);
            modTime.setVisibility(View.GONE);
            fileSize.setVisibility(View.GONE);
        }else if (listFileData.get(position).ispb()==0){
            progressBar.setVisibility(View.GONE);
            modTime.setVisibility(View.VISIBLE);
            fileSize.setVisibility(View.VISIBLE);
        }
        //监听整合列表项目Item
        LinearLayout layoutItem = (LinearLayout) convertView.findViewById(R.id.main_list_layoutItem);

       // img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file_doc));
        //Bug: Android 5.1 Image Invisible!!!
        if(listFileData.get(position).getImage()!=null){
            img.setImageBitmap(listFileData.get(position).getImage());
        }else {
            img.setImageResource(listFileData.get(position).getImageResource());
        }


        fileName.setText(listFileData.get(position).getFileName());
        //区分目录和图片
        boolean isFolder = listFileData.get(position).isFolder();
        modTime.setText(listFileData.get(position).getLastModified());
        if (isFolder) {
            fileSize.setText("");
        } else {
            fileSize.setText(DisplayUtils.bytesToHumanReadable(listFileData.get(position).getFileSize()));
        }

        layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //进入下一级目录
                fragment.intoItem(position);
            }
        });
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //数据记录选中
                listFileData.get(position).setChecked(b);
            }
        });

        return convertView;
    }

    /////////////////////////////比较器////////////////////////////////////////////

    /**
     * 按照文件名称进行升序或降序排序。
     */
    private class FileNameComparator implements Comparator<SFileData> {
        private int descend = SORT_ASCEND;

        public FileNameComparator(int descend) {
            this.descend = descend;
        }

        @Override
        public int compare(SFileData arg0, SFileData arg1) {
            String fname1 = arg0.getFileName();
            String fname2 = arg1.getFileName();
            if (descend == SORT_DESCEND) {
                //英文字符不区分大小写
                return fname2.toLowerCase().compareTo(fname1.toLowerCase());
            } else {
                //英文字符不区分大小写
                return fname1.toLowerCase().compareTo(fname2.toLowerCase());
            }
        }
    }


    /**
     * 按照文件大小进行升序或降序排序。
     */
    private class SizeComparator implements Comparator<SFileData> {
        private int descend = SORT_ASCEND;

        public SizeComparator(int descend) {
            this.descend = descend;
        }

        @Override
        public int compare(SFileData arg0, SFileData arg1) {
            try {
                int s0 = (int) arg0.getFileSize();
                int s1 = (int) arg0.getFileSize();
                ;
                return (descend == SORT_DESCEND) ? s1 - s0 : s0 - s1;
            } catch (Exception exp) {
                return 0;
            }
        }
    }

    /**
     * 按照时间进行升序或降序排序。
     */
    private class TimeComparator implements Comparator<SFileData> {
        private int descend = 0;

        public TimeComparator(int descend) {
            this.descend = descend;
        }

        @Override
        public int compare(SFileData arg0, SFileData arg1) {
            try {
                long s0 = arg0.getLastModifiedTime();
                long s1 = arg0.getLastModifiedTime();
                return (int) ((descend == SORT_DESCEND) ? s1 - s0 : s0 - s1);
            } catch (Exception exp) {
                return 0;
            }
        }
    }

    ///////////////////////抽取接口//////////////////>
    /**
     * 点击子项目，通知外部进行处理。
     */
    public interface ItemClickCallable
    {

        /**
         * 点击条目。
         * @param position
         */
        public void intoItem(int position);
    }
    ///////////////////////抽取接口//////////////////<

}
