package com.github.airsaid.inputcodelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.airsaid.inputcodelayout.widget.InputCodeLayout;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private InputCodeLayout mInputCodeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInputCodeLayout = (InputCodeLayout) findViewById(R.id.inputCodeLayout);
        mInputCodeLayout.setOnInputCompleteListener(new InputCodeLayout.OnInputCompleteCallback() {
            @Override
            public void onInputCompleteListener(String code) {
                Log.e(TAG, "输入的验证码为：" + code);
                Toast.makeText(MainActivity.this, "输入的验证码为：" + code, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void clear(View v){
        mInputCodeLayout.clear();
    }
}
