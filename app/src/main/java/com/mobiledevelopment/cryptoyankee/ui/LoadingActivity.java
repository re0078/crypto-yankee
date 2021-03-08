package com.mobiledevelopment.cryptoyankee.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.mobiledevelopment.cryptoyankee.MainActivity;
import com.mobiledevelopment.cryptoyankee.R;


public class LoadingActivity extends Activity {
    ProgressBar loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_main);
        loading = findViewById(R.id.loadingBar);
        reset();
    }

    @Override
    protected void onResume() {
        Toast.makeText(LoadingActivity.this, "Please Wait till loading is complete.", Toast.LENGTH_SHORT).show();
        super.onResume();
        // wait till some task is complete
        try {
            Thread.sleep(1000);
            loading.incrementProgressBy(25);
            Thread.sleep(1000);
            loading.incrementProgressBy(25);
            Thread.sleep(1000);
            loading.incrementProgressBy(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startActivity(new Intent(LoadingActivity.this, MainActivity.class));
    }

    private void reset(){
        loading.setProgress(25);
    }
}
