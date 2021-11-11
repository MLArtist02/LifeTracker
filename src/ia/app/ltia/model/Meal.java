package ia.app.ltia.model;

public class Meal {    
    private int mealId; //grams
    private String mealName; //grams
    private double protein; //grams
    private double carbs; //grams
    private double fiber; //grams
    private double fat; //grams
    private double caloriesEnergy; //kcal
    private double servingSize; //measure
    private String serviceSizeUnit; //g or ml

    public Meal(int mealId, String mealName, double protein, double carbs, double fiber, double fat, double caloriesEnergy, double servingSize, String serviceSizeUnit) {
        this.mealId = mealId;
        this.mealName = mealName;
        this.protein = protein;
        this.carbs = carbs;
        this.fiber = fiber;
        this.fat = fat;
        this.caloriesEnergy = caloriesEnergy;
        this.servingSize = servingSize;
        this.serviceSizeUnit = serviceSizeUnit;
    }

    public Meal() {}
    
    public int getMealId() {
        return mealId;
    }

    public void setMealId(int mealId) {
        this.mealId = mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public double getFiber() {
        return fiber;
    }

    public void setFiber(double fiber) {
        this.fiber = fiber;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCalories() {
        return caloriesEnergy;
    }

    public void setCalories(double caloriesEnergy) {
        this.caloriesEnergy = caloriesEnergy;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public String getServiceSizeUnit() {
        return serviceSizeUnit;
    }

    public void setServiceSizeUnit(String serviceSizeUnit) {
        this.serviceSizeUnit = serviceSizeUnit;
    }
    
    public String toString() {
        return this.mealName;
    }    
}