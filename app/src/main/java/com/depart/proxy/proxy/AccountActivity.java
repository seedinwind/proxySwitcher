package com.depart.proxy.proxy;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Yuan Jiwei on 17/1/9.
 */

public class AccountActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_account);
    }
}