package com.gary.olddermedicine.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gary.olddermedicine.R;

public class EditMedicine extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    LinearLayout myLayout;
    RadioGroup tagRadio;
    RadioButton rdButton;
    EditText nameView;
    EditText dosageView;
    EditText remainView;
    EditText categoryView;
    private int tag;
    private int num=0;
    private String name;
    private String dosage;
    private int remain;
    private String category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.medicine_edit);

        Intent it=getIntent();
        getInformationFromMain(it);

        myLayout =  findViewById(R.id.whole);
        myLayout.setBackgroundResource(R.drawable.edit_bg_yellow);

        tagRadio= findViewById(R.id.tagRadio);
        tagRadio.setOnCheckedChangeListener(this);
        setRadioButtonCheckedAccordingToTag(tag);
        rdButton.setChecked(true);

        nameView = findViewById(R.id.medicine_name);
        nameView.setText(name);
        dosageView = findViewById(R.id.medicine_dosage);
        dosageView.setText(dosage);
        remainView = findViewById(R.id.medicine_remain);
        remainView.setText(remain + "");
        categoryView = findViewById(R.id.medicine_category);
        categoryView.setText(category);
    }
    private void getInformationFromMain(Intent it) {
        num=it.getIntExtra("num",0);
        dosage = it.getStringExtra("dosage");
        name = it.getStringExtra("name");
        remain = it.getIntExtra("remain", 0);
        tag=it.getIntExtra("tag",0);
        category=it.getStringExtra("category");
    }

    private void setRadioButtonCheckedAccordingToTag(int tag) {
        switch (tag) {
            case 0:
                rdButton= findViewById(R.id.yellow);
                break;
            case 1:
                rdButton= findViewById(R.id.blue);
                break;
            case 2:
                rdButton= findViewById(R.id.green);
                break;
            case 3:
                rdButton= findViewById(R.id.red);
                break;
            case 4:
                rdButton= findViewById(R.id.white);
                break;
            default:
                break;
        }
    }

    public void onSave(View v) {
        returnResult();
        finish();
    }

    private void returnResult() {
        Intent it=new Intent();

        it.putExtra("tag",tag);
        it.putExtra("name",nameView.getText().toString());
        it.putExtra("dosage",dosageView.getText().toString());
        it.putExtra("remain",remainView.getText().toString());
        it.putExtra("category",categoryView.getText().toString());

        setResult(RESULT_OK,it);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (tagRadio.getCheckedRadioButtonId()) {
            case R.id.yellow:
                tag=0;
                //edt.setBackgroundColor(color[tag]);
                myLayout.setBackgroundResource(R.drawable.edit_bg_yellow);
                break;
            case R.id.blue:
                tag=1;
                //edt.setBackgroundColor(color[tag]);
                myLayout.setBackgroundResource(R.drawable.edit_bg_blue);
                break;
            case R.id.green:
                tag=2;
                //edt.setBackgroundColor(color[tag]);
                myLayout.setBackgroundResource(R.drawable.edit_bg_green);
                break;
            case R.id.red:
                tag=3;
                //edt.setBackgroundColor(color[tag]);
                myLayout.setBackgroundResource(R.drawable.edit_bg_red);
                break;
            case R.id.white:
                tag=4;
                //edt.setBackgroundColor(color[tag]);
                myLayout.setBackgroundResource(R.drawable.edit_bg_white);
                break;
            default:
                break;
        }
    }
}
