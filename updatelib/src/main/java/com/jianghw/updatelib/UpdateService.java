package com.jianghw.updatelib;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.File;

/**
 * Created by jhwei on 2016/10/18.
 * <p>
 * Describe:
 */

public class UpdateService extends Service {
    private String apkURL;
    private String filePath;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        filePath = Environment.getExternalStorageDirectory() + "/autoupdate/update.apk";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser("下载失败", "", 0);
            stopSelf();
        }
        apkURL = intent.getStringExtra("apkUrl");
        notifyUser("下载开始", "", 0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        UpdateManager.getInstance().startDownload(apkURL, filePath, new UpdateDownloadListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                notifyUser("正在下载", "", progress);
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                notifyUser("下载完成", "", 100);
                stopSelf();
            }

            @Override
            public void onFailure(FailureCode failureCode) {
                notifyUser("下载失败", "", 0);
                stopSelf();
            }
        });
    }

    /**
     * 更新notification来告知用户下载进度
     *
     * @param result
     * @param reason
     * @param progress
     */
    private void notifyUser(String result, String reason, int progress) {
        Notification mNotification;
        NotificationCompat.Builder build = new NotificationCompat.Builder(this);
        build.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name));
        if (progress > 0 && progress < 100) {
            build.setProgress(100, progress, false);
        } else {
            build.setProgress(0, 0, false);
        }

        build.setAutoCancel(false);
        build.setWhen(System.currentTimeMillis());
        build.setTicker(result);
        build.setContentIntent(progress >= 100 ? getContentIntent() : PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotification = build.build();
        notificationManager.notify(0, mNotification);
    }

    public PendingIntent getContentIntent() {
        File apkFile = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()), "application/vnd.android.package-archive");
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
