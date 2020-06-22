package com.gary.olddermedicine.view.fragment;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.activity.EditRecord;
import com.gary.olddermedicine.view.adapter.RecordAdapter;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.gary.olddermedicine.view.pojo.OneRecord;
import com.gary.olddermedicine.view.pojo.Record;
import com.gary.olddermedicine.view.receiver.RepeatingAlarm;
import com.gary.olddermedicine.view.util.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.androidman.SuperButton;

import static android.app.Activity.RESULT_OK;


public class FragmentMain extends Fragment {
    SuperButton button;
    TextView time;
    ListView listView;
    RecordAdapter recordAdapter;
    List<OneRecord> recordList=new ArrayList<>();
    private Handler handler;
    static final int BASE_NUM_FOR_ALARM = 100;

    public static FragmentMain newInstance(String text){
        FragmentMain fragmentCommon=new FragmentMain();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
    @SuppressLint("HandlerLeak")
    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_main,container,false);
        button = view.findViewById(R.id.textView);
        time = view.findViewById(R.id.time);
        button.setText("点击添加");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(inflater.getContext(), EditRecord.class);

                int position = recordList.size();
                Calendar c=Calendar.getInstance();
                String currentDate=c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
                String currentTime=c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);

                it.putExtra("num",position);
                it.putExtra("tag",0);
                it.putExtra("textDate",currentDate);
                it.putExtra("textTime",currentTime);
                it.putExtra("alarm","");
                it.putExtra("mainText","");
                it.putExtra("medicineInfo","");

                startActivityForResult(it,position);
            }
        });
        loadHistoryData();
        recordAdapter = new RecordAdapter(inflater.getContext(), R.layout.record_list, recordList);
        listView = view.findViewById(R.id.list);
        listView.setAdapter(recordAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int n=recordList.size();

                if(recordList.get(position).isAlarm()) {
                    cancelAlarm(position);
                }
                recordList.remove(position);
                recordAdapter.notifyDataSetChanged();

                String whereArgs = String.valueOf(position);
                DataSupport.deleteAll(Record.class, "num = ?", whereArgs);
                deleteByNum(position);

                for(int i=position+1; i<n; i++) {
                    ContentValues temp = new ContentValues();
                    temp.put("num", i-1);
                    String where = String.valueOf(i);
                    DataSupport.updateAll(Record.class, temp, "num = ?", where);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it=new Intent(inflater.getContext(), EditRecord.class);

                Record record=getRecordWithNum(position);

                it.putExtra("num",record.getNum());
                it.putExtra("tag",record.getTag());
                it.putExtra("textDate",record.getTextDate());
                it.putExtra("textTime",record.getTextTime());
                it.putExtra("alarm",record.getAlarm());
                it.putExtra("mainText",record.getMainText());
                it.putExtra("medicineInfo",record.getMedicineInfo());

                startActivityForResult(it,position);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (ResultCode.SUCCESS.code() == msg.arg1) {
                    recordAdapter.notifyDataSetChanged();
                }
            }
        };
        return view;
    }

    private void deleteByNum(final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/record/deleteByNum");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("num", num + "");
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

    public void onActivityResult(int requestCode, int resultCode, Intent it) {
        if(resultCode==RESULT_OK) {
            updateLitePalAndList(requestCode, it);
        }
    }

    private void updateLitePalAndList(int requestCode, Intent it) {
        String alarm=it.getStringExtra("alarm");
        String mainText=it.getStringExtra("mainText");
        String medicineInfo=it.getStringExtra("medicineInfo");
        int tag=it.getIntExtra("tag",0);
        List<Integer> rates = it.getIntegerArrayListExtra("rate");
        Calendar c = Calendar.getInstance();
        String currentDate=c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
        String currentTime=c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE);
        boolean gotAlarm = alarm.length() > 1;
        int num=requestCode;
        OneRecord oneRecord = new OneRecord(currentDate, currentTime, gotAlarm, mainText, tag, rates, medicineInfo);

        if((requestCode+1)>recordList.size()) {
            addRecordToLitePal(num, tag, currentDate, currentTime, alarm, mainText, rates, medicineInfo);

            recordList.add(oneRecord);
        }
        else {
            if(recordList.get(num).isAlarm()) {
                cancelAlarm(num);
            }

            ContentValues temp = new ContentValues();
            temp.put("tag", tag);
            temp.put("textDate", currentDate);
            temp.put("textTime", currentTime);
            temp.put("alarm", alarm);
            temp.put("mainText", mainText);
            StringBuilder sb = new StringBuilder();
            for (Integer i : rates) {
                sb.append(i);
                sb.append("|");
            }
            temp.put("rate", sb.toString());
            temp.put("medicineInfo", medicineInfo);
            String where = String.valueOf(num);
            DataSupport.updateAll(Record.class, temp, "num = ?", where);
            updateRecordByNum(tag, currentDate, currentTime, alarm, mainText, sb.toString(), medicineInfo, num);

            recordList.set(num, oneRecord);
        }
        if(gotAlarm) {
            loadAlarm(alarm, requestCode, rates);
        }
        recordAdapter.notifyDataSetChanged();
        showCurrentData();
    }

    private void updateRecordByNum(int tag, String currentDate, String currentTime, String alarm, String mainText, String rate, String medicineInfo, int num) {
        final Record record = new Record(num, tag, currentDate, currentTime, alarm, mainText, rate, medicineInfo);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/record/updateByNum");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("num", record.getNum() + "");
                    map.put("tag", record.getTag() + "");
                    map.put("textTime", record.getTextTime());
                    map.put("textDate", record.getTextDate());
                    map.put("alarm", record.getAlarm());
                    map.put("mainText", record.getMainText());
                    map.put("medicineInfo", record.getMedicineInfo());
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

    private void cancelAlarm(int num) {
        Record record = getRecordWithNum(num);
        int requestCode;
        if (record != null || record.getId() >= 0) {
            requestCode = record.getId() + BASE_NUM_FOR_ALARM;
        } else {
            return;
        }
        AlarmManager am = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));
        Intent intent = new Intent(getActivity(), RepeatingAlarm.class);
        intent.setAction("com.gary.alarm");
        PendingIntent op = PendingIntent.getBroadcast(getActivity(), requestCode, intent, 0);
        am.cancel(op);
    }

    private void loadAlarm(String alarm, int num, List<Integer> rate) {
        int i=0, k=0;
        while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
        int alarm_year=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
        int alarm_month=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
        int alarm_day=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
        int alarm_hour=Integer.parseInt(alarm.substring(k,i));
        k=i+1;i++;
        int alarm_minute=Integer.parseInt(alarm.substring(k));

        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.set(alarm_year,alarm_month-1,alarm_day,alarm_hour,alarm_minute);

        Record record = getRecordWithNum(num);
        int requestCode;
        if (record != null || record.getId() >= 0) {
            requestCode = record.getId() + BASE_NUM_FOR_ALARM;
        } else {
            return;
        }

        AlarmManager am = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));
        long triggerTime = alarmCalendar.getTimeInMillis();
        long currentTime = System.currentTimeMillis();

        System.out.println("current " + currentTime + " "+ new Date(currentTime));
        System.out.println("alarm " + triggerTime + " " + new Date(triggerTime));

        Intent intent = new Intent(getActivity(), RepeatingAlarm.class);
        intent.setAction("com.gary.alarm");
        intent.putExtra("mainText", record.getMainText());
        intent.putExtra("medicineInfo", record.getMedicineInfo());
        PendingIntent op = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (rate.contains(0)) {
            am.set(AlarmManager.RTC_WAKEUP, triggerTime, op);
        } else {
            for (Integer n : rate) {
                if (n.equals(0)) {
                    continue;
                } else {
                    am.setRepeating(AlarmManager.RTC_WAKEUP,triggerTime,n * AlarmManager.INTERVAL_DAY,op);
                }
            }
        }

    }

    private void addRecordToLitePal(int num, int tag, String textDate, String textTime, String alarm, String mainText, List<Integer> rate, String medicineInfo) {
        final Record record=new Record();
        record.setNum(num);
        record.setTag(tag);
        record.setTextDate(textDate);
        record.setTextTime(textTime);
        record.setAlarm(alarm);
        record.setMainText(mainText);
        StringBuilder sb = new StringBuilder();
        for (Integer i : rate) {
            sb.append(i);
            sb.append("|");
        }
        record.setRate(sb.toString());
        record.setMedicineInfo(medicineInfo);
        record.save();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/record/add");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("medicineInfo", record.getMedicineInfo());
                    map.put("rate", record.getRate());
                    map.put("mainText", record.getMainText());
                    map.put("alarm", record.getAlarm());
                    map.put("textDate", record.getTextDate());
                    map.put("textTime", record.getTextTime());
                    map.put("tag", record.getTag() + "");
                    map.put("num", record.getNum() + "");
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

    private Record getRecordWithNum(int num) {
        String whereArgs = String.valueOf(num);
        return DataSupport.where("num = ?", whereArgs).findFirst(Record.class);
    }

    private void showCurrentData() {
        List<Record> records= DataSupport.findAll(Record.class);
        for(Record record : records) {
            Log.d("CurrentData", "current num: " + record.getNum());
            Log.d("CurrentData", "id: " + record.getId());
            Log.d("CurrentData", "getAlarm: " + record.getAlarm());
            Log.d("CurrentData", "rate: " + record.getRate());
            Log.d("CurrentData", "medicineInfo: " + record.getMedicineInfo());
            Log.d("CurrentData", "mainText: " + record.getMainText());
        }
    }

    private void loadHistoryData() {
        DataSupport.deleteAll(Record.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/record/list");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    int resultCode = httpURLConnection.getResponseCode();
                    if(HttpURLConnection.HTTP_OK == resultCode){
                        InputStream inputStream = httpURLConnection.getInputStream();
//                        StringBuffer stringBuffer = new StringBuffer();
//                        byte [] buff = new byte[1024];
//                        int len;
//                        while((len = inputStream.read(buff))!=-1){
//                            stringBuffer.append(new String(buff,0,len,"utf-8"));
//                        }
                        String response = HttpUtil.is2String(inputStream);
                        Gson gson = new Gson();
                        Result result = gson.fromJson(response, Result.class);
                        List<Record> data = gson.fromJson(result.getData().toString(), new TypeToken<List<Record>>(){}.getType());
                        if (ResultCode.SUCCESS.code() == result.getCode()) {
                            System.out.println("获取" + result.toString());
                            System.out.println("data" + data.toString());
                            for(Record record : data) {
                                Log.d("MainActivity", "current num: " + record.getNum());
                                Log.d("MainActivity", "id: " + record.getId());
                                Log.d("MainActivity", "getAlarm: " + record.getAlarm());
                                Log.d("MainActivity", "rate: " + record.getRate());
                                Log.d("MainActivity", "medicineInfo: " + record.getMedicineInfo());
                                int tag = record.getTag();
                                String textDate = record.getTextDate();
                                String textTime = record.getTextTime();
                                boolean alarm = record.getAlarm().length() > 1;
                                String mainText = record.getMainText();
                                String rate = record.getRate();
                                String medicineInfo = record.getMedicineInfo();
                                List<Integer> integers = new ArrayList<>();
                                if (rate != null && rate.length() > 2) {
                                    String[] split = rate.substring(1, rate.length() - 1).split(",");
                                    for (String s : split) {
                                        integers.add(Integer.valueOf(s.trim()));
                                    }
                                }
                                OneRecord temp = new OneRecord(textDate, textTime, alarm, mainText, tag, integers, medicineInfo);
                                System.out.println("saveIsSuccessful:" + record.save());
                                recordList.add(temp);
                            }
                            Message msg = new Message();
                            msg.arg1 = result.getCode();
                            handler.sendMessage(msg);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
//        List<Record> records= DataSupport.findAll(Record.class);
//        if(records.size()==0) {
//            initializeLitePal();
//            records = DataSupport.findAll(Record.class);
//        }
//
//        for(Record record : records) {
//            Log.d("MainActivity", "current num: " + record.getNum());
//            Log.d("MainActivity", "id: " + record.getId());
//            Log.d("MainActivity", "getAlarm: " + record.getAlarm());
//            Log.d("MainActivity", "rate: " + record.getRate());
//            Log.d("MainActivity", "medicineInfo: " + record.getMedicineInfo());
//            int tag = record.getTag();
//            String textDate = record.getTextDate();
//            String textTime = record.getTextTime();
//            boolean alarm = record.getAlarm().length() > 1;
//            String mainText = record.getMainText();
//            String rate = record.getRate();
//            String medicineInfo = record.getMedicineInfo();
//            List<Integer> integers = new ArrayList<>();
//            if (rate != null && rate.length() > 2) {
//                String[] split = rate.substring(1, rate.length() - 1).split(",");
//                for (String s : split) {
//                    integers.add(Integer.valueOf(s.trim()));
//                }
//            }
//            OneRecord temp = new OneRecord(textDate, textTime, alarm, mainText, tag, integers, medicineInfo);
//            recordList.add(temp);
//        }
    }
    private void initializeLitePal() {
        Calendar c=Calendar.getInstance();
        String currentDate=c.get(Calendar.YEAR)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.DAY_OF_MONTH);
        String currentTime=c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE);

        addRecordToLitePal(0,0,currentDate,currentTime,"","点击编辑", new ArrayList<Integer>(), "");
        addRecordToLitePal(1,1,currentDate,currentTime,"","长按删除", new ArrayList<Integer>(), "");
    }
}