package com.rojao.customviewtrainning;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.rojao.customviewtrainning.widget.WaveView;

public class MainActivity extends AppCompatActivity {

    private WaveView mWaveView;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mWaveView.setProgress(msg.what);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWaveView = (WaveView) findViewById(R.id.id_waveView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int i =0;
                    while(i<=100){
                        Thread.sleep(500);
                        mHandler.sendEmptyMessage(i);
                        i++;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
