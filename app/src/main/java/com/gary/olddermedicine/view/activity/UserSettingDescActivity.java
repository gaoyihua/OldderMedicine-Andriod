package com.gary.olddermedicine.view.activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gary.olddermedicine.R;


public class UserSettingDescActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_desc);

        dealAction();
    }

    private void dealAction() {
        final SQLiteDatabase db = SQLiteDatabase
                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);
        final SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt("id", -1);

        final EditText descText = findViewById(R.id.desc);
        TextView saveDesc = findViewById(R.id.save_desc);
        View ret = findViewById(R.id.ret);

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc = descText.getText().toString();
                db.execSQL("update users set description=? where id=" + id, new Object[]{desc});
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("description", desc);
                edit.apply();
                finish();
            }
        });
    }

}
