package ia.app.ltia.model;

import java.util.Date;

public class ExerciseEvent {
    
    private int exerciseId;
    private Date exerciseDate;
    private double durationMinutes;
    private double caloriesBurned;

    public ExerciseEvent(){}
    
    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    public double getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(double durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }   
    
    public String toString() {
        return "Exercise id: " + this.exerciseId + " date: " + this.exerciseDate + " calories burned: " + this.caloriesBurned;
    }
    
}
