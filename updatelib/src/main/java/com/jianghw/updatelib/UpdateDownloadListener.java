package com.jianghw.updatelib;

/**
 * Created by jhwei on 2016/10/18.
 * <p>
 * Describe:
 */

public interface UpdateDownloadListener {
    void onStarted();

    void onProgressChanged(int progress, String s);

    void onFinished(int mCompleteSize, String s);

    void onFailure(FailureCode failureCode);

}
