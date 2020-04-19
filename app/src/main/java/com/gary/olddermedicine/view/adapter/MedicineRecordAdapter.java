package com.gary.olddermedicine.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.pojo.Medicine;
import com.gary.olddermedicine.view.pojo.OneRecord;

import java.util.List;

public class MedicineRecordAdapter extends ArrayAdapter<Medicine> {
    private final int resourceId;
    int[] color={Color.parseColor("#F5EFA0"), Color.parseColor("#8296D5"),Color.parseColor("#95C77E"),Color.parseColor("#F49393"),Color.parseColor("#FFFFFF")};

    public MedicineRecordAdapter(Context context, int resource, List<Medicine> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Medicine medicine = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);//实例化一个对象
        TextView name = view.findViewById(R.id.name_medicine);
        TextView remain = view.findViewById(R.id.remain_medicine);
        TextView dosage = view.findViewById(R.id.dosage_medicine);
        ImageView tag= view.findViewById(R.id.tag);
        name.setText(medicine.getName());
        remain.setText(medicine.getRemain() + "");
        dosage.setText(medicine.getDosage());
        if(medicine.getTag()<color.length)
            tag.setBackgroundColor(color[medicine.getTag()]);
        return view;
    }
}
