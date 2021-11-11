package ia.app.ltia.model;

import ia.app.ltia.LifeTrackerHelper;
import java.util.Date;

public class MemberHealth {
    private int age;
    private int height;
    private String gender;
    private int weight;
    private int targetWeight;
    private Date startDate;
    private Date targetDate;
    private double bmi;
    
    public MemberHealth() {}
    
    public MemberHealth(int age, int height, String gender, int weight, Date startDate, int targetWeight, Date targetDate) {
        this.age = age;
        this.height = height;
        this.gender = gender;
        this.weight = weight;
        this.targetWeight = targetWeight;
        this.targetDate = targetDate;
        this.startDate = startDate;
        this.bmi = LifeTrackerHelper.getBMI(weight, height);         
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }       

    public double getBmi() {
        return bmi;
    }
    
    public void setBmi(double bmi) {
        this.bmi = bmi;
    }    
    
    public String toString() {
        return "Age = " + this.age + 
               "\nHeight = " + this.height +
               "\nGender = " + this.gender + 
               "\nStart Date = " + this.startDate +
               "\nTarget Date = " + this.targetDate +
               "\nTarget Weight = " + this.targetWeight;
    }
}
