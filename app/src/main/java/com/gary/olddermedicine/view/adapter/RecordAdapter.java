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
import com.gary.olddermedicine.view.pojo.OneRecord;

import java.util.List;

public class RecordAdapter extends ArrayAdapter<OneRecord> {
    private final int resourceId;
    int[] color={Color.parseColor("#F5EFA0"), Color.parseColor("#8296D5"),Color.parseColor("#95C77E"),Color.parseColor("#F49393"),Color.parseColor("#FFFFFF")};

    public RecordAdapter(Context context, int resource, List<OneRecord> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OneRecord oneRecord = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);//实例化一个对象
        ImageView tag= view.findViewById(R.id.tag);
        TextView textDate= view.findViewById(R.id.textDate);
        TextView textTime= view.findViewById(R.id.textTime);
        ImageView alarm= view.findViewById(R.id.alarm);
        TextView mainText= view.findViewById(R.id.mainText);
        TextView medicineInfo = view.findViewById(R.id.medicineInfo);
        textDate.setText(oneRecord.getTextDate());
        textTime.setText(oneRecord.getTextTime().replace("-", ":"));
        medicineInfo.setText(oneRecord.getMedicineInfo());
        if(oneRecord.getTag()<color.length)
            tag.setBackgroundColor(color[oneRecord.getTag()]);
        if(oneRecord.isAlarm()) {
            alarm.setVisibility(View.VISIBLE);
        }
        else {
            alarm.setVisibility(View.GONE);
        }
        mainText.setText(oneRecord.getMainText());
        return view;
    }
}
