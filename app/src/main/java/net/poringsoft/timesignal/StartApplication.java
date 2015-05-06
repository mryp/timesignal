package net.poringsoft.timesignal;

import android.app.Application;

/**
 * アプリケーション起動時の処理
 */
public class StartApplication extends Application {
    /**
     * 起動時処理
     */
    @Override
    public void onCreate() {
        super.onCreate();
        PSDebug.d("call");

        //デバッグ状態セット
        PSDebug.initDebugFlag(getApplicationContext());

        //初期化
        EnvPath.init(getApplicationContext());
        PSDebug.d("RootPath=" + EnvPath.getRootDirPath());
    }
}
