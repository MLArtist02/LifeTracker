package ia.app.ltia.model;

import ia.app.ltia.LifeTrackerHelper;
import java.util.Date;

public class HealthProgress {     
    private int weight;
    private int height;
    private Date date;
    private double bmi;
    
    public HealthProgress(){}
    
    public HealthProgress(int weight, int height, Date date){
        this.weight = weight;
        this.height = height;
        this.date = date;
        this.bmi = LifeTrackerHelper.getBMI(weight, height);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }    
}