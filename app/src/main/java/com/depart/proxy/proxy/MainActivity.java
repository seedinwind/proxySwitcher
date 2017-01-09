package com.depart.proxy.proxy;

import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int NO_PROXY = 0;
    public static final int RC = 1;
    public static final int DEV = 2;
    private WifiHelper mWifiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        SharePreferencesUtil.init(this);
        if (TextUtils.isEmpty(SharePreferencesUtil.get(WifiHelper.SP_KEY_IDENTITY))
                | TextUtils.isEmpty(SharePreferencesUtil.get(WifiHelper.SP_KEY_PW))) {
            Toast.makeText(this, "请先设置登录账户和密码", Toast.LENGTH_SHORT).show();
        }
        mWifiHelper = new WifiHelper(this);
        RadioGroup proxySetting = (RadioGroup) findViewById(R.id.proxy_setting);
        proxySetting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int type = NO_PROXY;
                switch (i) {
                    case R.id.rc:
                        type = RC;
                        break;
                    case R.id.dev:
                        type = DEV;
                        break;
                    case R.id.no_proxy:
                    default:
                        break;
                }
                WifiConfiguration config = mWifiHelper.createWifiInfo(type);
                mWifiHelper.updateNetWork(config);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.setting){
            startActivity(new Intent(this,AccountActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}