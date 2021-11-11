package ia.app.ltia.model;

import java.util.Date;

public class MealEvent {   
    private Meal meal;
    private Date mealDate;
    private double servingSize;
    private double caloriesGained;

    public MealEvent(Meal meal, Date mealDate, double servingSize, double caloriesGained) {
        this.meal = meal;
        this.mealDate = mealDate;
        this.servingSize = servingSize;
        this.caloriesGained = caloriesGained;
    }

    public MealEvent() {}

    public Meal getMeal() {return meal;}
    
    public void setMeal(Meal meal) {
        this.meal = meal;
    }
    
    public Date getMealDate() {return mealDate;}
    
    public void setMealDate(Date mealDate) {
        this.mealDate = mealDate;
    }
    
    public double getServingSize() {return servingSize;}

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public double getCaloriesGained() {return caloriesGained;}

    public void setCaloriesGained(double caloriesGained) {
        this.caloriesGained = caloriesGained;
    }
    
    public String toString() {
        return "Meal id: " + this.meal.getMealId() + " date: " + this.mealDate + " calories gained: " + this.caloriesGained;
    }    
}
