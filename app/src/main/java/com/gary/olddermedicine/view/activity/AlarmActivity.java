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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.pojo.MedicineProcess;
import com.gary.olddermedicine.view.pojo.Record;
import com.gary.olddermedicine.view.receiver.RepeatingAlarm;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.Date;
import java.util.List;
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
        builder.setPositiveButton("稍等恰", new DialogInterface.OnClickListener() {

          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            alarm();
            saveProcess(new Date(), false);
            finish();
          }
        });

        builder.setNegativeButton("已恰", new DialogInterface.OnClickListener() {

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
        showCurrentData();
        if (!isFinish) {
            isTellEmergencyPhone();
        }
    }

    private void isTellEmergencyPhone() {
        List<MedicineProcess> list= DataSupport.where("isFinish=?", String.valueOf(0)).find(MedicineProcess.class);
        if (list.size() != 0 && list.size() % 5 == 0) {
            acquire();
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