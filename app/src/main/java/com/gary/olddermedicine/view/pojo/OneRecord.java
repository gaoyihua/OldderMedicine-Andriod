package com.gary.olddermedicine.view.pojo;

import java.util.List;

public class OneRecord {
    private int tag;
    private String textDate;
    private String textTime;
    private boolean alarm;
    private String mainText;
    private String medicineInfo;
    private List<Integer> rate;

    public OneRecord(String textDate, String textTime, boolean alarm, String mainText, int tag, List<Integer> rate, String medicineInfo) {
        this.textDate = textDate;
        this.textTime = textTime;
        this.alarm = alarm;
        this.mainText = mainText;
        this.tag = tag;
        this.rate = rate;
        this.medicineInfo = medicineInfo;
    }

    public String getMedicineInfo() {
        return medicineInfo;
    }

    public void setMedicineInfo(String medicineInfo) {
        this.medicineInfo = medicineInfo;
    }

    public List<Integer> getRate() {
        return rate;
    }

    public void setRate(List<Integer> rate) {
        this.rate = rate;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getTextDate() {
        return textDate;
    }

    public void setTextDate(String textDate) {
        this.textDate = textDate;
    }

    public String getTextTime() {
        return textTime;
    }

    public void setTextTime(String textTime) {
        this.textTime = textTime;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}
