package com.gary.olddermedicine.view.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class UserSettingSexActivity extends AppCompatActivity {
    private int id;
    private Handler handler;
    private int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_sex);
        
        dealAction();
    }

    @SuppressLint("HandlerLeak")
    private void dealAction() {
        final SQLiteDatabase db = SQLiteDatabase
                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);
        final SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt("id", -1);

        final Spinner spinner = findViewById(R.id.spinner);
        TextView saveSex = findViewById(R.id.save_sex);
        View ret = findViewById(R.id.ret);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sexText = spinner.getSelectedItem().toString();
                sex = sexText.equals("男") ? 0 : 1;
//                db.execSQL("update users set sex=? where id=" + id, new Object[]{sex});
//                SharedPreferences.Editor edit = sp.edit();
//                edit.putInt("sex", sex);
//                edit.apply();
//                finish();
                update(sex);
            }
        });

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (ResultCode.SUCCESS.code() == msg.arg1) {
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("sex", sex);
                    edit.apply();
                    finish();
                }
            }
        };
    }
    private void update(final int sex){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/om/user/update");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("sex", sex + "");
                    map.put("id", id + "");
                    JSONObject jsonObject = new JSONObject(map);

                    DataOutputStream dos=new DataOutputStream(httpURLConnection.getOutputStream());
                    dos.write(jsonObject.toString().getBytes());
                    dos.flush();
                    dos.close();

                    int resultCode = httpURLConnection.getResponseCode();
                    if(HttpURLConnection.HTTP_OK==resultCode){
                        InputStream inputStream = httpURLConnection.getInputStream();
                        StringBuffer stringBuffer = new StringBuffer();
                        byte [] buff = new byte[1024];
                        int len;
                        while((len = inputStream.read(buff))!=-1){
                            stringBuffer.append(new String(buff,0,len,"utf-8"));
                        }
                        Gson gson = new Gson();
                        Result result = gson.fromJson(stringBuffer.toString(), Result.class);

                        if (ResultCode.SUCCESS.code() == result.getCode()) {
                            System.out.println("获取" + result.toString());
                            Message msg = new Message();
                            msg.arg1 = result.getCode();
                            handler.sendMessage(msg);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
}
