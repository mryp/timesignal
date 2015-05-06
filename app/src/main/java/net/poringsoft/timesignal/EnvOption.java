package net.poringsoft.timesignal;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.preference.PreferenceManager;

/**
 * 設定情報保持クラス
 */
public class EnvOption {
    //定数
    //---------------------------------------------------------
    //通信関連
    public static final String NET_GET_AGENT = "Mozilla/5.0 (Linux; Android; ja-jp;)";
    public static final int NET_GET_TIMEOUT = 20000;
    
    //設定値
    public static final String KEY_TIME_USE_COUNTDOWN_VIBRATION = "time_use_countdown_vibration";
    public static final String KEY_TIME_USE_COUNTDOWN_SOUND = "time_use_countdown_sound";
    public static final String KEY_TIME_COUNTDOWN_COUNT = "time_countdown_count";
    public static final String KEY_TIME_DIFFERENCE_MILLISEC = "time_difference_millisec";
    public static final String KEY_TIME_VIB_TICK_MILLISEC = "time_vib_tick_millsec";
    public static final String KEY_TIME_VIB_FINISH_MILLISEC = "time_vib_finish_millsec";
    public static final String KEY_TIME_NTP_SERVER = "time_ntp_server";

    //共通メソッド
    //---------------------------------------------------------
    /**
     * 指定した文字列を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putString(Context context, String key, String value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putString(key, value).apply();
    }

    /**
     * 設定ファイルに保存されている文字列を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static String getString(Context context, String key, String def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String result = pref.getString(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }

    /**
     * 指定した整数値を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putInt(Context context, String key, int value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putInt(key, value).apply();
    }

    /**
     * 設定ファイルに保存されている整数値を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static int getInt(Context context, String key, int def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        int result = pref.getInt(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }

    /**
     * 指定した論理値を設定ファイルに保存する
     * @param context コンテキスト
     * @param key 保存キー
     * @param value 保存値
     */
    private static void putBoolean(Context context, String key, boolean value) {
        PSDebug.d("key=" + key + " value=" + value);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        pref.edit().putBoolean(key, value).apply();
    }

    /**
     * 設定ファイルに保存されている論理値を取得する
     * @param context コンテキスト
     * @param key 保存キー
     * @param def 保存されていなかったときのデフォルト値
     * @return 取得したデータ
     */
    private static boolean getBoolean(Context context, String key, boolean def) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean result = pref.getBoolean(key, def);
        PSDebug.d("key=" + key + " value=" + result + " def=" + def);
        return result;
    }
    
    //時報関連
    //-------------------------------------------
    public static boolean getTimeUseCountdownVibration(Context context) {
        return getBoolean(context, KEY_TIME_USE_COUNTDOWN_VIBRATION, true);
    }

    public static boolean getTimeUseCountdownSound(Context context) {
        return getBoolean(context, KEY_TIME_USE_COUNTDOWN_SOUND, true);
    }

    public static int getTimeCountdownCount(Context context) {
        return Integer.parseInt(getString(context, KEY_TIME_COUNTDOWN_COUNT, "5"));
    }

    public static int getTimeDifferenceMillsec(Context context) {
        return Integer.parseInt(getString(context, KEY_TIME_DIFFERENCE_MILLISEC, "0"));
    }

    public static int getTimeVibTickMillisec(Context context) {
        return Integer.parseInt(getString(context, KEY_TIME_VIB_TICK_MILLISEC, "100"));
    }

    public static int getTimeVibFinishMillisec(Context context) {
        return Integer.parseInt(getString(context, KEY_TIME_VIB_FINISH_MILLISEC, "1000"));
    }

    public static String getTimeNtpServer(Context context) {
        return getString(context, KEY_TIME_NTP_SERVER, "ntp.nict.jp");
    }
}
