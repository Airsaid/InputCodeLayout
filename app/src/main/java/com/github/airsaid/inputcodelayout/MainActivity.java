package com.github.airsaid.inputcodelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.airsaid.inputcodelayout.widget.InputCodeLayout;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = "MainActivity";

    private InputCodeLayout mInputCodeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SeekBar) findViewById(R.id.sbr_number)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sbr_divide_width)).setOnSeekBarChangeListener(this);
        ((SeekBar) findViewById(R.id.sbr_size)).setOnSeekBarChangeListener(this);

        mInputCodeLayout = (InputCodeLayout) findViewById(R.id.inputCodeLayout);
        mInputCodeLayout.setOnInputCompleteListener(new InputCodeLayout.OnInputCompleteCallback() {
            @Override
            public void onInputCompleteListener(String code) {
                Log.e(TAG, "输入的验证码为：" + code);
                Toast.makeText(MainActivity.this, "输入的验证码为：" + code, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void normal(View v){
        mInputCodeLayout.setShowMode(InputCodeLayout.NORMAL);
    }

    public void password(View v){
        mInputCodeLayout.setShowMode(InputCodeLayout.PASSWORD);
    }

    public void clear(View v){
        mInputCodeLayout.clear();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()){
            case R.id.sbr_number:
                mInputCodeLayout.setNumber(progress);
                ((TextView)findViewById(R.id.txt_number)).setText("修改输入框数量("+progress+")");
                break;
            case R.id.sbr_divide_width:
                mInputCodeLayout.setDivideWidth(dp2px(progress));
                ((TextView)findViewById(R.id.txt_divide_width)).setText("修改输入框间距("+progress+")");
                break;
            case R.id.sbr_size:
                mInputCodeLayout.setWidth(dp2px(progress));
                mInputCodeLayout.setHeight(dp2px(progress));
                ((TextView)findViewById(R.id.txt_size)).setText("修改输入框大小("+progress+")");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public int dp2px(float dpValue){
        return (int)(dpValue * (getResources().getDisplayMetrics().density) + 0.5f);
    }
}
