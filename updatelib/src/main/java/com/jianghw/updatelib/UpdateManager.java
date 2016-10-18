package com.jianghw.updatelib;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jhwei on 2016/10/18.
 * <p>
 * Describe:
 */

public class UpdateManager {

    private final ExecutorService threadPoolExecutor;
    private UpdateDownloadRequest request;

    private UpdateManager() {
        threadPoolExecutor = Executors.newCachedThreadPool();
    }

    static UpdateManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        static final UpdateManager INSTANCE = new UpdateManager();
    }

    void startDownload(String downloadUrl, String localFilePath, UpdateDownloadListener updateDownloadListener) {
        if (request != null) return;
        checkLocalFilePath(localFilePath);

        //开始文件的下载任务
        request = new UpdateDownloadRequest(downloadUrl, localFilePath, updateDownloadListener);
        Future<?> future = threadPoolExecutor.submit(request);
    }

    /**
     * 用来检查文件路径是否已经存在
     *
     * @param localPath
     */
    private void checkLocalFilePath(String localPath) {
        File dir = new File(localPath.substring(0, localPath.lastIndexOf("/") + 1));
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(localPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
