package com.acmetensortoys.ctfwstimer.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Chronometer;
import android.widget.SearchView;
import android.widget.Toast;

import com.acmetensortoys.ctfwstimer.utils.CtFwSDisplayTinyChrono;
import com.acmetensortoys.ctfwstimer.R;
import com.acmetensortoys.ctfwstimer.service.MainService;
import com.acmetensortoys.ctfwstimer.utils.CheckedAsyncDownloader;

import java.io.File;

public class HandbookActivity extends CtFwSActivityBase {

    public static final String HAND_FILE_NAME = "handbook.html";
    private static final String TAG = "CtFwSHandbook";

    private CtFwSDisplayTinyChrono mTitleChronoObs;
    private WebView mWV;

    private void display() {
        final File dlf = new File(getFilesDir(), HAND_FILE_NAME);
        if (dlf.exists()) {
            /* render the version we've downloaded */
            mWV.loadUrl(dlf.toURI().toString());
        } else {
            /* render the version we were shipped with instead */
            mWV.loadUrl("file:///android_asset/hand.html");
        }
    }

    private final MainService.Observer mSrvObs = new MainService.Observer() {
        @Override
        public void onMqttServerChanged(MainService.LocalBinder b, String sURL) {
            ;
        }

        @Override
        public void onMqttServerEvent(MainService.LocalBinder b, MainService.MqttServerEvent mse) {
            ;
        }

        @Override
        public void onHandbookFetch(MainService.LocalBinder b, CheckedAsyncDownloader.DL dl) {
            display();
            if (dl != null && dl.getResult() == CheckedAsyncDownloader.Result.RES_OK) {
                Toast.makeText(HandbookActivity.this,
                                R.string.hand_new,
                                Toast.LENGTH_SHORT)
                     .show();
            }
        }
    };

    protected void doRegisterObservers() {
        mSrvBinder.registerObserver(mSrvObs);
        if (mTitleChronoObs != null) {
            mSrvBinder.getGameState().registerObserver(mTitleChronoObs);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handbook);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_name_hand_title);
        }

        mWV = findViewById(R.id.hand_wv);

        WebSettings wvs = mWV.getSettings();
        wvs.setBuiltInZoomControls(true);
        wvs.setDisplayZoomControls(false);

        // mWV.setFindListener()

        display();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.handmenu, menu);

        Chronometer ch = (Chronometer) menu.findItem(R.id.hand_menu_chrono).getActionView();
        mTitleChronoObs = new CtFwSDisplayTinyChrono(getResources(), ch);

        if (mSrvBinder != null) {
            doRegisterObservers();
        }

        SearchView sv = (SearchView) menu.findItem(R.id.hand_menu_search).getActionView();
        sv.setQueryHint("Search handbook...");
        sv.setSubmitButtonEnabled(true);

        /*
        sv.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "sv.OClickL");
            }
        });

        sv.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "sv.OCloseL");
                return false;
            }
        });
        */

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mWV.findNext(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mWV.findAllAsync(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hand_menu_search:
                return true;
        }

        return false;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        if (mSrvBinder != null) {
            mSrvBinder.getGameState().unregisterObserver(mTitleChronoObs);
            mSrvBinder.unregisterObserver(mSrvObs);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
