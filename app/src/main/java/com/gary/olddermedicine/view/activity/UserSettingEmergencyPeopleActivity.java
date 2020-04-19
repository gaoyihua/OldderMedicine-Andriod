package com.gary.olddermedicine.view.activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gary.olddermedicine.R;


public class UserSettingEmergencyPeopleActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_emergency_people);
        dealAction();
    }

    private void dealAction() {
        final SQLiteDatabase db = SQLiteDatabase
                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);
        final SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt("id", -1);

        final EditText emergencyText = findViewById(R.id.emergency_people);
        TextView saveEmergency = findViewById(R.id.save_emergency_people);
        View ret = findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveEmergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emergencyPeople = emergencyText.getText().toString();
                db.execSQL("update users set description=? where id=" + id, new Object[]{emergencyPeople});
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("emergencyPeople", emergencyPeople);
                edit.apply();
                finish();
            }
        });
    }

}
