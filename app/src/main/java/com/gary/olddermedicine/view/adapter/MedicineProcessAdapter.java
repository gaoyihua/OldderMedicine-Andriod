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
import com.gary.olddermedicine.view.pojo.MedicineProcess;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicineProcessAdapter extends ArrayAdapter<MedicineProcess> {
    private final int resourceId;

    public MedicineProcessAdapter(Context context, int resource, List<MedicineProcess> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MedicineProcess process = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);//实例化一个对象
        TextView isFinishView = view.findViewById(R.id.process_isFinish);
        TextView timeView = view.findViewById(R.id.process_time);
        TextView infoView = view.findViewById(R.id.process_medicine_info);
        ImageView tag= view.findViewById(R.id.tag);
        isFinishView.setText(process.isFinish() == true ? "已服药" : "未服药");
        long time = process.getTime();
        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        timeView.setText(simpleDateFormat.format(date));
        infoView.setText(process.getMedicineInfo());
        tag.setBackgroundColor(Color.parseColor("#F5EFA0"));
        return view;
    }
}
