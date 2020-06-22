package com.gary.olddermedicine.view.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.gary.olddermedicine.view.pojo.MedicineProcess;
import com.gary.olddermedicine.view.pojo.Record;
import com.gary.olddermedicine.view.receiver.RepeatingAlarm;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AlarmActivity extends Activity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;

    MediaPlayer mp;
    String mainText;
    String medicineInfo;
    int randomNum = new Random().nextInt(100);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_alarm);
        mainText=getIntent().getStringExtra("mainText");
        medicineInfo=getIntent().getStringExtra("medicineInfo");
        mp = new MediaPlayer();
        AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
        try {
            mp.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                file.getLength());
            mp.prepare();
            file.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
        mp.setVolume(0.5f, 0.5f);
        mp.setLooping(true);
        mp.start();
        alarmOialog();
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS} , 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mp != null) {
          if (mp.isPlaying()) {
            mp.stop();
          }
          mp.release();
        }
    }

    public void alarmOialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mainText + "\n" + medicineInfo);
        builder.setPositiveButton("稍等吃", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            alarm();
            saveProcess(new Date(), false);
            finish();
          }
        });

        builder.setNegativeButton("已吃", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            cancleAlarm();
            saveProcess(new Date(), true);
            finish();// 关闭窗口
          }
        });
        builder.show().setCanceledOnTouchOutside(false);
    }

    private void saveProcess(Date time, boolean isFinish) {
        SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        String phone = sp.getString("email", "XXX99999999");
        new MedicineProcess(time.getTime(), medicineInfo, isFinish, phone).save();
        saveMedicineProcess(new MedicineProcess(time.getTime(), medicineInfo, isFinish, phone));
        showCurrentData();
        if (!isFinish) {
            isTellEmergencyPhone();
        }
    }

    private void saveMedicineProcess(final MedicineProcess medicineProcess) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/process/add");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("medicineInfo", medicineProcess.getMedicineInfo());
                    map.put("phone", medicineProcess.getPhone());
                    map.put("time", medicineProcess.getTime() + "");
                    map.put("isFinish", medicineProcess.isFinish() + "");
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
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }

    private void isTellEmergencyPhone() {
        List<MedicineProcess> list= DataSupport.where("isFinish=?", String.valueOf(0)).find(MedicineProcess.class);
        SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        String phone = sp.getString("emergencyPhone", "XXX99999999");
        String name = sp.getString("name", "用户");
        if (list.size() != 0 && list.size() % 5 == 0) {
            sendSMS(phone, name + "已经" + list.size() + "次未按时服药");
            acquire();
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> divideMessage = smsManager.divideMessage(message);
        for (String text : divideMessage) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }

    public void acquire() {
        // 检查是否获得了权限（Android6.0运行时权限）
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CALL_PHONE)) {
                Toast.makeText(this, "请授权！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
            CallPhone();
        }else {
            CallPhone();
        }
    }
    private void CallPhone() {
        SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        String phone = sp.getString("emergencyPhone", "XXX99999999");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void showCurrentData() {
        List<MedicineProcess> records= DataSupport.findAll(MedicineProcess.class);
        for(MedicineProcess record : records) {
            Log.d("CurrentData", "current id: " + record.getId());
            Log.d("CurrentData", "time: " + record.getTime());
            Log.d("CurrentData", "medicineInfo: " + record.getMedicineInfo());
            Log.d("CurrentData", "isFinish: " + record.isFinish());
        }
    }

    /**
     * 取消闹钟
     */
    private void cancleAlarm() {
        // Create the same intent, and thus a matching IntentSender, for
        // the one that was scheduled.
        Intent intent = new Intent(AlarmActivity.this, RepeatingAlarm.class);
        intent.setAction("com.gary.alarm");
        intent.putExtra("mainText", mainText);
        PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this,
                randomNum, intent, 0);

        // And cancel the alarm.
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(sender);
    }

    private void alarm() {
        // 获取系统的闹钟服务 AlarmManager主要是用来在某个时刻运行你的代码的，即时你的APP在那个特定 时间并没有运行！还有
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 触发闹钟的时间（毫秒）
        long triggerTime = System.currentTimeMillis() + 10000;
        Intent intent = new Intent(this, RepeatingAlarm.class);
        intent.setAction("com.gary.alarm");
        intent.putExtra("mainText", mainText);
        intent.putExtra("medicineInfo", medicineInfo);
        PendingIntent op = PendingIntent.getBroadcast(this, randomNum, intent, 0);
        // 启动一次只会执行一次的闹钟
        am.set(AlarmManager.RTC, triggerTime, op);
    }
}