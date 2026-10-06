package com.example.nithuinstitueapp.model;

import java.sql.Date;

public class Student {
    private int id;
    private String name;
    private Date dob;
    private String gender;
    private String grade;
    private String address;
    private String phone;
    private String email;

    private int parentId;
    private String parentName;
    private String parentContact;
    private String parentRelation;

    public Student(String name, Date dob, String gender, String grade, String address, String phone, String email,
                   String parentName, String parentContact, String parentRelation) {
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.grade = grade;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.parentName = parentName;
        this.parentContact = parentContact;
        this.parentRelation = parentRelation;
    }

    public int getId(){ return id;}
    public String getName() { return name; }
    public Date getDob() { return dob; }
    public String getGender() { return gender; }
    public String getGrade() { return grade; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public int getParentId(){ return parentId;}
    public String getParentName() { return parentName; }
    public String getParentContact() { return parentContact; }
    public String getParentRelation() { return parentRelation; }

    public void setId (int id){
        this.id = id;
    }
    public void setParentId(int id){
        this.parentId = id;
    }
}