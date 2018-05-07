package com.bamboolmc.binderpool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.concurrent.CountDownLatch;

/**
 * Created by limc on 18/5/6.
 */

public class BinderPool {
    private static final String TAG = "BinderPool";

    public static final int BINDER_NONE = -1;
    public static final int BINDER_COMPUTE = 0;
    public static final int BINDER_SECURITY = 1;

    private Context context;
    private IBinderPool mBinderPool;
    private static BinderPool sInstance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;

    public BinderPool(Context context) {
        this.context = context.getApplicationContext();
        connectBindPoolService();

    }

    //单例，保证只有一个BinderPool
    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private synchronized void connectBindPoolService() {
        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);

        Intent service = new Intent("com.bamboolmc.binderpool.BINDER_POOL_SERVICE");
//        Intent service = new Intent(context, BinderPoolService.class);
        context.bindService(service, mBinderConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //调用内部类里面查询Binder方法
    public IBinder queryBinder(int binderCode) {
        IBinder binder = null;
        try {
            if (mBinderPool != null) {
                binder = mBinderPool.queryBinder(binderCode);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return binder;
    }

    //实现ServiceConnection来绑定Service
    private ServiceConnection mBinderConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeath, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeath = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            //BinderPool断开口重新连接，并重新绑定
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeath, 0);
            mBinderPool = null;
            connectBindPoolService();

        }
    };


}
