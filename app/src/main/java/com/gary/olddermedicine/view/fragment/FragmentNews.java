package com.gary.olddermedicine.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.adapter.MedicineProcessAdapter;
import com.gary.olddermedicine.view.adapter.MedicineRecordAdapter;
import com.gary.olddermedicine.view.entity.Result;
import com.gary.olddermedicine.view.entity.ResultCode;
import com.gary.olddermedicine.view.pojo.Medicine;
import com.gary.olddermedicine.view.pojo.MedicineProcess;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.androidman.SuperButton;

public class FragmentNews extends Fragment {
    private ListView listView;
    private MedicineProcessAdapter medicineProcessAdapter;
    private List<MedicineProcess> medicineProcessesList = new ArrayList<>();
    private Handler handler;
    private SuperButton query;
    private Spinner conditionIsFinish;

    public static FragmentNews newInstance(String text){
        FragmentNews fragmentCommon=new FragmentNews();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
    @SuppressLint("HandlerLeak")
    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_news,container,false);
        listView = view.findViewById(R.id.list_medicine_process);
        query = view.findViewById(R.id.query);
        conditionIsFinish = view.findViewById(R.id.condition_isFinish);
        //loadHistoryData();
        medicineProcessAdapter = new MedicineProcessAdapter(inflater.getContext(), R.layout.medicine_process_list, medicineProcessesList);
        listView.setAdapter(medicineProcessAdapter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (ResultCode.SUCCESS.code() == msg.arg1) {
                    medicineProcessAdapter.notifyDataSetChanged();
                }
            }
        };

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = conditionIsFinish.getSelectedItem().toString();
                if ("全部".equals(selectedItem)) {
                    loadHistoryData();
                    return;
                }
                final boolean isFinish = "已服药".equals(selectedItem) ;
                findDataByIsFinish(isFinish);
            }
        });
        return view;
    }

    private void findDataByIsFinish(final boolean isFinish) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/process/findByIsFinish");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();

                    Map<String, String> map = new HashMap<>();
                    map.put("isFinish", isFinish + "");
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
                            medicineProcessesList.clear();
                            DataSupport.deleteAll(MedicineProcess.class);
                            List<MedicineProcess> data = gson.fromJson(result.getData().toString(), new TypeToken<List<MedicineProcess>>(){}.getType());
                            System.out.println("获取" + result.toString());
                            System.out.println("data" + data.toString());
                            for(MedicineProcess medicine : data) {
                                MedicineProcess temp = new MedicineProcess(medicine.getId(), medicine.getTime(), medicine.getMedicineInfo(), medicine.isFinish(), medicine.getPhone());
                                temp.save();
                                medicineProcessesList.add(temp);
                            }
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

    public void loadHistoryData() {
//        List<MedicineProcess> medicineProcess= DataSupport.findAll(MedicineProcess.class);
        medicineProcessesList.clear();
        DataSupport.deleteAll(MedicineProcess.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://10.0.2.2:8080/medicine/process/list");
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
                        StringBuffer stringBuffer = new StringBuffer();
                        byte [] buff = new byte[1024];
                        int len;
                        while((len = inputStream.read(buff))!=-1){
                            stringBuffer.append(new String(buff,0,len,"utf-8"));
                        }
                        Gson gson = new Gson();
                        Result result = gson.fromJson(stringBuffer.toString(), Result.class);
                        List<MedicineProcess> data = gson.fromJson(result.getData().toString(), new TypeToken<List<MedicineProcess>>(){}.getType());
                        if (ResultCode.SUCCESS.code() == result.getCode()) {
                            System.out.println("获取" + result.toString());
                            System.out.println("data" + data.toString());
                            for(MedicineProcess medicine : data) {
                                Log.d("MainActivity", "getId: " + medicine.getId());
                                Log.d("MainActivity", "getMedicineInfo: " + medicine.getMedicineInfo());
                                Log.d("MainActivity", "getTime: " + medicine.getTime());
                                Log.d("MainActivity", "getPhone: " + medicine.getPhone());
                                Log.d("MainActivity", "isFinish: " + medicine.isFinish());

                                MedicineProcess temp = new MedicineProcess(medicine.getId(), medicine.getTime(), medicine.getMedicineInfo(), medicine.isFinish(), medicine.getPhone());
                                temp.save();
                                medicineProcessesList.add(temp);
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

//        if(medicineProcess.size()==0) {
//            initializeLitePal();
//            medicineProcess = DataSupport.findAll(MedicineProcess.class);
//        }
//
//        for(MedicineProcess process : medicineProcess) {
//            Log.d("MainActivity", "id: " + process.getId());
//            Log.d("MainActivity", "name: " + process.getTime());
//            Log.d("MainActivity", "num: " + process.getMedicineInfo());
//            Log.d("MainActivity", "dosage: " + process.isFinish());
//            medicineProcessesList.add(process);
//        }
    }

    private void initializeLitePal() {
        addMedicineProcessToLitePal(new Date().getTime(), "咳咳片-一天三次, 护肝胶囊1-一次四粒", false, "18792958337");
        addMedicineProcessToLitePal(new Date().getTime(), "咳咳片-一天三次, 护肝胶囊1-一次四粒", false, "18792958337");
    }

    private void addMedicineProcessToLitePal(long time, String medicineInfo, boolean isFinish, String phone) {
        new MedicineProcess(time, medicineInfo, isFinish, phone).save();
    }
}