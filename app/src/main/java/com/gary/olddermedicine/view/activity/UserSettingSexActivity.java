package com.gary.olddermedicine.view.activity;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.gary.olddermedicine.R;


public class UserSettingSexActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_setting_sex);
        
        dealAction();
    }

    private void dealAction() {
        final SQLiteDatabase db = SQLiteDatabase
                .openOrCreateDatabase(this.getFilesDir().toString() + "/user.db3", null);
        final SharedPreferences sp = getSharedPreferences("myShare", MODE_PRIVATE);
        id = sp.getInt("id", -1);

        final Spinner spinner = findViewById(R.id.spinner);
        TextView saveSex = findViewById(R.id.save_sex);
        View ret = findViewById(R.id.ret);
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        saveSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sexText = spinner.getSelectedItem().toString();
                int sex = sexText.equals("男") ? 0 : 1;
                db.execSQL("update users set sex=? where id=" + id, new Object[]{sex});
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt("sex", sex);
                edit.apply();
                finish();
            }
        });
    }

}
