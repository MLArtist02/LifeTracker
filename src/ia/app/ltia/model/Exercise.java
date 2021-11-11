package ia.app.ltia.model;

public class Exercise {
    private int exerciseId;
    private String exerciseName;
    private double durationInMinutes;
    private double caloriesBurned;

    public Exercise(int exerciseId, String exerciseName, double durationInMinutes, double caloriesBurned) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.durationInMinutes = durationInMinutes;
        this.caloriesBurned = caloriesBurned;
    }
    
    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public double getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(double durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(double caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }
    
    public String toString(){
        return this.exerciseName;
    }    
}