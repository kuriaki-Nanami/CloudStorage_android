package com.xiandian.openstack.cloud.swiftstorage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.woorea.openstack.keystone.model.Access;
import com.xiandian.openstack.cloud.swiftstorage.fragment.CategoryFragment;
import com.xiandian.openstack.cloud.swiftstorage.fragment.GarbageFragment;
import com.xiandian.openstack.cloud.swiftstorage.fragment.ImgFragment;
import com.xiandian.openstack.cloud.swiftstorage.fragment.MainFragment;
import com.xiandian.openstack.cloud.swiftstorage.fragment.SFileEditable;
import com.xiandian.openstack.cloud.swiftstorage.fragment.VideoFragment;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.sdk.test.OpenStackSDKTest;
import com.xiandian.openstack.cloud.swiftstorage.utils.Constants;

/**
 * 主界面Activity类，父类AppCompatActivity类，AppCompatActivity的父类实现FragmentActivity。
 * 操作栏通过Toolbar实现。本类 实现导航选择监听器，操作栏操作监听。
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        SearchView.OnQueryTextListener,View.OnFocusChangeListener {//(1) 主类，实现导航切换OnNavigationItemSelectedListener接口

    //(3.1) 主要容器对应的视图
    //所有主容器对应的视图
    private MainFragment mainFragment;
    //文档对应的视图
    private CategoryFragment docCategoryFragment;
    //图片对应的视图
    private ImgFragment picCategoryFragment;
    //视频对应的视图
    private VideoFragment videoCategoryFragment;
    //音频对应的视图
    private CategoryFragment audioCategoryFragment;
    //回收站
    private GarbageFragment garbageFragment;

    //记录当前的Fragment，不同的Fragment控制回退有差别
    private SFileEditable currentFragment;
    //回退程序进行提醒，提醒过后再退出，退出程序
    private boolean confirmed = false;

    //Toolbar组件，不同的切换实现，展示不同的信息
    private Toolbar toolbar;
    //查找组件
    private SearchView searchView;

    /**
     * 创建视图。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {//(2)创建视图，主视图、工具栏、导航切换、导航操作监听
        super.onCreate(savedInstanceState);
        //主视图
        setContentView(R.layout.activity_main);
        //工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //导航抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //工具栏上抽屉展开和隐藏操作按钮监听器（ActionBarDrawerToggle实现DrawerLayout.DrawerListener）
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        // Synchronize the state of the drawer indicator/affordance with the linked DrawerLayout.
        toggle.syncState();

        //导航视图
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //监听（onNavigationItemSelected）
        navigationView.setNavigationItemSelectedListener(this);

        //(3.2)默认选择全部文件，
        FragmentManager fragmentManager = getSupportFragmentManager();
        mainFragment = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
        currentFragment = mainFragment;
        navigationView.setCheckedItem(R.id.nav_swiftdisk);

        //改变Toolbar的信息，进入所有，根目录
        setToolbarTitles(getString(R.string.menu_swiftdisk), "");
    }


    /**
     * 改变Toolbar的状态
     *
     * @param title
     * @param subTitle
     */
    public void setToolbarTitles(String title, String subTitle) {
        if (title != null) {
            toolbar.setTitle(title);
        }
        if (subTitle != null) {
            toolbar.setSubtitle(subTitle);
        }
    }


    /**
     * 回退操作。
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            //增加回退控制代码
            //(1) 如果当前是mainFragment，要回退子目录要回退上一级目录。
            boolean isExiting = true;
            if (mainFragment != null) {
                isExiting = mainFragment.onContextBackPress();
            }
            //如果到根目录，提示退出
            if (!isExiting) {
                if (confirmed) {
                    // 退出程序
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                } else {
                    Toast.makeText(this, R.string.alert_confirm_exit, Toast.LENGTH_SHORT).show();
                    confirmed = true;
                    //如果2秒内不按回退，不再回退
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            confirmed = false;
                        }
                    }, 2000);
                }
            }
        }
    }

    /**
     * 工具栏创建。
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//(4)创建操作栏
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(currentFragment==garbageFragment){
            menu.findItem(R.id.action_select_all).setVisible(false);
            menu.findItem(R.id.action_unselect_all).setVisible(false);
            menu.findItem(R.id.action_create_dir).setVisible(false);
            menu.findItem(R.id.action_download).setVisible(false);
            menu.findItem(R.id.action_upload).setVisible(false);
            menu.findItem(R.id.action_take_photo).setVisible(false);
            menu.findItem(R.id.action_rename).setVisible(false);
            menu.findItem(R.id.action_copy).setVisible(false);
            menu.findItem(R.id.action_details).setVisible(false);
            menu.findItem(R.id.action_move).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_record_video).setVisible(false);
            menu.findItem(R.id.action_record_audio).setVisible(false);
            menu.findItem(R.id.action_share).setVisible(false);
        }else if(currentFragment==mainFragment) {
            menu.findItem(R.id.action_restore).setVisible(false);
            menu.findItem(R.id.action_empty).setVisible(false);
        }
        else{
            menu.findItem(R.id.action_record_video).setVisible(false);
            menu.findItem(R.id.action_record_audio).setVisible(false);
            menu.findItem(R.id.action_search).setVisible(false);
            menu.findItem(R.id.action_move).setVisible(false);
            menu.findItem(R.id.action_copy).setVisible(false);
            menu.findItem(R.id.action_take_photo).setVisible(false);
            menu.findItem(R.id.action_upload).setVisible(false);
            menu.findItem(R.id.action_create_dir).setVisible(false);
            menu.findItem(R.id.action_restore).setVisible(false);
            menu.findItem(R.id.action_empty).setVisible(false);
        }
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        //Toolbar 回退操作，回到所有
        searchView.setOnFocusChangeListener(this);
        searchView
                .setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            currentFragment.refresh();
                        }
                    }
                });
        return true;
    }

    /**
     * 工具栏操作。
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//(5)工具栏选择操作
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //3 菜单定制 增加
        //查找
        if (id == R.id.action_search) {
           //这里不需要实现代码，被SearchView的监听处理替代
        }
        //分享
        else if (id == R.id.action_share){
            this.currentFragment.share();
        }
        //全选
        else if (id == R.id.action_select_all) {
            this.currentFragment.selectAll();


        }
        //全选
        else if (id == R.id.action_unselect_all) {
            this.currentFragment.unselectAll();

        }
        //新建目录
        else if (id == R.id.action_create_dir) {
            //弹出输入框
            this.currentFragment.createDir(null);
        }
        //下载
        else if(id == R.id.action_download){

            this.currentFragment.download();
        }
        //上传
        else if (id == R.id.action_upload) {
            this.currentFragment.upload();
        }
        //拍照
        else if (id == R.id.action_take_photo) {
            this.currentFragment.takePhoto();
        }
        //重定名
        else if (id == R.id.action_rename) {
            this.currentFragment.rename(null, null);
        }
        //复制
        else if (id == R.id.action_copy) {
            this.currentFragment.copy(null, null);
        }
        //移动
        else if (id == R.id.action_move) {
            this.currentFragment.move(null, null);
        }
        //删除
        else if (id == R.id.action_recycle) {
            this.currentFragment.recycle(null);
        }
        //详情
        else if (id == R.id.action_details) {
            this.currentFragment.details(0, true);
        }
        //还原
        else if (id== R.id.action_restore){
            currentFragment.restroe();
        }
        //清空
        else if (id== R.id.action_empty){
            currentFragment.empty();
        }
        //拍视频
        else if(id== R.id.action_record_video){
            this.currentFragment.recordvideo();
        }
        //录音
        else if (id== R.id.action_record_audio){
            this.currentFragment.recordaudio();
        }
        //排序
        else if (id== R.id.action_sort){
            this.currentFragment.sort();

        }
        return super.onOptionsItemSelected(item);
    }


    //

    /**
     * 用户SearchView输入字符时激发该方法。
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    // 单击搜索按钮时激发该方法

    /**
     * 用户SearchView输入字符时激发该方法。
     *
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        currentFragment.search(query);
        return false;
    }

    /**
     *
     * @param view
     * @param isFocusLost
     */
    @Override
    public void onFocusChange(View view, boolean isFocusLost) {

        if (!isFocusLost)
        {
            Toast.makeText(MainActivity.this, "back", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 导航切换操作。
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//导航选择操作
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        //所有文件
        if (id == R.id.nav_swiftdisk) {
            //(3.3) 当选择全部文件时
            if (mainFragment == null) {
                mainFragment = new MainFragment();
            }
            fragmentManager.beginTransaction().replace(R.id.container, mainFragment).commit();
            currentFragment = mainFragment;

            //改变工具栏
            setToolbarTitles(getString(R.string.menu_swiftdisk), "");

        }
        //文档
        else if (id == R.id.nav_doc) {

        }
        //图片
        else if (id == R.id.nav_pic) {

        }
        //音频
        else if (id == R.id.nav_music) {

        }
        //视频
        else if (id == R.id.nav_video) {

        }
        //回收站
        else if (id == R.id.nav_recycle) {

        }
        //点击后关闭导航面板
        invalidateOptionsMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /**
     * 重载onPostCreate，处理工具栏Title创建过程中设置被该方法覆盖。
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setToolbarTitles(getString(R.string.menu_swiftdisk), "");
    }




    //认证异步任务测试

    /**
     * 认证,成功后验证租户，租户有了获得容器，容器有了读对象。
     */
    private class AuthTestTask extends AsyncTask<String, Object, Access> {
        /**
         * 异步UI调用。
         */
        @Override
        protected Access doInBackground(String... params) {

            try {
                Access access = OpenStackSDKTest.test();
                return access;
            } catch (Exception e) {
                e.printStackTrace();
                OpenStackClientService.getInstance().resetConnection();
                return null;
            }
        }

        /**
         * 处理结果。
         */
        @Override
        protected void onPostExecute(Access access) {
            if (access == null) {

                Toast.makeText(MainActivity.this, "Auth Error", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(MainActivity.this, "Auth OK:" + access.getToken(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
