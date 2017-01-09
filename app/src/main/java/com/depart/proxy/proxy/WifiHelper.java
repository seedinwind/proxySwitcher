package com.depart.proxy.proxy;

import android.content.Context;
import android.net.ProxyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Yuan Jiwei on 17/1/9.
 */

public class WifiHelper {
    private static final String SSID_SYD_WIRELESS = "XXXXXXX";
    public static final String SP_KEY_IDENTITY = "identity";
    public static final String SP_KEY_PW = "password";
    private static final String PROXY_RC_HOST = "xx.xx.xx.xx";
    private static final String PROXY_RC_PORT = "8888";
    private static final String PROXY_DEV_HOST = "xx.xx.xx.xx";
    private static final String PROXY_DEV_PORT = "8888";
    private static final String DEFAULT_USER = "xxxxxxx";
    private static final String DEFAULT_PW = "********";
    private WifiManager mWifiManager;

    public WifiHelper(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public WifiInfo getWifiConfig() {
        return mWifiManager.getConnectionInfo();
    }

    public WifiConfiguration createWifiInfo(int type) {
        WifiConfiguration config;
        WifiConfiguration tempConfig = isExsits(SSID_SYD_WIRELESS);
        if (tempConfig != null) {
            config = tempConfig;
            mWifiManager.removeNetwork(tempConfig.networkId);
        } else {
            config = new WifiConfiguration();
            config.SSID = "\"" + SSID_SYD_WIRELESS + "\"";
        }

        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
        config.enterpriseConfig = new WifiEnterpriseConfig();
        config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
        config.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
        config.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
        config.enterpriseConfig.setIdentity(TextUtils.isEmpty(SharePreferencesUtil.get(SP_KEY_IDENTITY)) ? DEFAULT_USER : SharePreferencesUtil.get(SP_KEY_IDENTITY));
        config.enterpriseConfig.setAnonymousIdentity("");
        config.enterpriseConfig.setPassword(TextUtils.isEmpty(SharePreferencesUtil.get(SP_KEY_PW)) ? DEFAULT_PW : SharePreferencesUtil.get(SP_KEY_PW));

        String host = null;
        String port = null;
        switch (type) {
            case MainActivity.RC:
                host = PROXY_RC_HOST;
                port = PROXY_RC_PORT;
                break;
            case MainActivity.DEV:
                host = PROXY_DEV_HOST;
                port = PROXY_DEV_PORT;
                break;
            case MainActivity.NO_PROXY:
            default:
                break;
        }

        try {
            if (!TextUtils.isEmpty(host) && !TextUtils.isEmpty(port)) {
                setHttpPorxySetting(config, host, Integer.valueOf(port));
            } else {
                unSetHttpProxy(config);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                NoSuchFieldException e
                ) {
            Log.e("WifiHelper", e.getMessage(), e);
        }

        return config;
    }

    private WifiConfiguration isExsits(String ssid) {
        List<WifiConfiguration> configuration = mWifiManager.getConfiguredNetworks();
        if (configuration == null) {
            return null;
        }
        for (WifiConfiguration c : configuration) {
            String id = "\"" + ssid + "\"";
            if (id.equals(c.SSID)) {
                return c;
            }
        }
        return null;
    }


    /**
     * 设置代理信息 exclList是添加不用代理的网址用的
     */
    private void setHttpPorxySetting(WifiConfiguration config, String host, int port)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException, NoSuchFieldException {
        ProxyInfo mInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInfo = ProxyInfo.buildDirectProxy(host, port);
        }
        if (config != null) {
            Class clazz = Class.forName("android.net.wifi.WifiConfiguration");
            Class parmars = Class.forName("android.net.ProxyInfo");
            Method method = clazz.getMethod("setHttpProxy", parmars);
            method.invoke(config, mInfo);
            Object mIpConfiguration = ReflectHelper.getDeclaredFieldObject(config, "mIpConfiguration");
            ReflectHelper.setEnumField(mIpConfiguration, "STATIC", "proxySettings");
            ReflectHelper.setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);
        }

    }

    /**
     * 取消代理设置
     */
    public void unSetHttpProxy(WifiConfiguration config)
            throws ClassNotFoundException, InvocationTargetException, IllegalAccessException,
            NoSuchFieldException, NoSuchMethodException {
        ProxyInfo mInfo = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mInfo = ProxyInfo.buildDirectProxy(null, 0);
        }
        if (config != null) {
            Class clazz = Class.forName("android.net.wifi.WifiConfiguration");
            Class parmars = Class.forName("android.net.ProxyInfo");
            Method method = clazz.getMethod("setHttpProxy", parmars);
            method.invoke(config, mInfo);
            Object mIpConfiguration = ReflectHelper.getDeclaredFieldObject(config, "mIpConfiguration");
            ReflectHelper.setEnumField(mIpConfiguration, "NONE", "proxySettings");
            ReflectHelper.setDeclardFildObject(config, "mIpConfiguration", mIpConfiguration);
        }
    }


    public void updateNetWork(WifiConfiguration config) {
        int netID = mWifiManager.addNetwork(config);
        mWifiManager.enableNetwork(netID, true);
    }
}