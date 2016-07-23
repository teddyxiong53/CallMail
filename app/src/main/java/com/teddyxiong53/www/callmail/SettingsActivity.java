package com.teddyxiong53.www.callmail;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/16 0016.
 */
public class SettingsActivity extends Activity{

    private SharedPreferences preferences ;
    private SharedPreferences.Editor editor;
    private EditText mSenderEmail;
    private EditText mSenderPassword;
    private EditText mReceiverEmail;

    private Map<String, String> mParam = new HashMap<>();

    private Button mSaveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.settings);
        init();
    }
    private void init() {
        mSenderEmail = (EditText)findViewById(R.id.sender_email);
        mSenderPassword = (EditText)findViewById(R.id.sender_password);
        mReceiverEmail = (EditText)findViewById(R.id.receiver_email);
        preferences = getSharedPreferences("callmail", MODE_PRIVATE);
        editor = preferences.edit();
        //mParam = (Map<String, ?>)preferences.getAll();
        String senderEmail = preferences.getString("receiver_email", "xxx@huawei.com");
        if(senderEmail.equals("xxx@huawei.com")) {
            //user hasn't set the param yet.
            String senderPassword = "callmail8888";
            String receiverEmail = "xxx@huawei.com";
            editor.putString("sender_email", "callmail123@163.com");
            editor.putString("sender_password", "callmail8888");
            editor.putString("receiver_email", "xxx@huawei.com");
            editor.commit();
            Log.d("xhl", "set param first time");

        }
        getParams();
        mSenderEmail.setText(mParam.get("sender_email"));
        mSenderPassword.setText(mParam.get("sender_password"));
        mReceiverEmail.setText(mParam.get("receiver_email"));

        mSaveButton = (Button)findViewById(R.id.save_button);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save param here
                editor.putString("sender_email", mSenderEmail.getText().toString());
                editor.putString("sender_password", mSenderPassword.getText().toString());
                editor.putString("receiver_email", mReceiverEmail.getText().toString());
                editor.commit();

                Toast.makeText(SettingsActivity.this, "Save params successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getParams() {
        mParam.put("sender_email", preferences.getString("sender_email", ""));
        mParam.put("sender_password", preferences.getString("sender_password", ""));
        mParam.put("receiver_email", preferences.getString("receiver_email", ""));
    }
}
