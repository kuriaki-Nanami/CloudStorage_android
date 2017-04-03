package com.xiandian.openstack.cloud.swiftstorage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.swift.model.Container;
import com.xiandian.openstack.cloud.swiftstorage.base.TaskResult;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.utils.PromptDialogUtil;

/**
 * 登录Activity，实现用户输入账号密码，通过Option设置OpenStack IP地址。
 *
 * 登录进行Keystone认证，租户获取，Swift服务链接和默认容器检查。
 *
 * @author 云计算应用与开发项目组
 * @since  V1.0
 */

public class LoginActivity extends Activity {

    /**
     * 进度条（旋转）
     */
    protected ProgressBar progressBar;
    /**
     * 用户名称组件.
     */
    private EditText txtUsername;
    /**
     * 用户名称组件.
     */
    private EditText txtPassword;


    //Option设置IP地址
    private AlertDialog alertDialog;
    private String userName;
    private String tenantName;
    private String userPassword;
    private String containerName;
    private String garbageContainerName;
    private String openstackSwiftIP;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //(1) 增加视图，并对组件进行初始化
        setContentView(R.layout.login);

        // 默认进度条隐藏
        progressBar = (ProgressBar) findViewById(R.id.servicesProgressBar);
        progressBar.setIndeterminate(true);
        progressBar.bringToFront();
        progressBar.setVisibility(View.INVISIBLE);
        //获得输入输出组件
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        Button btLogin = (Button) findViewById(R.id.btnLogin);
        btLogin.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                //(3) 进行认证，涉及网络访问，采用AsyncTask任务来完成
                login();
            }
        });
        TextView register= (TextView) findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    //(2) 增加右键菜单设置HostIP地址。

    /**
     * Option菜单，用于设置登录网站。
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_option, menu);
        return true;
    }

    /**
     * Option启动设置IP地址。
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // 配置IP的对话框
            AlertDialog.Builder builder = new Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.login_option_setting, null);
            // 设置前一次设置或者默认
            final EditText txtUrl = (EditText) view.findViewById(R.id.txtUrl);
            txtUrl.setText(AppState.getInstance().getOpenStackIP());
            view.findViewById(R.id.btnEnter).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    AppState.getInstance().setOpenStackIP(txtUrl.getText().toString());
                    // 缓存
                    SharedPreferences setting = getSharedPreferences("setting", Context.MODE_PRIVATE);
                    Editor editor = setting.edit();
                    editor.putString("url", AppState.getInstance().getOpenStackIP());
                    editor.commit();
                    Toast.makeText(LoginActivity.this, getString(R.string.alert_successful), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
            view.findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            builder.setTitle(getString(R.string.title_swift_IP));

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//(3) 进行认证，涉及网络访问，采用AsyncTask任务来完成

    /**
     * 登录。步骤包括，认证，获得租户， 根据租户获得容器，根据容器获得对象。
     */
    public void login() {

        userName = txtUsername.getText().toString().trim();
        userPassword = txtPassword.getText().toString().trim();
        openstackSwiftIP = AppState.getInstance().getOpenStackIP().trim();

        tenantName = userName;// 目前环境用户名称和租户名称一致
        containerName = tenantName;// 目前主容器和租户名称一致
        garbageContainerName = "garbage_" + containerName;// 目前回收站容器在主容器名称前garbage_

//        Toast.makeText(this, "登录测试，" + " 输入信息:" + userName + ":" + userPassword + ":"
//                + openstackSwiftIP, Toast.LENGTH_LONG).show();


        String keystoneAuthUrl="http://"+openstackSwiftIP+":5000/v2.0";
        OpenStackClientService service = OpenStackClientService.getInstance();
        service.setKeystoneAuthUrl(keystoneAuthUrl);
        service.setKeystoneAdminAuthUrl(keystoneAuthUrl);
        service.setKeystoneEndpoint(keystoneAuthUrl);
        service.setOpenstackIP(openstackSwiftIP);

        service.setTenantName(tenantName);
        service.setKeystoneUsername(userName);
        service.setKeystonePassword(userPassword);

       // (3. 1). 认证异步任务
        AuthTask authTask = new AuthTask();
        authTask.execute();

    }
    //(3. 1). 认证异步任务
    /**
     * 认证,成功后验证租户，租户有了获得容器，容器有了读对象。
     */
    private class AuthTask extends AsyncTask<String, Object, TaskResult<Access>> {
        /**
         * 异步UI调用。
         */
        @Override
        protected TaskResult<Access> doInBackground(String... params) {
            OpenStackClientService service = OpenStackClientService.getService();
            try {
                Keystone keystone = service.getKeystone();
                Access access = service.getAccess();
                return new TaskResult<Access>(access);
            } catch (Exception e) {
                e.printStackTrace();
                service.resetConnection();
                return new TaskResult<Access>(e);
            }
        }
        /**
         * 处理结果。
         */
        @Override
        protected void onPostExecute(TaskResult<Access> result) {
            if (result.isValid()) {
                //(3. 2). 认证成功，执行获取租户异步任务
                GetTenantsTask getTenantsTask = new GetTenantsTask();
                getTenantsTask.execute();

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                PromptDialogUtil.showDialog(LoginActivity.this, getString
                                (R.string.alert_title_login_auth),
                        getString(R.string.alert_error_login_auth_user_password), null);
            }
        }
    }

    //(3. 2). 认证成功，执行获取租户异步任务
    /**
     * 获取Tenant,成功后获取容器。
     */
    private class GetTenantsTask extends AsyncTask<String, Object, TaskResult<Tenant>> {

        @Override
        protected TaskResult<Tenant> doInBackground(String... params) {
            OpenStackClientService osServcie = OpenStackClientService.getService();
            try {
                Tenant tenant = osServcie.getTenant();
                return new TaskResult<Tenant>(tenant);
            } catch (Exception e) {
                osServcie.resetConnection();
                return new TaskResult<Tenant>(e);
            }
        }

        /**
         * 处理结果。
         */
        @Override
        protected void onPostExecute(TaskResult<Tenant> result) {
            if (result.isValid()) {
                //(3. 3). 获取租户成功，获取租户对应的容器的异步操作
                AppState.getInstance().setSelectedTenant((Tenant) result.getResult());
                // 获取Container
                GetContainerTask getContainerTask = new GetContainerTask();
                getContainerTask.execute();

            } else {
                progressBar.setVisibility(View.INVISIBLE);
                PromptDialogUtil.showDialog(LoginActivity.this, getString
                                (R.string.alert_error_login_get_tenant),
                        getString(R.string.alert_error_login_get_tenant), null);
            }
        }
    }
    //(3. 3). 获取租户成功，获取租户对应的容器的异步操作
    /**
     * 获取Container
     */
    private class GetContainerTask extends AsyncTask<String, Object,
            TaskResult<Container>> {

        @Override
        protected TaskResult<Container> doInBackground(String... params) {
            OpenStackClientService osServcie = OpenStackClientService.getService();
            try {
                Container container = osServcie.getContainer(containerName);
                return new TaskResult<Container>(container);
            } catch (Exception e) {
                return new TaskResult<Container>(e);
            }
        }

        /**
         * 处理结果。
         */
        @Override
        protected void onPostExecute(TaskResult<Container> result) {

            if (result.isValid()) {
                //(3. 4). 如果没有容器，创建2个容器（containerName, garbageContainerName)
                if (result.getResult() == null) {
                    // 创建容器
                    CreateContainerTask createContainerTask = new CreateContainerTask();
                    createContainerTask.execute();
                } else {
                    //缓存容器
                    //(3. 5). 2个容器（containerName, garbageContainerName)存在，启动进入主界面
                    AppState.getInstance().setSelectedContainer((Container)
                            result.getResult());
                    // 切换到主界面
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                PromptDialogUtil.showDialog(LoginActivity.this, getString
                                (R.string.alert_error_login_get_container),
                        getString(R.string.alert_error_login_get_container), null);
            }
        }
    }

    //(3. 4). 如果没有容器，创建2个容器（containerName, garbageContainerName)
    /**
     * 如果容器没有，默认为改用户，使用租户名称创建2个容器。
     */
    private class CreateContainerTask extends AsyncTask<String, Object,
            TaskResult<Container>> {
        protected TaskResult<Container> doInBackground(String... params) {
            try {
                OpenStackClientService osServcie = OpenStackClientService.getService();
                Container mainContainer = osServcie.createContainter(containerName);
                Container garbageContainer = osServcie.createContainter
                        (garbageContainerName);
                return new TaskResult<Container>(mainContainer);
            } catch (Exception e) {
                return new TaskResult<Container>(e);
            }
        }

        protected void onPostExecute(TaskResult<Container> result) {
            progressBar.setVisibility(View.GONE);
            //(3. 6). 2个容器（containerName, garbageContainerName)创建成功，启动进入主界面
            if (result.isValid()) {
                AppState.getInstance().setSelectedContainer((Container)
                        result.getResult());
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                PromptDialogUtil.showDialog(LoginActivity.this, getString
                                (R.string.alert_error_login_create_container),
                        getString(R.string.alert_error_login_create_container), null);
            }
        }
    }

}