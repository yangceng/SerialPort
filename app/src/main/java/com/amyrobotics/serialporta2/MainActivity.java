package com.amyrobotics.serialporta2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amyrobotics.serialport.LogUtils;
import com.amyrobotics.serialport.head.ControlHeadChannel;
import com.amyrobotics.serialport.mic.ControlMicChannel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        ControlHeadChannel.getInstance().connect();

//        startServer();
    }

    private void startServer() {
        Intent serviceIntent = new Intent(this, SerialPortService.class);
        startService(serviceIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ControlHeadChannel.getInstance().disconnect();
    }

    private void initView() {
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:

                ControlHeadChannel.getInstance().warning();
                break;
        }
    }
}
