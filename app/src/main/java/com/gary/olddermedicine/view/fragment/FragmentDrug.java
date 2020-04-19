package com.gary.olddermedicine.view.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
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
import com.gary.olddermedicine.view.pojo.Medicine;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import top.androidman.SuperButton;

import static android.app.Activity.RESULT_OK;

public class FragmentDrug extends Fragment {
    SuperButton button;
    ListView listView;
    MedicineRecordAdapter medicineRecordAdapter;
    List<Medicine> medicineList = new ArrayList<>();


    public static FragmentDrug newInstance(String text){
        FragmentDrug fragmentCommon=new FragmentDrug();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
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

                for(int i=position+1; i<n; i++) {
                    ContentValues temp = new ContentValues();
                    temp.put("num", i-1);
                    String where = String.valueOf(i);
                    DataSupport.updateAll(Medicine.class, temp, "num = ?", where);
                }
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(inflater.getContext(), EditMedicine.class);

                Medicine medicine = getMedicineWithNum(position);

                it.putExtra("num",medicine.getNum());
                it.putExtra("tag",medicine.getTag());
                it.putExtra("name",medicine.getName());
                it.putExtra("dosage",medicine.getDosage());
                it.putExtra("remain",medicine.getRemain());

                startActivityForResult(it,position);
            }
        });
        return view;
    }

    private void loadHistoryData() {
        List<Medicine> medicines= DataSupport.findAll(Medicine.class);

        if(medicines.size()==0) {
            initializeLitePal();
            medicines = DataSupport.findAll(Medicine.class);
        }

        for(Medicine medicine : medicines) {
            Log.d("MainActivity", "id: " + medicine.getId());
            Log.d("MainActivity", "name: " + medicine.getName());
            Log.d("MainActivity", "num: " + medicine.getNum());
            Log.d("MainActivity", "dosage: " + medicine.getDosage());
            Log.d("MainActivity", "remain: " + medicine.getRemain());

            Medicine temp = new Medicine(medicine.getId(), medicine.getName(), medicine.getNum(), medicine.getDosage(), medicine.getRemain());
            medicineList.add(temp);
        }
    }

    private void initializeLitePal() {
        addMedicineToLitePal("硫糖铝片", 97, "成人：口服，一次1g，一日4次，饭前1小时及睡前空腹嚼碎服用。小儿遵医嘱。", 0, 0);
        addMedicineToLitePal("葡萄糖钙片", 98, "成人每日0.5-2g，餐后服。", 1, 0);
    }

    private void addMedicineToLitePal(String name, int remain, String dosage, int num, int tag) {
        new Medicine(name, remain, dosage, num, tag).save();
    }

    private Medicine getMedicineWithNum(int num) {
        String whereArgs = String.valueOf(num);
        Medicine medicine= DataSupport.where("num = ?", whereArgs).findFirst(Medicine.class);
        return medicine;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent it) {
        if(resultCode==RESULT_OK) {
            updateLitePalAndList(requestCode, it);
        }
    }

    private void updateLitePalAndList(int requestCode, Intent it) {
        String name=it.getStringExtra("name");
        String dosage=it.getStringExtra("dosage");
        String remain=it.getStringExtra("remain");
        int re = 0;
        if (remain != null) {
            re = Integer.valueOf(remain);
        }
        int tag=it.getIntExtra("tag",0);
        int num = requestCode;
        Medicine medicine = new Medicine(name, re, dosage, num, tag);

        if((requestCode+1)>medicineList.size()) {
            addMedicineToLitePal(name, re, dosage, num, tag);

            medicineList.add(medicine);
        }
        else {

            ContentValues temp = new ContentValues();
            temp.put("tag", tag);
            temp.put("name", name);
            temp.put("dosage", dosage);
            temp.put("remain", remain);
            String where = String.valueOf(num);
            DataSupport.updateAll(Medicine.class, temp, "num = ?", where);

            medicineList.set(num, medicine);
        }
        medicineRecordAdapter.notifyDataSetChanged();
        showCurrentData();
    }

    private void showCurrentData() {
        List<Medicine> Medicines= DataSupport.findAll(Medicine.class);
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