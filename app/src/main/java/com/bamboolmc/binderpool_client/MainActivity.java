package com.bamboolmc.binderpool_client;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bamboolmc.binderpool.BinderPool;
import com.bamboolmc.binderpool.ICompute;
import com.bamboolmc.binderpool.ISecurityCenter;

/**
 * 摘自 开发艺术详解＋
 * http://vlambda.com/wz_wXIgSL7BiV.html
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BinderPool mBinderPool;
    Button mButtonAdd;
    Button mButtonSec;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mButtonAdd.setOnClickListener(MainActivity.this);
            mButtonSec.setOnClickListener(MainActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButtonAdd = (Button) findViewById(R.id.email_sign_in_button2);
        mButtonSec = (Button) findViewById(R.id.email_sign_in_button1);
        getBinderPool();

    }

    private void getBinderPool() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mBinderPool = BinderPool.getInstance(MainActivity.this);
                mHandler.obtainMessage().sendToTarget();

            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.email_sign_in_button1:
                    IBinder securityBinder = mBinderPool.queryBinder(BinderPool.BINDER_SECURITY);
                    ISecurityCenter mSecurityCenter = (ISecurityCenter) ISecurityCenter.Stub.asInterface(securityBinder);
                    String msg = "helloword-Android";
                    String password = mSecurityCenter.encrypt(msg);
                    Toast.makeText(MainActivity.this, password + "\n" + mSecurityCenter.decrypt(password), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.email_sign_in_button2:
                    IBinder computeBinder = mBinderPool.queryBinder(BinderPool.BINDER_COMPUTE);
                    ICompute mCompute = ICompute.Stub.asInterface(computeBinder);
                    int result = mCompute.add(12, 12);
                    Toast.makeText(MainActivity.this, result + "", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
