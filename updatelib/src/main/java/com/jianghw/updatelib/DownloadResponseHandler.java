package com.jianghw.updatelib;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by jhwei on 2016/10/18.
 * <p>
 * Describe:
 */
public class DownloadResponseHandler {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int NETWORK_OFF = 4;
    private static final int PROGRESS_CHANGED = 5;
    private final Handler handler;
    private final String localFilePath;
    private final UpdateDownloadListener updateDownloadListener;
    private boolean isDownloading = false;
    private int mCompleteSize;


    public DownloadResponseHandler(String localFilePath, UpdateDownloadListener updateDownloadListener) {
        this.localFilePath = localFilePath;
        this.updateDownloadListener = updateDownloadListener;
        this.isDownloading = true;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                handleSelfMessage(msg);
            }
        };
    }

    private void handleSelfMessage(Message msg) {
        Object[] response;
        switch (msg.what) {
            case FAILURE_MESSAGE://失败
                response = (Object[]) msg.obj;
                handleFailureMessage((FailureCode) response[0]);
                break;
            case PROGRESS_CHANGED://进度条
                response = (Object[]) msg.obj;
                int p = ((Integer) response[0]).intValue();
                handlerProgressChangedMessage(p);
                break;
            case FINISH_MESSAGE://完成
                onFinish();
                break;
        }
    }

    private void handleFailureMessage(FailureCode failureCode) {
        onFailure(failureCode);
    }

    private void handlerProgressChangedMessage(int progress) {
        updateDownloadListener.onProgressChanged(progress, "");
    }

    private void onFinish() {
        updateDownloadListener.onFinished(mCompleteSize, "");
    }

    private void onFailure(FailureCode failureCode) {
        updateDownloadListener.onFailure(failureCode);
    }

    /**
     * 文件下载方法
     *
     * @param inputStream
     * @param contentLength
     */
    public void sendResponseMessage(InputStream inputStream, int contentLength) {
        byte[] buffer = new byte[1024 * 2];
        int length = -1;
        int limit = 0;//更新频率
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
            while ((length = inputStream.read(buffer)) != -1) {
                if (isDownloading) {
                    randomAccessFile.write(buffer, 0, length);
                    mCompleteSize += length;
                    if (mCompleteSize < contentLength) {
                        int progress = (int) (mCompleteSize * 100 / contentLength);
                        if (limit % 30 == 0 && progress <= 100) {
                            sendProgressChangedMessage(progress);
                        }
                        if (progress >= 100) {
                            //下载100完成
                            sendProgressChangedMessage(progress);
                        }
                        limit++;
                    }
                }
            }
            //完成
            sendFinishMessage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendProgressChangedMessage(int progress) {
        sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[]{progress}));
    }

    /**
     * 进度条消息
     *
     * @param responseMessage
     * @param objects
     */
    private Message obtainMessage(int responseMessage, Object objects) {
        Message msg;
        if (handler != null) {
            msg = handler.obtainMessage(responseMessage, objects);
        } else {
            msg = Message.obtain();
            msg.what = responseMessage;
            msg.obj = objects;
        }
        return msg;
    }

    /**
     * 发送消息统一点
     *
     * @param message
     */
    private void sendMessage(Message message) {
        if (handler != null) {
            handler.sendMessage(message);
        } else {
            handleSelfMessage(message);
        }
    }

    /**
     * 完成消息
     */
    private void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }
}
