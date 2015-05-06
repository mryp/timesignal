package net.poringsoft.timesignal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 日差記録画面用フラグメント
 * Created by mry on 15/05/06.
 */
public class DayDiffFragment  extends Fragment {
    /**
     * ビュー生成
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daydiff, container, false);
    }
}