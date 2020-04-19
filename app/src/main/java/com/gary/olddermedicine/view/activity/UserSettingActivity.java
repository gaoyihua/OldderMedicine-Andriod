package com.gary.olddermedicine.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gary.olddermedicine.R;

public class UserSettingActivity extends AppCompatActivity {
    private String phone = null;
    private String name = null;
    private int id;
    private String description = null;
    private String emergencyPeople = null;
    private String emergencyPhone = null;
    private int sex;
    private TextView phoneText;
    private TextView nameText;
    private TextView descText;
    private TextView sexText;
    private TextView emergencyPeopleText;
    private TextView emergencyPhoneText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting);
        View ret = findViewById(R.id.ret);
        View lineName = findViewById(R.id.line_name);
        View lineSex = findViewById(R.id.line_sex);
        View lineDesc = findViewById(R.id.line_description);
        View lineEmergencyPeople = findViewById(R.id.line_emergency_people);
        View lineEmergencyPhone = findViewById(R.id.line_emergency_phone);
        phoneText = findViewById(R.id.phone);
        nameText = findViewById(R.id.name);
        descText = findViewById(R.id.description);
        sexText = findViewById(R.id.sex);
        emergencyPeopleText = findViewById(R.id.emergency_people);
        emergencyPhoneText = findViewById(R.id.emergency_phone);

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        refreshData();

        lineSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "点击了性别！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingActivity.this, UserSettingSexActivity.class);
                startActivity(intent);
            }
        });

        lineDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "点击了描述！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingActivity.this, UserSettingDescActivity.class);
                startActivity(intent);
            }
        });

        lineName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "点击了用户名！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingActivity.this, UserSettingNameActivity.class);
                startActivity(intent);
            }
        });
        lineEmergencyPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "点击了紧急联系人！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingActivity.this, UserSettingEmergencyPeopleActivity.class);
                startActivity(intent);
            }
        });
        lineEmergencyPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserSettingActivity.this, "点击了紧急联系人电话！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UserSettingActivity.this, UserSettingEmergencyPhoneActivity.class);
                startActivity(intent);
            }
        });
    }

    private void refreshData() {
        SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt( "id", -1);
        phone = sp.getString("email", "xxx99999999");
        name = sp.getString("name", "XX");
        description = sp.getString("description", "暂无");
        emergencyPeople = sp.getString("emergencyPeople", "暂无");
        emergencyPhone = sp.getString("emergencyPhone", "暂无");
        sex = sp.getInt("sex", 0);

        phoneText.setText(phone);
        nameText.setText(name);
        descText.setText(description);
        sexText.setText(sex == 0 ? "男" : "女");
        emergencyPeopleText.setText(emergencyPeople);
        emergencyPhoneText.setText(emergencyPhone);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        refreshData();
    }
}
