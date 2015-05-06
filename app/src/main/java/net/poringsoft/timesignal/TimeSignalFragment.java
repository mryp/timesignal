package net.poringsoft.timesignal;

import android.app.Activity;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableFuture;
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
    private static final int TICK_COUNT = 10;
    private static final int VIB_TIME_TICK = 100;
    private static final int VIB_TIME_FINISH = 1000;
    private static final int NOW_DIFF_TIME = 0;

    //フィールド
    //----------------------------------------------------------
    private TextView m_timeTextView;
    private ScheduledExecutorService m_clockService;
    private ScheduledFuture m_clockFuture;
    private Vibrator m_vibrator;
    private SoundPlayer m_tickSound;
    private SoundPlayer m_finishSound;

    private String m_preClockText = "";
    private List<String> m_countdownClockTextList;

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

        //カウントダウンで音を鳴らす秒数文字列リストを作成する
        m_countdownClockTextList = new ArrayList<>();
        for (int i=0; i<TICK_COUNT; i++)
        {
            int time = 59 - i;
            if (time > 0)
            {
                m_countdownClockTextList.add(String.format("%1$02d", time));
            }
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
    }

    /**
     * 復旧時
     */
    @Override
    public void onResume() {
        super.onResume();
        PSDebug.d("call");

        PSUtils.ScreenSleepEnable(getActivity(), false);

        //サウンド初期化
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

    private void updateTimeClock()
    {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, NOW_DIFF_TIME);   //秒調整時間を加算

        final String timeText = DateFormat.format("HH:mm:ss", now).toString();
        if (!timeText.equals(m_preClockText))   //秒単位の変化があった時
        {
            m_preClockText = timeText;
            if (m_preClockText.endsWith("00"))
            {
                //ちょうど分が変化した時
                m_finishSound.play();
                m_vibrator.vibrate(VIB_TIME_FINISH);
            }
            for (String tick : m_countdownClockTextList)
            {
                if (m_preClockText.endsWith(tick))
                {
                    //指定秒数までのカウントダウン開始
                    m_tickSound.play();
                    m_vibrator.vibrate(VIB_TIME_TICK);
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

}
