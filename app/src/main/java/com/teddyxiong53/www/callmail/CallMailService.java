package com.teddyxiong53.www.callmail;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallMailService extends Service {

    public static final String TAG = "CallMailService";
    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor;

    private String mSenderEmail = null;
    private String mSenderPassword = null;
    private String mReceiverEmail = null;

    private TelephonyManager mTelMng;
    private PhoneStateListener phoneStateMng;
    public static  String currentNumber;

    public void initParam() {
        preferences = getSharedPreferences("callmail", MODE_PRIVATE);
        editor = preferences.edit();

        mSenderEmail = preferences.getString("sender_email","callmail123@163.com");
        mSenderPassword = preferences.getString("sender_password","callmail8888");
        mReceiverEmail = preferences.getString("receiver_email", "");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        //startCallMailService();

        super.onCreate();
    }

    public void startCallMailService() {
		
        mTelMng = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        Log.d(TAG, "startCallMailService");
		
        phoneStateMng = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                //Log.d("xhl", "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //Log.d("xhl", "idle................");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //Log.d("xhl", "offhook................");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        //Log.d("xhl", "calling................");
                        Toast.makeText(getApplicationContext(), incomingNumber + "calling", Toast.LENGTH_SHORT).show();
                        Thread newThread;
                        currentNumber = incomingNumber;

                        newThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sendMail(currentNumber);
                            }
                        });
                        newThread.start();
                        break;
                }


            }
        };
        mTelMng.listen(phoneStateMng, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void stopCallMailService() {
        Log.d(TAG, "stopCallMailService");
        mTelMng.listen(phoneStateMng, PhoneStateListener.LISTEN_NONE);
    }
    private String mSubject = "CallMail | Call | ";
    private String mContent = "Call";

    private void sendMail(String number) {
		initParam();
        MailSenderInfo info = new MailSenderInfo();
        info.setMailServerHost("smtp.163.com");
        //info.setMailServerHost("smtp.qq.com");
        mSubject = "CallMail | Call | ";
        mSubject +=number;
        info.setMailServerPort("25");
        info.setValidate(true);
        info.setUserName(mSenderEmail);
        info.setPassword(mSenderPassword);
        info.setSenderEmail(mSenderEmail);
        info.setReceiverEmail(mReceiverEmail);
        info.setSubject(mSubject);
        info.setContent(mContent);
        SimpleMailSender sms = new SimpleMailSender();
        sms.sendTextEmail(info);
    }

    public CallMailService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        startCallMailService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "CallMailService destroy");
        stopCallMailService();
        super.onDestroy();
    }
}
