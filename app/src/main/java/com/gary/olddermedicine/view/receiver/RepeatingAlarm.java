package com.gary.olddermedicine.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.gary.olddermedicine.view.activity.AlarmActivity;

public class RepeatingAlarm extends BroadcastReceiver{
  String mainText;
  String medicineInfo;

  @Override
  public void onReceive(Context context, Intent intent) {
    mainText=intent.getStringExtra("mainText");
    medicineInfo=intent.getStringExtra("medicineInfo");
    if (intent.getAction()!=null&&intent.getAction().equals("com.gary.alarm")) {//自定义的action
      intent = new Intent(context, AlarmActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("mainText", mainText);
      intent.putExtra("medicineInfo", medicineInfo);
      context.startActivity(intent);
    }
  }
}