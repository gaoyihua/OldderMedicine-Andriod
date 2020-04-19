package com.gary.olddermedicine.view.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.activity.LoginActivity;
import com.gary.olddermedicine.view.activity.MainActivity;
import com.gary.olddermedicine.view.activity.UserSettingActivity;
import com.gary.olddermedicine.view.pojo.MedicineProcess;

import org.litepal.crud.DataSupport;

import java.util.List;

import top.androidman.SuperButton;

import static android.content.Context.MODE_PRIVATE;

public class FragmentInfo extends Fragment {


    public static FragmentInfo newInstance(String text){
        FragmentInfo fragmentCommon=new FragmentInfo();
        Bundle bundle=new Bundle();
        bundle.putString("text",text);
        fragmentCommon.setArguments(bundle);
        return fragmentCommon;
    }
    @Nullable @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate( R.layout.fragment_info,container,false);
        View my = view.findViewById(R.id.activity_myself);
        View her = view.findViewById(R.id.activity_myself_exit);
        SharedPreferences sp = getActivity().getSharedPreferences("myShare", MODE_PRIVATE);
        String email = sp.getString("email", "");
        TextView emailText = view.findViewById(R.id.email);
        emailText.setText(email);
        if (email != null && !"".equals(email)) {
            my.setVisibility(View.VISIBLE);
            her.setVisibility(View.GONE);
        } else {
            my.setVisibility(View.GONE);
            her.setVisibility(View.VISIBLE);
        }
        Button exitUser = view.findViewById(R.id.exit_user);
        exitUser.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("myShare", MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.clear();
                edit.apply();
                View my = view.findViewById(R.id.activity_myself);
                View her = view.findViewById(R.id.activity_myself_exit);
                my.setVisibility(View.GONE);
                her.setVisibility(View.VISIBLE);
                MainActivity activity = (MainActivity) getActivity();
                activity.loadUser();
            }
        });

        TextView login = view.findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        Button exitSys = view.findViewById(R.id.exit_sys);
        exitSys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Button exitSysExit = view.findViewById(R.id.exit_sys_exit);
        exitSysExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Button editUser = view.findViewById(R.id.edit_user);
        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserSettingActivity.class);
                startActivity(intent);
            }
        });
        Button testButton = view.findViewById(R.id.test_button);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCurrentData();
                List<MedicineProcess> list= DataSupport.where("isFinish=?", String.valueOf(0)).find(MedicineProcess.class);
                System.out.println(list.size());
            }
        });
        return view;
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


}