package com.example.service;
//Google文件:A_Service,參考網站:https://blog.csdn.net/javazejian/article/details/52709857,
//:文件https://www.runoob.com/android/android-services.html
//:概念:https://windsuzu.github.io/learn-android-services/
//https://www.youtube.com/watch?v=ZudnSIsIjnA
//https://codertw.com/android-%E9%96%8B%E7%99%BC/346510/
/*//1.Service特性:
* 可以在背景中長時間執行操作的應用程式,且不提供使用者介面
* 就算你切換到別的應用程式,仍然在背景中執行
* 元件可以連接服務互動,甚至處理程序間通訊(IPC)
* ex:網路交易,撥放音樂,執行檔案輸入/輸出或與內容供應程式互動
* */

/*2.Service採取兩種形式
* 2.1:Started(啟動狀態):當Activity呼叫startService啟動Service時,服務即是啟動狀態,可無限次數在背景執行,就算Acitivty等啟動Service元件被銷毀也不影響
* 已啟動的服務會執行單一操作且不會將結果傳回呼叫端。例如，服務可能會透過網路下載或上傳檔案。 當操作完成時，服務應該會自行終結。
* 2.2:Bound(綁定狀態):當應用組件通過調用 bindService() 綁定到服務時，服務即處於“綁定”狀態。綁定服務提供了一個客戶端-服務器接口，允許組件與服務進行交互、發送請求、獲取結果，
* 甚至是利用進程間通信 (IPC) 跨進程執行這些操作。僅當與另一個應用組件綁定時，綁定服務才會運行。多個組件可以同時綁定到該服務，但全部取消綁定後，該服務即會被銷毀。
* 雖然是兩種形式,但Service兩種方式都可以執行,可以啟動Service無限次執行,也允許綁定
* 這與你是否實作兩種方法而定:onStartCommand() 允許元件啟動服務，而 onBind() 則允許繫結。
* 不論APP是否啟動,或是綁定,或者兩者都有,任何應用程式元件都可以使用Service(就算來自不同的應用程式),與任何元件可以使用Activity的方式相同使用Intent啟動Service
* */

/*3.Service在Manifast文件中的聲明
*前面說過Service分為啟動狀態和綁定狀態兩種，但無論是某些特定的服務啟動類型，都是通過繼承Service基類自定義而來，也都需要在AndroidManifest.xml中聲明，
* 那麼在分析 這兩種狀態之前，我們先來了解一下Service在AndroidManifest.xml中的聲明語法，其格式如下：
*<service android:description="string resource" //1.描述:一個向用戶描述服務的String。標籤應設置為對String資源的引用，以便可以像在用戶界面中的其他字符串一樣對其進行本地化
         android:directBootAware=["true" | "false"] //2.直接啟動意識:服務是否支持直接啟動；也就是說，它是否可以在用戶解鎖設備之前運行。注意：在Direct Boot期間 ，應用程序中的服務只能訪問存儲在 設備保護的存儲中的數據。默認值為false
         android:enabled=["true" | "false"] //3.啟動實例化:服務是否可以由系統實例化- true可以/false不行,預設為True
         android:exported=["true" | "false"] //4.外給調用:代表是否能被其他應用隱式調用，其替代值是由服務中有無intent-filter決定的，如果有intent-filter，則變為true，否則為false。為false的情況下，甚至有intent- filter匹配，也無法打開，即無法被其他應用隱式調用。
         android:foregroundServiceType=["connectedDevice" | "dataSync" |  //5.前台服務類型:ex: Location的話表示正在獲取當前位置
                                        "location" | "mediaPlayback" | "mediaProjection" |
                                        "phoneCall"]
         android:icon="drawable resource"  //5.設定伺服器圖片:請參見 <intent-filter>元素的 icon屬性
         android:isolatedProcess=["true" | "false"] //6.是否隔離過程:如果為True,則此服務將在與系統其餘部分隔離的特殊進程下運行，並且沒有自己的權限。與它的唯一通信是通過Service API（綁定和啟動）。
         android:label="string resource" //7.Service顯示於用戶的服務名稱:
         android:name="string" //8.對應Service的類別名稱
         android:permission="string"  //9.權限:實體必須具有的權限名稱才能啟動服務或綁定到該服務。如果主叫方 startService()， bindService()或者 stopService()，沒有被授予這個權限，該方法將無法正常工作，並意圖對象將不會被傳遞到服務。如果未設置此屬性，則由<application>元素的 permission 屬性設置的權限 適用於服務。如果未設置任何屬性，則該服務不受權限保護。
         android:process="string" > //10.處理:是否需要在單獨的進程中運行,當設置為android:process=”:remote”時，代表Service在單獨的進程中運行。注意“：”很重要，它的意思是指要在當前進程名稱前面附加上當前的包名，所以“remote”和”:remote”不是同一個意思，前者的進程名稱為：remote，而後者的進程名稱為：App-packageName:remote。
    . . .
</service>
* */

//4.創建Service
//5.MyService類別準備好
//6.這邊Activity,StartServie,跟StopServie看一下生命週期狀況
//7.這邊Startservice 產生亂數,當按下start服務時,讓onStartCommand接收

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnStartSer,btnStopSer;
    String TAG ="hank";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartSer = findViewById(R.id.btnStart);
        btnStopSer = findViewById(R.id.btnStop);
        btnStartSer.setOnClickListener(this);
        btnStopSer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                mStartService();
                break;
            case R.id.btnStop:
                mStopService();
                break;
        }
    }
    //停止Servie
    private void mStopService() {
        //停止Servie
        Intent intent = new Intent(this,MyService.class); //要Intent到的Service
        stopService(intent);//停止Service
        Log.v(TAG,"MainActivity =>mStopService()");
    }

    //啟動Servie
    private void mStartService() {
        //啟動Service
        Intent intent = new Intent(this,MyService.class);
        //7.這邊Startservice 產生亂數,當按下start服務時,產生int亂數資料,讓onStartCommand接收
        intent.putExtra("i",(int)(Math.random()*49+1));
        startService(intent); //啟動Service
        Log.v(TAG,"MainActivity =>mStartService()");
    }
}