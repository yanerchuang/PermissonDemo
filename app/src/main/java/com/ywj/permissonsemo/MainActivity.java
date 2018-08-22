package com.ywj.permissonsemo;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionUtil.OnRequestPermissionsResultCallbacks {

    private final String TAG = MainActivity.class.getSimpleName();
    private final int REQUEST_CODE_SMS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    public void requestPermisson(View view) {
        //manifest.xml清单中需配置<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        //manifest.xml清单中需配置<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        String[] permissions = new String[]{Manifest.permission.READ_SMS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

        PermissionUtil.requestPerssions(this, REQUEST_CODE_SMS,permissions );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms, boolean isAllGranted) {
        Log.e(TAG,"同意:" + perms.size() + "个权限,isAllGranted=" + isAllGranted);
        for (String perm : perms) {
            Log.e(TAG,"同意:" + perm);
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms, boolean isAllDenied) {
        Log.e(TAG,"拒绝:" + perms.size() + "个权限,isAllDenied=" + isAllDenied);
        for (String perm : perms) {
            Log.e(TAG,"拒绝:" + perm);
        }
    }

    public void goPermissionsSettings(View view) {
        PermissionUtil.startApplicationDetailsSettings(this, 123);
    }

    public void isReadSMSPermissionDenied(View view) {
        //manifest.xml清单中需配置<uses-permission android:name="android.permission.READ_SMS" />
        boolean isForbidden  = PermissionUtil.deniedRequestPermissonsAgain(this, Manifest.permission.READ_SMS);
        Log.e(TAG,"读取短信权限是否禁止询问=" + isForbidden);
        Toast.makeText(this,"读取短信权限是否禁止询问=" + isForbidden,Toast.LENGTH_SHORT).show();
    }

    public void readSMS(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HashMap<String, String>> hashMaps = readAllSMS();
                for (HashMap<String, String> hashMap : hashMaps) {
                    Log.e(TAG,hashMap.get("addr") + "," + hashMap.get("person") + "," + hashMap.get("body"));
                }
            }
        }).run();
    }

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    private ArrayList<HashMap<String, String>> readAllSMS() {
        Cursor cursor = managedQuery(SMS_INBOX, new String[]{"address", "person", "body"},
                null, null, null);
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        if (cursor.moveToFirst()) {
            int addrIdx = cursor.getColumnIndex("address");
            int personIdx = cursor.getColumnIndex("person");
            int bodyIdx = cursor.getColumnIndex("body");
            do {
                String addr = cursor.getString(addrIdx);
                String person = cursor.getString(personIdx);
                String body = cursor.getString(bodyIdx);

                HashMap<String, String> item = new HashMap<String, String>();
                item.put("addr", addr);
                item.put("person", person);
                item.put("body", body);
                list.add(item);
            } while (cursor.moveToNext());
        }
        return list;
    }

}
