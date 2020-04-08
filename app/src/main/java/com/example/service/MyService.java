package com.example.service;
//1.創建Service 自動有了onBind(Intent intent),但出現因為尚未實作的例外
//2.看一下Service的生命週期呼叫 onCreate(),onStartCommand(Intent intent, int flags, int startId)(,onDestroy()

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    String TAG ="hank";
    int i= 1;
    Timer timer;
    public MyService() {
    }


    //1.創建Service 自動有了onBind(Intent intent),但出現因為尚未實作的例外
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return  null;
    }

    //1.StartService初次被啟用使(第一個被呼叫只做一次,做初始化的事情)
    @Override
    public void onCreate() {
        super.onCreate();

        //5.Service 創建時初始化計時器
        timer = new Timer();
        timer.schedule(new MyTask(),0,1000);
        Log.v(TAG,"onCreate:");
    }
    //2.每當有Acitvity用StartSevice時,會被呼叫(第二個被呼叫的,每次呼叫StartService都會被呼叫)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //8.onStartCommand當startService會被呼叫因此透過MainActivity傳來的Intent取得資料
        int rand = intent.getIntExtra("i",-1);
        i = rand;
        Log.v(TAG,"onStartCommand:" + "/Intent:" + intent + "/flags:" + flags +"/startId:" + startId);
        return super.onStartCommand(intent, flags, startId);
    }

    //3.當StropService時被呼叫,(測試結果關掉App也馬上呼叫SerVice被摧毀)
    @Override
    public void onDestroy() {
        super.onDestroy();
        //6.onDestroy時關掉季時
        if(timer != null){
            timer.cancel();
            timer.purge(); //刪除所有已取消的任務
            timer = null; //重新給空值
        }
        Log.v(TAG,"onDestroy:");
    }

    //4.計時Service秒數任務
    private class  MyTask extends TimerTask{
        @Override
        public void run() {
            Log.v(TAG,"Service存活秒數:" + i++ +"秒");
        }
    }
}