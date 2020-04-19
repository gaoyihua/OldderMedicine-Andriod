package com.gary.olddermedicine.view.pojo;

import org.litepal.crud.DataSupport;

public class Medicine extends DataSupport {
    private int id;
    private String name;
    private int num;
    private String dosage;
    private int remain;
    private int tag;

    public Medicine( String name, int remain, String dosage, int num, int tag) {
        this.name = name;
        this.num = num;
        this.dosage = dosage;
        this.remain = remain;
        this.tag = tag;
    }

    public Medicine(String name, int remain, String dosage, int num) {
        this.name = name;
        this.remain = remain;
        this.dosage = dosage;
        this.num = num;
    }

    public Medicine(int id, String name, int num, String dosage, int remain) {
        this.id = id;
        this.name = name;
        this.num = num;
        this.dosage = dosage;
        this.remain = remain;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return name + "-" + dosage;
    }
}
