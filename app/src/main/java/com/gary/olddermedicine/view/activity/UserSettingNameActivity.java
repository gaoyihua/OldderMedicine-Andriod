package com.gary.olddermedicine.view.activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gary.olddermedicine.R;


public class UserSettingNameActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_name);
        final SQLiteDatabase db = SQLiteDatabase
                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);

        final SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt("id", -1);

        final TextView editName =findViewById(R.id.name);
        final View ret = findViewById(R.id.ret);

        TextView saveName = findViewById(R.id.save_name);

        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                if (name != null && !name.equals("")) {
                    db.execSQL("update users set name=? where id=" + id, new Object[]{name});
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putString("name", name);
                    edit.apply();
                    finish();
                }
            }
        });

        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
