package com.gary.olddermedicine.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.adapter.MedicineProcessAdapter;
import com.gary.olddermedicine.view.adapter.MedicineRecordAdapter;
import com.gary.olddermedicine.view.pojo.Medicine;
import com.gary.olddermedicine.view.pojo.MedicineProcess;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentNews extends Fragment {
    private ListView listView;
    private MedicineProcessAdapter medicineProcessAdapter;
    private List<MedicineProcess> medicineProcessesList = new ArrayList<>();

    public static FragmentNews newInstance(String text){
        FragmentNews fragmentCommon=new FragmentNews();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate( R.layout.fragment_news,container,false);
        listView = view.findViewById(R.id.list_medicine_process);
        loadHistoryData();
        medicineProcessAdapter = new MedicineProcessAdapter(inflater.getContext(), R.layout.medicine_process_list, medicineProcessesList);
        listView.setAdapter(medicineProcessAdapter);
        return view;
    }

    private void loadHistoryData() {
        List<MedicineProcess> medicineProcess= DataSupport.findAll(MedicineProcess.class);

        if(medicineProcess.size()==0) {
            initializeLitePal();
            medicineProcess = DataSupport.findAll(MedicineProcess.class);
        }

        for(MedicineProcess process : medicineProcess) {
            Log.d("MainActivity", "id: " + process.getId());
            Log.d("MainActivity", "name: " + process.getTime());
            Log.d("MainActivity", "num: " + process.getMedicineInfo());
            Log.d("MainActivity", "dosage: " + process.isFinish());
            medicineProcessesList.add(process);
        }
    }

    private void initializeLitePal() {
        addMedicineProcessToLitePal(new Date().getTime(), "咳咳片-一天三次, 护肝胶囊1-一次四粒", false, "18792958337");
        addMedicineProcessToLitePal(new Date().getTime(), "咳咳片-一天三次, 护肝胶囊1-一次四粒", false, "18792958337");
    }

    private void addMedicineProcessToLitePal(long time, String medicineInfo, boolean isFinish, String phone) {
        new MedicineProcess(time, medicineInfo, isFinish, phone).save();
    }
}