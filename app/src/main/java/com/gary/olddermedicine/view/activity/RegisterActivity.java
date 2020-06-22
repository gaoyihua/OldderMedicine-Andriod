package com.gary.olddermedicine.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.gary.olddermedicine.view.util.Validator;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private Handler handler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final AutoCompleteTextView phoneText = findViewById(R.id.phone);
        final AutoCompleteTextView nameText = findViewById(R.id.name);
        final AutoCompleteTextView descriptionText = findViewById(R.id.description);
        final AutoCompleteTextView emergencyPeopleText = findViewById(R.id.emergency_people);
        final AutoCompleteTextView emergencyPhoneText = findViewById(R.id.emergency_phone);
        final RadioGroup radioGroup = findViewById(R.id.sex);

        final EditText passwordText = findViewById(R.id.password);

        final Button register = findViewById(R.id.register);
        final Button signIn = findViewById(R.id.signin);
        final View ret = findViewById(R.id.ret);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (ResultCode.SUCCESS.code() == msg.arg1) {
                    Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

//        final SQLiteDatabase db = SQLiteDatabase
////                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);
////
////        db.execSQL("create table IF NOT EXISTS users" +
////                "(id integer PRIMARY KEY AUTOINCREMENT, name varchar(50), sex integer, phone varchar(50), password varchar(50)" +
////                ", emergency_phone varchar(50), emergency_people varchar(50), description varchar(100))");

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String phone = phoneText.getText().toString();
//                String password = passwordText.getText().toString();
//                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
//                int sex = 0;
//                if (checkedRadioButtonId == R.id.male) {
//                    sex = 0;
//                } else {
//                    sex = 1;
//                }
//                String name = nameText.getText().toString();
//                String description = descriptionText.getText().toString();
//                String emergencyPeople = emergencyPeopleText.getText().toString();
//                String emergencyPhone = emergencyPhoneText.getText().toString();
//                db.execSQL("insert into users values(?, ?, ?, ?, ?, ?, ?, ?)",
//                        new Object[] {null, name, sex, phone, password, emergencyPhone, emergencyPeople, description});
//                Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                String phone = phoneText.getText().toString();
                if (!Validator.isMobile(phone)) {
                    Toast.makeText(RegisterActivity.this, "您的电话号码格式有误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String emergencyPhone = emergencyPhoneText.getText().toString();
                if (!Validator.isMobile(emergencyPhone)) {
                    Toast.makeText(RegisterActivity.this, "紧急联系人电话号码格式有误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://10.0.2.2:8080/om/user/register");

                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                            httpURLConnection.setRequestProperty("Content-Type", "application/json");
                            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                            httpURLConnection.setRequestProperty("Charset", "UTF-8");
                            httpURLConnection.setUseCaches(false);
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoInput(true);
                            httpURLConnection.connect();

                            Map<String, String> map = new HashMap();
                            String phone = phoneText.getText().toString();
                            String password = passwordText.getText().toString();
                            int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                            int sex;
                            if (checkedRadioButtonId == R.id.male) {
                                sex = 0;
                            } else {
                                sex = 1;
                            }
                            String name = nameText.getText().toString();
                            String description = descriptionText.getText().toString();
                            String emergencyPeople = emergencyPeopleText.getText().toString();
                            String emergencyPhone = emergencyPhoneText.getText().toString();
                            map.put("phone", phone);
                            map.put("password", password);
                            map.put("sex", sex + "");
                            map.put("name", name);
                            map.put("description", description);
                            map.put("emergencyPeople", emergencyPeople);
                            map.put("emergencyPhone", emergencyPhone);
                            JSONObject jsonObject = new JSONObject(map);

                            DataOutputStream dos=new DataOutputStream(httpURLConnection.getOutputStream());
                            //dos.writeBytes(jsonObject.toString());
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
        });
    }

}
