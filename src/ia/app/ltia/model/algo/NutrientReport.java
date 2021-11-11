package ia.app.ltia.model.algo;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.LifeTrackerHelper;
import ia.app.ltia.model.Meal;
import ia.app.ltia.model.MealEvent;
import ia.app.ltia.model.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Collections;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
public class NutrientReport {
    
    private static final Logger logger = LogManager.getLogger(NutrientReport.class);
    
    private Member member;
    private Date mealdate;
    private double sumOfCarbs = 0;
    private double sumOfProtein = 0;
    private double sumOfFat = 0;
    private double sumOfFibre = 0;
    private double sumOfAll = 0;
    private String highestNutrient;
    private String highestFood;
    private String recommendedNutrients;
    
    private double calories = 0;    
    
    private double carbsPercent;
    private double fatPercent;
    private double proteinPercent;
    private double fibrePercent;    
    
    private ArrayList<NutrientConsumption> fatConsumed;
    private ArrayList<NutrientConsumption> carbsConsumed;
    private ArrayList<NutrientConsumption> proteinConsumed;
    private ArrayList<NutrientConsumption> fibreConsumed;    
    
    private enum WeightGoal {
        LOSE_WEIGHT,
        GAIN_WEIGHT
    }
    
    private WeightGoal weightGoal;

    public NutrientReport(Member member, Date mealdate) {
        
        this.member = member;
        this.mealdate = mealdate;
        
        this.fatConsumed = new ArrayList<NutrientConsumption>();
        this.carbsConsumed = new ArrayList<NutrientConsumption>();
        this.proteinConsumed = new ArrayList<NutrientConsumption>();
        this.fibreConsumed = new ArrayList<NutrientConsumption>();
        
        WeightChallenge wc = new WeightChallenge(member);
        if (wc.getWeightLoseOrGainOrMaintain().equals(LifeTrackerConstants.WEIGHT_GOAL_LOSE)){
            weightGoal = WeightGoal.LOSE_WEIGHT;
        }
        else {
            weightGoal = WeightGoal.GAIN_WEIGHT;
        }

        //calculate all the nutrient report information
        calculateNutrientsReport();
        
    }
    
    private void calculateNutrientsReport(){
        
        ArrayList<MealEvent> mealEvents = new ArrayList<>();
        mealEvents = this.member.getMealEvents();
        
        //loop through each mealEvent and get the nutrients only for the mealDate        
        Iterator i = mealEvents.iterator();        
        while(i.hasNext()){
            
            MealEvent me = (MealEvent)i.next();
            
            if(me.getMealDate().equals(this.mealdate)) {
                
                Meal meal = me.getMeal();          
                double fat = meal.getFat() * LifeTrackerHelper.getServingSizeRatio(meal, me.getServingSize());
                double carbs = meal.getCarbs() * LifeTrackerHelper.getServingSizeRatio(meal, me.getServingSize());
                double protein = meal.getProtein() * LifeTrackerHelper.getServingSizeRatio(meal, me.getServingSize());
                double fiber = meal.getFiber() * LifeTrackerHelper.getServingSizeRatio(meal, me.getServingSize());
                
                NutrientConsumption fatConsumption = new NutrientConsumption(meal.getMealName(), fat);
                this.fatConsumed.add(fatConsumption);
                
                NutrientConsumption carbsConsumption = new NutrientConsumption(meal.getMealName(), carbs);
                this.carbsConsumed.add(carbsConsumption);
                
                NutrientConsumption proteinConsumption = new NutrientConsumption(meal.getMealName(), protein);
                this.proteinConsumed.add(proteinConsumption);
                
                NutrientConsumption fibreConsumption = new NutrientConsumption(meal.getMealName(), fiber);
                this.fibreConsumed.add(fibreConsumption);                                                              
                
                this.sumOfFat += fat;
                this.sumOfCarbs += carbs;
                this.sumOfProtein += protein;
                this.sumOfFibre += fiber; 
                this.calories += meal.getCalories();
                
                logger.debug("Calories: " + this.calories);                
            }           
        }
        
        this.sumOfAll = this.sumOfCarbs + this.sumOfFat + this.sumOfFibre + this.sumOfProtein;
        this.carbsPercent = (this.sumOfCarbs/this.sumOfAll)*100;
        this.proteinPercent = (this.sumOfProtein/this.sumOfAll)*100;
        this.fatPercent = (this.sumOfFat/this.sumOfAll)*100;
        this.fibrePercent = (this.sumOfFibre/this.sumOfAll)*100;        
        
        calculatetReportStatements();
    }

    public Member getMember() {
        return member;
    }

    public Date getMealdate() {
        return mealdate;
    }

    public double getSumOfCarbs() {
        return sumOfCarbs;
    }

    public double getSumOfProtein() {
        return sumOfProtein;
    }

    public double getSumOfFat() {
        return sumOfFat;
    }

    public double getSumOfFibre() {
        return sumOfFibre;
    }
    
    private void calculatetReportStatements() {
        
        if (this.sumOfCarbs == 0 && this.sumOfProtein == 0 && this.sumOfFat == 0) {
            this.highestNutrient = "No nutrient information found!";
            this.highestFood = "";
            this.recommendedNutrients = "";
        }
        else {
        
            String highestNutrientString = "On this date your diet contains a high percentage in ";

            //Get highest nutrient
            double[] nutrients = { this.sumOfCarbs, this.sumOfFat, this.sumOfProtein };
            String[] nutrientLabels = { LifeTrackerConstants.CARBS_LABEL, LifeTrackerConstants.FAT_LABEL, LifeTrackerConstants.PROTEIN_LABEL };
            double highest = 0;
            int highestIndex = 0;


            for (int i = 0; i < nutrients.length; i++){
                if (nutrients[i] >  highest){
                    highest = nutrients[i];
                    highestIndex = i;
                }                
            }
            
            // Now set the statements for display
            
            // set the statement for the highest nutrient
            // e.g. On this date your diet contains a high percentage in FAT or CARBS or PROTEIN
            this.highestNutrient = highestNutrientString + nutrientLabels[highestIndex] + ".";
            
            //set the statement for the meals most responsible for the highest nutrient
            //and set the advice statement
            if(Arrays.asList(nutrientLabels).indexOf(LifeTrackerConstants.CARBS_LABEL) == highestIndex) {
                this.highestFood = generateHighestFoodStatement(this.carbsConsumed);
            }
            else if (Arrays.asList(nutrientLabels).indexOf(LifeTrackerConstants.FAT_LABEL) == highestIndex) {
                this.highestFood = generateHighestFoodStatement(this.fatConsumed);
            }
            else if (Arrays.asList(nutrientLabels).indexOf(LifeTrackerConstants.PROTEIN_LABEL) == highestIndex) {
                this.highestFood = generateHighestFoodStatement(this.proteinConsumed);
            }
            
            this.recommendedNutrients = generateRecommendedNutrientsStatement();
        }
    }
    
    private String generateRecommendedNutrientsStatement(){
        
        String recommendation = "";
        String caloriesRecommendation = "";
        String fatRecommendation = "";
        String proteinRecommendation = "";
        String carbsRecommendation = "";
        
        if(weightGoal.equals(WeightGoal.LOSE_WEIGHT)){
            
            if(this.calories > LifeTrackerConstants.DAILY_RECOMMENDED_MAINTENANCE_CALORIES){
                caloriesRecommendation = "You consumed more than your daily calories.\n";
            }
            
            if(this.fatPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_FAT_MIN_LIMIT_PERCENT){
                fatRecommendation = "FATS: Not enough consumed.\n";
            }
            else if(this.fatPercent >= LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_FAT_MIN_LIMIT_PERCENT && 
                    this.fatPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_FAT_MAX_LIMIT_PERCENT){
                fatRecommendation = "FATS: Good result, recommended portion consumed.\n";
            }
            else if(this.fatPercent > LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_FAT_MAX_LIMIT_PERCENT){
                fatRecommendation = "FATS: Bad result, consumed more than recommended portion.\n";
            }
            
            if(this.proteinPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Not enough consumed.\n";
            }
            else if(this.proteinPercent >= LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT && 
                    this.proteinPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Good result, recommended portion consumed.\n";
            }
            else if(this.proteinPercent > LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Bad result, consumed more than recommended portion.\n";
            }            
            
            if(this.carbsPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_CARB_MIN_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Not enough consumed.\n";
            }
            else if(this.carbsPercent >= LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_CARB_MIN_LIMIT_PERCENT && 
                    this.carbsPercent < LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_CARB_MAX_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Good result, recommended portion consumed.\n";
            }
            else if(this.carbsPercent > LifeTrackerConstants.WEIGHT_LOSS_RECOMMENDED_CARB_MAX_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Bad result, consumed more than the recommended portion.\n";
            }                        
            
        }        
        else if(weightGoal.equals(WeightGoal.GAIN_WEIGHT)){
            
            if(this.calories <= LifeTrackerConstants.DAILY_RECOMMENDED_MAINTENANCE_CALORIES){
                caloriesRecommendation = "You have not consumed enough calories.\n";
            }
            
            if(this.fatPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_FAT_MIN_LIMIT_PERCENT){
                fatRecommendation = "FATS: Not enough consumed.\n";
            }
            else if(this.fatPercent >= LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_FAT_MIN_LIMIT_PERCENT && 
                    this.fatPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_FAT_MAX_LIMIT_PERCENT){
                fatRecommendation = "FATS: Good result, recommended portion consumed.\n";
            }
            else if(this.fatPercent > LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_FAT_MAX_LIMIT_PERCENT){
                fatRecommendation = "FATS: Bad result, consumed more than recommended portion.\n";
            }
            
            if(this.proteinPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Not enough consumed.\n";
            }
            else if(this.proteinPercent >= LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT && 
                    this.proteinPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Good result, recommended portion consumed.\n";
            }
            else if(this.proteinPercent > LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT){
                proteinRecommendation = "PROTEIN: Bad result, consumed more than recommended protein.\n";
            }            
            
            if(this.carbsPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_CARB_MIN_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Not enough consumed.\n";
            }
            else if(this.carbsPercent >= LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_CARB_MIN_LIMIT_PERCENT && 
                    this.carbsPercent < LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_CARB_MAX_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Good result, recommended portion consumed.\n";
            }
            else if(this.carbsPercent > LifeTrackerConstants.WEIGHT_GAIN_RECOMMENDED_CARB_MAX_LIMIT_PERCENT){
                carbsRecommendation = "CARBS: Bad result, consumed more than recommended portion.\n";
            }                        
            
        }                
        
        recommendation = caloriesRecommendation + "\n" + fatRecommendation + "\n" + proteinRecommendation + "\n" + carbsRecommendation;
        
        return recommendation;
        
    }
    
    private String generateHighestFoodStatement(ArrayList<NutrientConsumption> ncList){
        
        String foodsForHighestNutrients = "";
        
        if(ncList.size()>0){
            
            Collections.sort(ncList);
            String foodForHighestNutrientPart1, foodForHighestNutrientPart2, foodForHighestNutrientPart3;
            foodForHighestNutrientPart1 = "This is primarily from:\n";
            foodForHighestNutrientPart2 = "";
            foodForHighestNutrientPart3 = "";

            int i = 1;
            Iterator iter = ncList.iterator();
            while (iter.hasNext()){

                NutrientConsumption nc = (NutrientConsumption)iter.next();

                i++;

                if (i==2){
                    foodForHighestNutrientPart2 = "1. " + nc.mealName + "\n";                        
                }
                else if (i==3){
                    foodForHighestNutrientPart3 = "2. " + nc.mealName + "\n";
                }
                else if (i>3){
                    break;
                }                          
            }

            foodsForHighestNutrients = foodForHighestNutrientPart1 + foodForHighestNutrientPart2 + foodForHighestNutrientPart3;
        }
                            
        return foodsForHighestNutrients;
    }
    
    public String getHighestNutrient() {
        
        return highestNutrient;
    }

    public String getHighestFood() {
        return highestFood;
    }

    public String getRecommendedFood() {
        return recommendedNutrients;
    }
    
    public String toString() {
        
        return "NutrientReport: " + this.member.getUsername() + "\n" + 
               "Date: " + this.mealdate + "\n" + 
                "Carbs: " + this.sumOfCarbs + "\n" + 
                "Protein: " + this.sumOfProtein + "\n" + 
                "Fat: " + this.sumOfFat + "\n" + 
                "Fibre: " + this.sumOfFibre + "\n";               
    }
    
    
    //inner class
    //https://stackoverflow.com/questions/11398122/what-are-the-purposes-of-inner-classes#11398251
    
    //Comparable interface
    //https://stackoverflow.com/questions/3718383/why-should-a-java-class-implement-comparable#3718515
    
    private class NutrientConsumption implements Comparable<NutrientConsumption> {
        
        String mealName;
        double gramsConsumed;

        public NutrientConsumption(String mealName, double gramsConsumed) {
            this.mealName = mealName;
            this.gramsConsumed = gramsConsumed;
        }
        
        @Override
        public int compareTo(NutrientConsumption nutrientConsumed) {
            
            if(this.gramsConsumed < nutrientConsumed.gramsConsumed)
                return 1;
            else if(this.gramsConsumed > nutrientConsumed.gramsConsumed)
                return -1;
            else
                return 0;            
        }
    }    
}
