package com.gary.olddermedicine.view.fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
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

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.activity.EditMedicine;
import com.gary.olddermedicine.view.adapter.MedicineRecordAdapter;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.gary.olddermedicine.view.pojo.Medicine;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.androidman.SuperButton;

import static android.app.Activity.RESULT_OK;

public class FragmentDrug extends Fragment {
    SuperButton button;
    ListView listView;
    MedicineRecordAdapter medicineRecordAdapter;
    List<Medicine> medicineList = new ArrayList<>();
    private Handler handler;

    public static FragmentDrug newInstance(String text){
        FragmentDrug fragmentCommon=new FragmentDrug();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
    @SuppressLint("HandlerLeak")
    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_drug,container,false);
        button = view.findViewById(R.id.btn_medicine);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it=new Intent(inflater.getContext(), EditMedicine.class);

                int position = medicineList.size();

                it.putExtra("num",position);
                it.putExtra("tag",0);
                it.putExtra("name","");
                it.putExtra("dosage","");
                it.putExtra("remain",0);
                it.putExtra("category","");

                startActivityForResult(it,position);
            }
        });
        listView = view.findViewById(R.id.list_medicine);
        loadHistoryData();
        medicineRecordAdapter = new MedicineRecordAdapter(inflater.getContext(), R.layout.medicine_list, medicineList);
        listView.setAdapter(medicineRecordAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int n=medicineList.size();

                medicineList.remove(position);
                medicineRecordAdapter.notifyDataSetChanged();

                String whereArgs = String.valueOf(position);
                DataSupport.deleteAll(Medicine.class, "num = ?", whereArgs);
                deleteByNum(position);

                for(int i=position+1; i<n; i++) {
                    ContentValues temp = new ContentValues();
                    temp.put("num", i-1);
                    String where = String.valueOf(i);
                    DataSupport.updateAll(Medicine.class, temp, "num = ?", where);
                    updateNumByNum(i);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(inflater.getContext(), EditMedicine.class);
                System.out.println("position:" + position);
                Medicine medicine = getMedicineWithNum(position);

                it.putExtra("num",medicine.getNum());
                it.putExtra("tag",medicine.getTag());
                it.putExtra("name",medicine.getName());
                it.putExtra("dosage",medicine.getDosage());
                it.putExtra("remain",medicine.getRemain());
                it.putExtra("category",medicine.getCategory());

                startActivityForResult(it,position);
            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (ResultCode.SUCCESS.code() == msg.arg1) {
                    medicineRecordAdapter.notifyDataSetChanged();
                    showCurrentData();
                }
            }
        };
        return view;
    }

    private void updateNumByNum(final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/updateNumByNum");
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

    private void deleteByNum(final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/deleteByNum");
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

    private void loadHistoryData() {
        //List<Medicine> medicines= DataSupport.findAll(Medicine.class);
        DataSupport.deleteAll(Medicine.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/list");
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
                        List<Medicine> data = gson.fromJson(result.getData().toString(), new TypeToken<List<Medicine>>(){}.getType());
                        if (ResultCode.SUCCESS.code() == result.getCode()) {
                            System.out.println("获取" + result.toString());
                            System.out.println("data" + data.toString());
                            for(Medicine medicine : data) {
                                Log.d("MainActivity", "id: " + medicine.getId());
                                Log.d("MainActivity", "name: " + medicine.getName());
                                Log.d("MainActivity", "num: " + medicine.getNum());
                                Log.d("MainActivity", "dosage: " + medicine.getDosage());
                                Log.d("MainActivity", "remain: " + medicine.getRemain());
                                Log.d("MainActivity", "category: " + medicine.getCategory());

                                Medicine temp = new Medicine(medicine.getId(), medicine.getName(), medicine.getNum(), medicine.getDosage(), medicine.getRemain(), medicine.getTag(), medicine.getCategory());

                                System.out.println("saveIsSuccessful:" + medicine.save());
                                medicineList.add(temp);
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
//        if(medicines.size()==0) {
//            initializeLitePal();
//            medicines = DataSupport.findAll(Medicine.class);
//        }
//
//        for(Medicine medicine : medicines) {
//            Log.d("MainActivity", "id: " + medicine.getId());
//            Log.d("MainActivity", "name: " + medicine.getName());
//            Log.d("MainActivity", "num: " + medicine.getNum());
//            Log.d("MainActivity", "dosage: " + medicine.getDosage());
//            Log.d("MainActivity", "remain: " + medicine.getRemain());
//
//            Medicine temp = new Medicine(medicine.getId(), medicine.getName(), medicine.getNum(), medicine.getDosage(), medicine.getRemain());
//            medicineList.add(temp);
//        }
    }

//    private void initializeLitePal() {
//        addMedicineToLitePal("硫糖铝片", 97, "成人：口服，一次1g，一日4次，饭前1小时及睡前空腹嚼碎服用。小儿遵医嘱。", 0, 0);
//        addMedicineToLitePal("葡萄糖钙片", 98, "成人每日0.5-2g，餐后服。", 1, 0);
//    }

    private void addMedicineToLitePal(final String name, final int remain, final String dosage, final int num, final int tag, final String category) {
        new Medicine(name, remain, dosage, num, tag, category).save();
        final Medicine medicine = new Medicine(name, remain, dosage, num, tag, category);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/add");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("name", medicine.getName());
                    map.put("remain", medicine.getRemain() + "");
                    map.put("num", medicine.getNum() + "");
                    map.put("tag", medicine.getTag() + "");
                    map.put("dosage", medicine.getDosage());
                    map.put("category", medicine.getCategory());
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

    private Medicine getMedicineWithNum(int num) {
        String whereArgs = String.valueOf(num);
        return DataSupport.where("num = ?", whereArgs).findFirst(Medicine.class);
    }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    URL url = new URL("http://10.0.2.2:8080/medicine/findByNum");
//                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
//                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
//                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
//                    httpURLConnection.setUseCaches(false);
//                    httpURLConnection.setDoOutput(true);
//                    httpURLConnection.setDoInput(true);
//                    httpURLConnection.connect();
//
//                    Map<String, String> map = new HashMap<>();
//                    map.put("num", num + "");
//                    JSONObject jsonObject = new JSONObject(map);
//
//                    DataOutputStream dos=new DataOutputStream(httpURLConnection.getOutputStream());
//                    dos.write(jsonObject.toString().getBytes());
//                    dos.flush();
//                    dos.close();
//
//                    int resultCode = httpURLConnection.getResponseCode();
//                    if(HttpURLConnection.HTTP_OK==resultCode){
//                        InputStream inputStream = httpURLConnection.getInputStream();
//                        StringBuffer stringBuffer = new StringBuffer();
//                        byte [] buff = new byte[1024];
//                        int len;
//                        while((len = inputStream.read(buff))!=-1){
//                            stringBuffer.append(new String(buff,0,len,"utf-8"));
//                        }
//                        Gson gson = new Gson();
//                        Result result = gson.fromJson(stringBuffer.toString(), Result.class);
//                        medicine = gson.fromJson(result.getData().toString(), Medicine.class);
//
//                        if (ResultCode.SUCCESS.code() == result.getCode()) {
//                            System.out.println("获取" + result.toString());
//                            Message msg = new Message();
//                            msg.arg1 = result.getCode();
//                            handler.sendMessage(msg);
//                        }
//                    }
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }).start();

    public void onActivityResult(int requestCode, int resultCode, Intent it) {
        if(resultCode==RESULT_OK) {
            updateLitePalAndList(requestCode, it);
        }
    }

    private void updateLitePalAndList(int requestCode, Intent it) {
        String name=it.getStringExtra("name");
        String dosage=it.getStringExtra("dosage");
        String remain=it.getStringExtra("remain");
        String category=it.getStringExtra("category");
        int re = 0;
        if (remain != null) {
            re = Integer.valueOf(remain);
        }
        int tag=it.getIntExtra("tag",0);
        int num = requestCode;
        Medicine medicine = new Medicine(name, re, dosage, num, tag, category);

        if((requestCode+1)>medicineList.size()) {
            addMedicineToLitePal(name, re, dosage, num, tag, category);

            medicineList.add(medicine);
        }
        else {

            ContentValues temp = new ContentValues();
            temp.put("tag", tag);
            temp.put("name", name);
            temp.put("dosage", dosage);
            temp.put("remain", remain);
            temp.put("category", category);
            String where = String.valueOf(num);
            DataSupport.updateAll(Medicine.class, temp, "num = ?", where);
            updateMedicineByNum(tag, name, dosage, re, num, category);

            medicineList.set(num, medicine);
        }
        medicineRecordAdapter.notifyDataSetChanged();
        showCurrentData();
    }

    private void updateMedicineByNum(int tag, String name, String dosage, int remain, int num, String category) {
        final Medicine medicine = new Medicine(name, remain, dosage, num, tag, category);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/updateByNum");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("name", medicine.getName());
                    map.put("remain", medicine.getRemain() + "");
                    map.put("num", medicine.getNum() + "");
                    map.put("tag", medicine.getTag() + "");
                    map.put("dosage", medicine.getDosage());
                    map.put("category", medicine.getCategory());
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

    private void showCurrentData() {
        List<Medicine> Medicines= DataSupport.findAll(Medicine.class);
        System.out.println("local size:" + Medicines.size());
        for(Medicine medicine : Medicines) {
            Log.d("CurrentData", "id: " + medicine.getId());
            Log.d("CurrentData", "current num: " + medicine.getNum());
            Log.d("CurrentData", "remain: " + medicine.getRemain());
            Log.d("CurrentData", "dosage: " + medicine.getDosage());
            Log.d("CurrentData", "name: " + medicine.getName());
            Log.d("CurrentData", "tag: " + medicine.getTag());
        }
    }
}