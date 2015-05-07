package net.poringsoft.timesignal;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

/**
 * メイン画面
 *
 * 効果音：http://www.kurage-kosho.info/system.html
 */
public class MainActivity extends ActionBarActivity {
    //定数
    //----------------------------------------------------------


    //フィールド
    //----------------------------------------------------------


    //メソッド
    //----------------------------------------------------------
    /**
     * 画面起動時処理
     * @param savedInstanceState 保存データ
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PSDebug.d("call");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new TimeSignalFragment()).commit();
        }
        /*
        //タブの設定
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);
        tabHost.addTab(tabHost.newTabSpec(TimeSignalFragment.class.getName())
                .setIndicator(getString(R.string.tab_timesignal)), TimeSignalFragment.class, null);
        //tabHost.addTab(tabHost.newTabSpec(DayDiffFragment.class.getName())
        //        .setIndicator(getString(R.string.tab_daydiff)), DayDiffFragment.class, null);
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                PSDebug.d("タブ変更 id=" + tabId);
            }
        });
        */
    }

    /**
     * 復旧時
     */
    @Override
    protected void onResume() {
        super.onResume();
        PSDebug.d("call");
    }

    /**
     * 停止時
     */
    @Override
    protected void onPause() {
        super.onPause();
        PSDebug.d("call");
    }

    /**
     * メニュー表示設定
     * @param menu メニュー
     * @return 設定時はtrue
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * メニュー項目選択イベント
     * @param item 選択メニューアイテム
     * @return 選択処理を行ったかどうか
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, PrefActivity.class);
            this.startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
