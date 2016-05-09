package net.poringsoft.timesignal;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 時報表示用フラグメント
 * Created by mry on 15/05/06.
 */
public class TimeSignalFragment extends Fragment {
    //定数
    //----------------------------------------------------------
    private static final int UPDATE_INTERVAL = 10;
    private static final String KEY_NTP_SABUN = "ntpSabun";

    //フィールド
    //----------------------------------------------------------
    private TextView m_timeTextView;
    private TextView m_diffTextView;
    private TextView m_ntpTextView;
    private ScheduledExecutorService m_clockService;
    private ScheduledFuture m_clockFuture;
    private Vibrator m_vibrator;
    private SoundPlayer m_tickSound;
    private SoundPlayer m_finishSound;

    private String m_preClockText = "";
    private List<String> m_countdownClockTextList;

    private boolean m_useSoundTick = false;
    private boolean m_useVibTick = false;
    private int m_nowDiffMillisec = 0;
    private int m_vibTickMillisec = 100;
    private int m_vibFinishMilisec = 1000;

    private int m_ntpSabun = 0;

    //メソッド
    //----------------------------------------------------------
    /**
     * Fragmentのビュー要求
     * Fragmentで表示するビューの生成を行う
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_timesignal, container, false);
    }

    /**
     * ActivityのonCreate完了通知
     * 親Activityの生成完了後に必要なデータの生成
     * @param savedInstanceState セーブデータ
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PSDebug.d("call");

        //フィールド初期化
        m_vibrator = (Vibrator)getActivity().getSystemService(Activity.VIBRATOR_SERVICE);
        m_timeTextView = (TextView)getActivity().findViewById(R.id.timeTextView);
        m_diffTextView = (TextView)getActivity().findViewById(R.id.diffTextView);
        m_ntpTextView = (TextView)getActivity().findViewById(R.id.ntpTextView);

        if (savedInstanceState != null)
        {
            PSDebug.d("savedInstanceState復元");
            m_ntpSabun = savedInstanceState.getInt(KEY_NTP_SABUN, 0);
        }
        if (m_ntpSabun == 0)
        {
            updateNtpTimeAsync();
        }
        else
        {
            updateSabunTextView(0 - m_ntpSabun, m_ntpTextView, "NTP補正時間：");
        }
    }

    /**
     * データの保存
     * @param outState 保存用バンドル
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PSDebug.d("call");

        outState.putInt(KEY_NTP_SABUN, m_ntpSabun);
    }

    /**
     * 復旧時
     */
    @Override
    public void onResume() {
        super.onResume();
        PSDebug.d("call");

        PSUtils.ScreenSleepEnable(getActivity(), false);
        loadEnvOption();

        //サウンド初期化
        //音源：http://www.kurage-kosho.info/system.html
        m_tickSound = new SoundPlayer(getActivity(), R.raw.se_pre_2);
        m_finishSound = new SoundPlayer(getActivity(), R.raw.se_signal_2);

        //時間タイマー初期化
        m_clockService = Executors.newSingleThreadScheduledExecutor();
        m_clockFuture = m_clockService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                updateTimeClock();
            }
        }, UPDATE_INTERVAL, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    /**
     * 環境設定の読み込み
     */
    private void loadEnvOption()
    {
        //設定値初期化
        m_useSoundTick = EnvOption.getTimeUseCountdownSound(getActivity());
        m_useVibTick = EnvOption.getTimeUseCountdownVibration(getActivity());
        m_vibTickMillisec = EnvOption.getTimeVibTickMillisec(getActivity());
        m_vibFinishMilisec = EnvOption.getTimeVibFinishMillisec(getActivity());

        //カウントダウンで音を鳴らす秒数文字列リストを作成する
        int tickCount = EnvOption.getTimeCountdownCount(getActivity());
        m_countdownClockTextList = new ArrayList<String>();
        for (int i=0; i<tickCount; i++)
        {
            int time = 59 - i;
            if (time > 0)
            {
                m_countdownClockTextList.add(String.format("%1$02d", time));
            }
        }

        //手動調整ミリ秒
        m_nowDiffMillisec = EnvOption.getTimeDifferenceMillsec(getActivity());
        updateSabunTextView(m_nowDiffMillisec, m_diffTextView, "手動補正時間：");
    }

    /**
     * 停止時
     */
    @Override
    public void onPause() {
        super.onPause();
        PSDebug.d("call");

        PSUtils.ScreenSleepEnable(getActivity(), true);

        //リソース解放
        m_tickSound.release();
        m_finishSound.release();
        m_clockFuture.cancel(true);
        m_clockService.shutdown();
        m_clockService = null;
    }

    /**
     * 時報表示時間を更新する
     * また更新時に音・バイブを発生させる
     */
    private void updateTimeClock()
    {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, m_nowDiffMillisec);   //指定ミリ秒数の手動調整
        now.add(Calendar.MILLISECOND, 0 - m_ntpSabun);      //NT時刻との差分を調整

        final String timeText = DateFormat.format("kk:mm:ss", now).toString();
        if (!timeText.equals(m_preClockText))   //秒単位の変化があった時
        {
            m_preClockText = timeText;
            if (m_preClockText.endsWith("00"))
            {
                //ちょうど分が変化した時
                if (m_useSoundTick) {
                    m_finishSound.play();
                }
                if (m_useVibTick) {
                    m_vibrator.vibrate(m_vibFinishMilisec);
                }
            }
            else
            {
                //指定秒数までのカウントダウン開始
                for (String tick : m_countdownClockTextList)
                {
                    if (m_preClockText.endsWith(tick))
                    {
                        if (m_useSoundTick) {
                            m_tickSound.play();
                        }
                        if (m_useVibTick) {
                            m_vibrator.vibrate(m_vibTickMillisec);
                        }
                    }
                }
            }

            //描画更新
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_timeTextView.setText(timeText);
                }
            });
        }
    }

    /**
     * ミリ秒調整を行った結果をテキストビューに設定する
     * @param sabun 調整ミリ秒
     * @param sabunTextView テキストビュー
     * @param textPrefix 表示する接頭語
     */
    private void updateSabunTextView(int sabun , TextView sabunTextView, String textPrefix)
    {
        double sec = (double)(sabun / 1000.0F);
        String text = String.format("%.3f", sec);
        if (sabun != 0 && !text.startsWith("-"))
        {
            text = "+" + text;
        }
        sabunTextView.setText(textPrefix + " " + text + " 秒");
    }

    /**
     * NT時刻を非同期で取得開始する
     */
    private void updateNtpTimeAsync()
    {
        if (!EnvOption.getTimeUseNtp(getActivity()))
        {
            m_ntpTextView.setText("NTPサーバー時刻補正無効");
            return;
        }
        SntpTimeUpdateAsyncTask task = new SntpTimeUpdateAsyncTask();
        task.execute(EnvOption.getTimeNtpServer(getActivity()));
    }

    /**
     * NTP時刻非同期取得タスク
     */
    public class SntpTimeUpdateAsyncTask extends AsyncTask<String, String, Integer> {
        private static final int RET_ERROR_SNTP = 0xFFFFFFFF;

        /**
         * 更新前処理
         */
        @Override
        protected void onPreExecute() {
            m_ntpSabun = 0;
        }

        /**
         * 処理開始
         * 空文字を指定するとすべてのデータを取得する
         * @param text NTPサーバーURL
         * @return NTPサーバーとの差分時間（ミリ秒）
         */
        @Override
        protected Integer doInBackground(String... text) {
            String url = text[0];

            SntpClient sntp = new SntpClient();
            int result = RET_ERROR_SNTP;
            if (sntp.requestTime(url, 10000))
            {
                long ntpNow = sntp.getNtpTime() + SystemClock.elapsedRealtime() - sntp.getNtpTimeReference();
                long localNow = Calendar.getInstance().getTime().getTime();
                result = (int)(localNow - ntpNow);
                PSDebug.d("NT時刻取得成功 差分時刻=" + result);
            }
            else
            {
                PSDebug.d("NTP時刻取得失敗");
                result = RET_ERROR_SNTP;
            }
            return result;
        }

        /**
         * 完了処理
         * @param result 現在時刻とNTP時刻との差分（ミリ秒）＋の場合は端末側が進んでいる、-の場合は端末側が遅れている
         */
        @Override
        protected void onPostExecute(Integer result) {
            if (result != RET_ERROR_SNTP)
            {
                m_ntpSabun = result;
                updateSabunTextView(0 - m_ntpSabun, m_ntpTextView, "NTP補正時間：");
            }
            else
            {
                m_ntpTextView.setText("NTPサーバー時刻取得失敗");
            }
        }
    }
}
