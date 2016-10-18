package com.jianghw.updatelib;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class UpdateActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 111;

    //apk下载链接
    private static final String APK_DOWNLOAD_URL = "http://app.xiaomi.com/download/421138?ref=search";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
    }

    private void chechVersion() {
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle("发现新版本");
        mDialog.setMessage("为了给大家提供更好的用户体验，每次应用的更新都包含速度和稳定性的提升，感谢您的使用！");
        mDialog.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //请求存储权限
                boolean hasPermission = (ContextCompat.checkSelfPermission(UpdateActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(UpdateActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
                    ActivityCompat.shouldShowRequestPermissionRationale(UpdateActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    //下载
                    startDownload();
                    dialog.dismiss();
                }
            }
        });
        mDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //获取到存储权限,进行下载
                    startDownload();
                } else {
                    Toast.makeText(UpdateActivity.this, "不授予存储权限将无法进行下载!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 启动下载
     */
    private void startDownload() {
        Intent it = new Intent(UpdateActivity.this, UpdateService.class);
        //下载地址
        it.putExtra("apkUrl", APK_DOWNLOAD_URL);
        startService(it);
    }
}
