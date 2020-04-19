package com.gary.olddermedicine.view.pojo;

public class OmUser {
    private Integer id;

    private String name;

    private Integer sex;

    private String phone;

    private String password;

    private String emergencyPhone;

    private String emergencyPeople;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEmergencyPhone() {
        return emergencyPhone;
    }

    public void setEmergencyPhone(String emergencyPhone) {
        this.emergencyPhone = emergencyPhone == null ? null : emergencyPhone.trim();
    }

    public String getEmergencyPeople() {
        return emergencyPeople;
    }

    public void setEmergencyPeople(String emergencyPeople) {
        this.emergencyPeople = emergencyPeople == null ? null : emergencyPeople.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    @Override
    public String toString() {
        return "OmUser{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", emergencyPhone='" + emergencyPhone + '\'' +
                ", emergencyPeople='" + emergencyPeople + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}