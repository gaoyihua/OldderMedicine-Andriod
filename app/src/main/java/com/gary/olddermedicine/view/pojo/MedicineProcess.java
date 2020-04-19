package com.gary.olddermedicine.view.pojo;

import org.litepal.crud.DataSupport;

public class MedicineProcess extends DataSupport {
    private int id;
    private long time;
    private String medicineInfo;
    private boolean isFinish;

    public MedicineProcess() {
    }

    public MedicineProcess(int id, long time, String medicineInfo, boolean isFinish) {
        this.id = id;
        this.time = time;
        this.medicineInfo = medicineInfo;
        this.isFinish = isFinish;
    }

    public MedicineProcess(long time, String medicineInfo, boolean isFinish) {
        this.time = time;
        this.medicineInfo = medicineInfo;
        this.isFinish = isFinish;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMedicineInfo() {
        return medicineInfo;
    }

    public void setMedicineInfo(String medicineInfo) {
        this.medicineInfo = medicineInfo;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }
}
