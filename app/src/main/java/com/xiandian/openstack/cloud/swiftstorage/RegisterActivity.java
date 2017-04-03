package com.xiandian.openstack.cloud.swiftstorage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.api.TokensResource;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Role;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.xiandian.openstack.cloud.swiftstorage.base.TaskResult;
import com.xiandian.openstack.cloud.swiftstorage.sdk.connector.AndroidOpenStackClientConnector;
import com.xiandian.openstack.cloud.swiftstorage.sdk.service.OpenStackClientService;
import com.xiandian.openstack.cloud.swiftstorage.utils.PromptDialogUtil;

import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private static String OPENSTACK_IP = "";
    private static String OPENSTACK_USER_NAME = "admin";// admin
    private static String OPENSTACK_USER_PW = "000000";// b35C26j3"; //000000
    private static String keystoneAuthUrl = "http://" + OPENSTACK_IP
            + ":35357/v2.0";
    private String username;
    private String password;
    private String email;
    private Access access;
    private Keystone keystone;
    private Tenant tenant;
    private User user;
    private Role role;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        EditText txtusername = (EditText) findViewById(R.id.txtUsername);
        EditText txtpassword = (EditText) findViewById(R.id.txtPassword);
        EditText txtemail = (EditText) findViewById(R.id.txtemail);
        username = txtusername.getText().toString();
        password = txtpassword.getText().toString();
        email = txtemail.getText().toString();
        Button button = (Button) findViewById(R.id.btnregister);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterTask registerTask = new RegisterTask();
                registerTask.execute();
            }
        });
    }


    public class RegisterTask extends AsyncTask<String, Object, TaskResult<Access>> {
        /**
         * 异步UI调用。
         */
        @Override
        protected TaskResult<Access> doInBackground(String... params) {
            OpenStackClientService service = OpenStackClientService.getService();
            try {
                keystone = new Keystone(keystoneAuthUrl, new AndroidOpenStackClientConnector());
                TokensResource.Authenticate abc = keystone.tokens().authenticate(
                        new UsernamePassword(OPENSTACK_USER_NAME, OPENSTACK_USER_PW));
                access = abc.execute();
                access = keystone.tokens().authenticate(new TokenAuthentication(access.getToken().getId())).withTenantName("admin").execute();
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
                // 认证成功，执行创建租户异步任务
                CreateTenantsTask getTenantsTask = new CreateTenantsTask();
                getTenantsTask.execute();

            } else {
                PromptDialogUtil.showDialog(RegisterActivity.this, getString
                                (R.string.alert_title_login_auth),
                        getString(R.string.alert_error_login_auth_user_password), null);
            }
        }
    }

    /**
     * 创建租户
     */
    private class CreateTenantsTask extends AsyncTask<String, Object, TaskResult<Tenant>> {

        @Override
        protected TaskResult<Tenant> doInBackground(String... params) {
            OpenStackClientService osServcie = OpenStackClientService.getService();
            try {
                //设置租户的属性
                tenant = new Tenant();
                tenant.setName(username);
                tenant.setDescription(username);
                tenant.setEnabled(true);
                //在keystone中创建租户
                keystone = new Keystone(keystoneAuthUrl);
                keystone.token(access.getToken().getId());
                tenant = keystone.tenants().create(tenant).execute();
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
                //创建租户成功
                CreateUserTask createUserTask = new CreateUserTask();
                createUserTask.execute();

            } else {
                //创建租户失败
                PromptDialogUtil.showDialog(RegisterActivity.this, getString
                                (R.string.alert_error_login_set_tenant),
                        getString(R.string.alert_error_login_set_tenant), null);
            }
        }
    }

    /**
     * 创建用户
     */
    private class CreateUserTask extends AsyncTask<String, Object,
            TaskResult<User>> {


        @Override
        protected TaskResult<User> doInBackground(String... params) {
            OpenStackClientService osServcie = OpenStackClientService.getService();
            try {
                //设置用户的属性
                user = new User();
                user.setName(username);
                user.setPassword(password);
                user.setEnabled(true);
                user.setEmail(email);
                user.setTenantId(tenant.getId());
                //在keystone中创建用户
                keystone = new Keystone(keystoneAuthUrl);
                keystone.token(access.getToken().getId());
                user = keystone.users().create(user).execute();
                return new TaskResult<User>(user);
            } catch (Exception e) {
                osServcie.resetConnection();
                return new TaskResult<User>(e);
            }
        }

        @Override
        protected void onPostExecute(TaskResult<User> result) {
            if (result.isValid()) {
                //创建用户成功
                CreateRoleTask createRoleTask = new CreateRoleTask();
                createRoleTask.execute();
            } else {
                //创建用户失败
                PromptDialogUtil.showDialog(RegisterActivity.this, getString
                                (R.string.alert_error_login_set_user),
                        getString(R.string.alert_error_login_set_user), null);
            }
        }
    }

    /**
     * 创建角色
     */
    private class CreateRoleTask extends AsyncTask<String, Object,
            TaskResult<Role>> {


        @Override
        protected TaskResult<Role> doInBackground(String... params) {
            OpenStackClientService osServcie = OpenStackClientService.getService();
            try {
                //设置用户角色属性
                role = new Role();
                role.setName(username);
                role.setDescription(username);
                //在keystone中创建角色
                keystone = new Keystone(keystoneAuthUrl);
                keystone.token(access.getToken().getId());
                role = keystone.roles().create(role).execute();
                return new TaskResult<Role>(role);
            } catch (Exception e) {
                osServcie.resetConnection();
                return new TaskResult<Role>(e);
            }
        }

        @Override
        protected void onPostExecute(TaskResult<Role> result) {
            if (result.isValid()) {
                //创建角色成功
                PromptDialogUtil.showDialog(RegisterActivity.this, getString
                                (R.string.set_seccess),
                        getString(R.string.set_seccess), null);

            } else {
                //创建角色失败
                PromptDialogUtil.showDialog(RegisterActivity.this, getString
                                (R.string.alert_error_login_set_role),
                        "", null);
            }
        }
    }
}
