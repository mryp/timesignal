package net.poringsoft.timesignal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.SearchRecentSuggestions;

/**
 * 設定画面
 * Created by mry on 15/05/06.
 */
public class PrefActivity extends PreferenceActivity {
    /**
     * 設定画面起動時処理
     * @param savedInstanceState 保存データ
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);

        //バージョン情報セット
        PreferenceScreen versionScreen = (PreferenceScreen)this.findPreference("version_info");
        if (versionScreen != null) {
            String versionName = "X.X.X";
            PackageManager pm = this.getPackageManager();
            if (pm != null) {
                try {
                    PackageInfo info = pm.getPackageInfo(this.getPackageName(), 0);
                    versionName = info.versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    versionName = "X.X.X";
                }
            }
            versionScreen.setTitle("version " + versionName);
            versionScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(getString(R.string.url_poringsoft)));
                    startActivity(intent);
                    return true;
                }
            });
        }
    }
}
