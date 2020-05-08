package com.gary.olddermedicine.view.pojo;

import org.litepal.crud.DataSupport;

public class Record extends DataSupport {
    private int num;
    private int tag;
    private String textDate;
    private String textTime;
    private String alarm;
    private String mainText;
    private String rate;
    private String medicineInfo;
    private int id;

    public Record(int num, int tag, String textDate, String textTime, String alarm, String mainText, String rate, String medicineInfo) {
        this.num = num;
        this.tag = tag;
        this.textDate = textDate;
        this.textTime = textTime;
        this.alarm = alarm;
        this.mainText = mainText;
        this.rate = rate;
        this.medicineInfo = medicineInfo;
    }

    public Record(int num, int tag, String textDate, String textTime, String alarm, String mainText, String rate, String medicineInfo, int id) {
        this.num = num;
        this.tag = tag;
        this.textDate = textDate;
        this.textTime = textTime;
        this.alarm = alarm;
        this.mainText = mainText;
        this.rate = rate;
        this.medicineInfo = medicineInfo;
        this.id = id;
    }

    public Record() {
    }

    public String getMedicineInfo() {
        return medicineInfo;
    }

    public void setMedicineInfo(String medicineInfo) {
        this.medicineInfo = medicineInfo;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    //getter
    public int getNum(){
        return num;
    }
    public int getTag(){
        return tag;
    }
    public String getTextDate(){
        return textDate;
    }
    public String getTextTime(){
        return textTime;
    }
    public String getAlarm(){
        return alarm;
    }
    public String getMainText(){
        return mainText;
    }
    public int getId() { return id; }

    //setter
    public void setNum(int num) {
        this.num=num;
    }
    public void setTag(int tag){
        this.tag=tag;
    }
    public void setTextDate(String textDate){
        this.textDate=textDate;
    }
    public void setTextTime(String textTime){
        this.textTime=textTime;
    }
    public void setAlarm(String alarm){
        this.alarm=alarm;
    }
    public void setMainText(String mainText){
        this.mainText=mainText;
    }
    public void setId(int id){ this.id=id; }
}
