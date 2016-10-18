package com.jianghw.updatelib;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jhwei on 2016/10/18.
 * <p>
 * Describe:
 */

public class UpdateDownloadRequest implements Runnable {

    private final String downloadUrl;
    private final DownloadResponseHandler downloadHandler;

    public UpdateDownloadRequest(String downloadUrl, String localFilePath, UpdateDownloadListener updateDownloadListener) {
        this.downloadUrl = downloadUrl;
        this.downloadHandler = new DownloadResponseHandler(localFilePath, updateDownloadListener);
    }

    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 建立连接
     *
     * @throws IOException
     * @throws InterruptedException
     */
    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();//阻塞我们当前的线程
                if (!Thread.currentThread().isInterrupted()) {
                    //文件下载
                    downloadHandler.sendResponseMessage(connection.getInputStream(),connection.getContentLength());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
