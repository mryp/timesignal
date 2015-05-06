package net.poringsoft.timesignal;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * 効果音再生クラス
 * Created by mry on 15/05/06.
 */
public class SoundPlayer {
    //フィールド
    //-------------------------------------------------
    private Context m_context;
    private SoundPool m_soundPool;
    private int m_soundId;
    private boolean m_isReady = false;


    //メソッド
    //-------------------------------------------------
    /**
     * コンストラクタ
     * @param context 画面コンテキスト
     * @param resId 再生リソースID
     */
    public SoundPlayer(Context context, int resId)
    {
        m_context = context;
        m_soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        m_soundId = m_soundPool.load(m_context, resId, 0);
        m_soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                m_soundPool.play(m_soundId, 0.0F, 0.0F, 0, 0, 1.0F);    //音なしで一度鳴らす
                m_isReady = true;
            }
        });
    }

    /**
     * 再生開始
     */
    public void play()
    {
        if (m_isReady)
        {
            m_soundPool.play(m_soundId, 1.0F, 1.0F, 0, 0, 1.0F);
        }
    }

    /**
     * リソース解放
     */
    public void release()
    {
        m_soundPool.release();
        m_soundPool = null;
    }
}
