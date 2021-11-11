package ia.app.ltia.model;

import java.util.Date;

/**
 *
 * @author
 */
public class CalorieRecord implements Comparable<CalorieRecord> {
    
    private Date date;
    private double calories;

    public CalorieRecord() {
    }
    
    public CalorieRecord(Date date, double calories) {
        this.date = date;
        this.calories = calories;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    @Override
    public int compareTo(CalorieRecord o) {
        if(this.date.getTime() < o.date.getTime()){
            return -1;
        }
        else if(this.date.getTime() > o.date.getTime()){ 
            return 1;
        }
        else {
            return 0;
        }            
    }        
}
