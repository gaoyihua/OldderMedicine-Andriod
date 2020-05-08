package com.gary.olddermedicine.view.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gary.olddermedicine.R;
import com.gary.olddermedicine.view.pojo.Medicine;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EditRecord extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, RadioGroup.OnCheckedChangeListener, View.OnLongClickListener {
    LinearLayout myLayout;
    TextView date_text;
    TextView time_text;
    TextView selectMedicine;
    ImageButton alarm_button;
    EditText edt;
    TextView av;
    TextView chooseMedicine;
    RadioGroup tagRadio;
    RadioButton rdButton;
    int tag;
    String textDate;
    String textTime;
    String mainText;
    String medicineInfo;
    int num=0;
    String alarm;
    int alarm_hour=0;
    int alarm_minute=0;
    int alarm_year=0;
    int alarm_month=0;
    int alarm_day=0;
    private AlertDialog alertDialog3;
    private AlertDialog alertDialog2;
    private Set<Integer> rateSets = new HashSet<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record_edit);

        Intent it=getIntent();
        getInformationFromMain(it);

        rateSets.add(0);

        myLayout =  findViewById(R.id.whole);
        myLayout.setBackgroundResource(R.drawable.edit_bg_yellow);

        date_text= findViewById(R.id.dateText);
        time_text= findViewById(R.id.timeText);
        alarm_button= findViewById((R.id.alarmButton));
        edt= findViewById(R.id.editText);
        av= findViewById(R.id.alarmView);

        date_text.setText(textDate);
        time_text.setText(textTime);
        edt.setText(mainText);

        av.setOnLongClickListener(this);
        if(alarm.length()>1) av.setText("Alert at "+alarm+"!");
        else av.setVisibility(View.GONE);

        tagRadio= findViewById(R.id.tagRadio);
        tagRadio.setOnCheckedChangeListener(this);

        setRadioButtonCheckedAccordingToTag(tag);
        rdButton.setChecked(true);

        selectMedicine = findViewById(R.id.select_medicine);
        selectMedicine.setText(medicineInfo);
        chooseMedicine = findViewById(R.id.choose_medicine);
        final List<Medicine> medicineList = DataSupport.findAll(Medicine.class);
        List<String> nameList = new ArrayList<>(medicineList.size());
        for (Medicine medicine : medicineList) {
            nameList.add(medicine.getName());
        }
        final String[] nameArray = nameList.toArray(new String[0]);
        final boolean[] medicineArray = new boolean[medicineList.size()];
        final Set<String> selectMedicineSets = new HashSet<>();
        chooseMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(EditRecord.this);
                alertBuilder.setTitle("请选择药物");
                alertBuilder.setMultiChoiceItems(nameArray, medicineArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked){
                            Toast.makeText(EditRecord.this, "选择" + nameArray[which], Toast.LENGTH_SHORT).show();
                            medicineArray[which] = true;

                            //selectMedicineSets.add(nameArray[which]);
                            selectMedicineSets.add(medicineList.get(which).toString());
                        }else {
                            Toast.makeText(EditRecord.this, "取消选择" + nameArray[which], Toast.LENGTH_SHORT).show();
                            medicineArray[which] = false;
                            //selectMedicineSets.remove(nameArray[which]);
                            selectMedicineSets.remove(medicineList.get(which).toString());
                        }
                        StringBuilder sb = new StringBuilder();
                        for (String medicine : selectMedicineSets) {
                            sb.append(medicine);
                            sb.append("|");
                        }
                        selectMedicine.setText(sb.toString());
                    }
                });
                alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog2.dismiss();
                    }
                });

                alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog2.dismiss();
                    }
                });
                alertDialog2 = alertBuilder.create();
                alertDialog2.show();
            }
        });

    }
    private void getInformationFromMain(Intent it) {
        num=it.getIntExtra("num",0);

        tag=it.getIntExtra("tag",0);
        textDate=it.getStringExtra("textDate");
        textTime=it.getStringExtra("textTime");

        alarm=it.getStringExtra("alarm");
        mainText=it.getStringExtra("mainText");
        medicineInfo=it.getStringExtra("medicineInfo");
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

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        alarm_year=year;
        alarm_month=monthOfYear+1;
        alarm_day=dayOfMonth;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        alarm_hour=hourOfDay;
        alarm_minute=minute;

        alarm=alarm_year+"-"+alarm_month+"-"+alarm_day+"-"+alarm_hour+"-"+alarm_minute;
        av.setText("Alert at "+alarm+"!");
        av.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Alarm will be on at "+alarm+" !",Toast.LENGTH_LONG).show();
    }

    public void onSave(View v) {
        returnResult();
        finish();
    }

    private void returnResult() {
        Intent it=new Intent();

        it.putExtra("tag",tag);
        it.putExtra("alarm",alarm);
        it.putExtra("mainText",edt.getText().toString());
        it.putExtra("medicineInfo",selectMedicine.getText());
        it.putIntegerArrayListExtra("rate", new ArrayList<>(rateSets));

        setResult(RESULT_OK,it);
    }

    public void setAlarm(View v) {
        if(alarm.length()<=1) {
            Calendar c=Calendar.getInstance();
            alarm_hour=c.get(Calendar.HOUR_OF_DAY);
            alarm_minute=c.get(Calendar.MINUTE);

            alarm_year=c.get(Calendar.YEAR);
            alarm_month=c.get(Calendar.MONTH)+1;
            alarm_day=c.get(Calendar.DAY_OF_MONTH);
        } else {
            int i=0, k=0;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_year=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_month=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_day=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            while(i<alarm.length()&&alarm.charAt(i)!='-') i++;
            alarm_hour=Integer.parseInt(alarm.substring(k,i));
            k=i+1;i++;
            alarm_minute=Integer.parseInt(alarm.substring(k));
        }
        new TimePickerDialog(this,2,this,alarm_hour,alarm_minute,true).show();
        new DatePickerDialog(this,2,this,alarm_year,alarm_month-1,alarm_day).show();
        final String[] items = {"仅一次", "一天一次", "二天一次", "三天一次", "四天一次", "五天一次", "六天一次", "七天一次"};
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("请选择提醒时间");
        alertBuilder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rateSets.clear();
                Toast.makeText(EditRecord.this, "选择" + items[which], Toast.LENGTH_SHORT).show();
                rateSets.add(which);
                System.out.println(rateSets.toString());
            }
        } );
        alertBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (rateSets.isEmpty()) {
                    rateSets.add(0);
                }
                alertDialog3.dismiss();
            }
        });

        alertBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (rateSets.isEmpty()) {
                    rateSets.add(0);
                }
                alertDialog3.dismiss();
            }
        });
        alertDialog3 = alertBuilder.create();
        alertDialog3.show();
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==R.id.alarmView||v.getId()==R.id.alarmButton) {
            alarm="";
            av.setVisibility(View.GONE);
        }
        return true;
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
