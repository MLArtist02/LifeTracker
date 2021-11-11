package ia.app.ltia;

import ia.app.ltia.model.Exercise;
import ia.app.ltia.model.HealthProgress;
import ia.app.ltia.model.Meal;
import ia.app.ltia.model.Member;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
public class LifeTrackerHelper {
    
    private static final Logger logger = LogManager.getLogger(LifeTrackerHelper.class);
    
    public static boolean isValidDouble(String s){
        
        boolean isValidDouble = false;
        
        try {
            double d = Double.parseDouble(s);
            isValidDouble = true;
        }
        catch(NumberFormatException nfe) {
            isValidDouble = false;
        }
        
        return isValidDouble;        
    }
    
    public static boolean isValidString(String s){
        
        boolean isValidString = false;
        
        //lets make sure the exercise is not empty and at least certain number of letters long
        if(s.length() >= LifeTrackerConstants.MIN_NUMBER_OF_EXERCISENAME_LETTERS)
            isValidString = true;
        
        return isValidString;        
    }        
    
    public static double getCaloriesBurnedForExerciseEvent(Exercise exercise, double exerciseDurationInMinutes) {
        
        double caloriesBurnedPerMinuteForExercise = 0.0;
        double caloriesBurnedForExerciseEvent = 0.0;
        
        caloriesBurnedPerMinuteForExercise = exercise.getCaloriesBurned() / exercise.getDurationInMinutes();
        
        caloriesBurnedForExerciseEvent = caloriesBurnedPerMinuteForExercise * exerciseDurationInMinutes;
        
        logger.debug("Calories burned calculation");
        logger.debug("Exercise: " + exercise.getExerciseName() + ", calories burned " + exercise.getCaloriesBurned() + " per " + exercise.getDurationInMinutes() + " minutes");
        logger.debug("Calories burned per minute: " + caloriesBurnedPerMinuteForExercise);
        logger.debug("Exercise event duration: " + exerciseDurationInMinutes);
        logger.debug("Calculated calorie burn: " + caloriesBurnedForExerciseEvent);
        
        return caloriesBurnedForExerciseEvent;
        
    }
    
    public static double getCaloriesGainedForMealEvent(Meal meal, double servingSize) {
        
        double caloriesPerServingUnitForMeal = 0.0;
        double caloriesGainedForMealEvent = 0.0;
        
        caloriesPerServingUnitForMeal = meal.getCalories() / meal.getServingSize();        
        
        caloriesGainedForMealEvent = caloriesPerServingUnitForMeal * servingSize;
        
        logger.debug("Calories gained calculation");
        logger.debug("Meal: " + meal.getMealName() + ", calories energy: " + meal.getCalories() + " per " + meal.getServingSize() + " " + meal.getServiceSizeUnit());
        logger.debug("Calories gained per " + meal.getServiceSizeUnit() + ": " + caloriesPerServingUnitForMeal);
        logger.debug("Meal serving size: " + servingSize + " " + meal.getServiceSizeUnit());
        logger.debug("Calculated calorie gain: " + caloriesGainedForMealEvent);
        
        return caloriesGainedForMealEvent;        
    }    
    
    public static double getServingSizeRatio(Meal meal, double servingSize) {
        
        return servingSize / meal.getServingSize();        
    }
    
    public static Date getToday() {
        
        Calendar calendarToday = new GregorianCalendar();
        calendarToday.set(Calendar.HOUR_OF_DAY, 0);
        calendarToday.set(Calendar.MINUTE, 0);
        calendarToday.set(Calendar.SECOND, 0);
        calendarToday.set(Calendar.MILLISECOND, 0);
        
        Date today = calendarToday.getTime();
        
        return today;
        
    }
    
    public static long getDateDifferenceInDays(Date date1, Date date2) {
        
        long difference = date2.getTime() - date1.getTime();
        long differenceInDays = difference / (24 * 60 * 60 * 1000);
        
        return differenceInDays;        
    }
    
    public static double getBMI(double weightInKg , double heightInCentimetres) {
        
        double heightInMetres = heightInCentimetres/100;
        
        double bmi = weightInKg / Math.pow(heightInMetres, 2);
        
        return bmi;
        
    }

    public static double getLatestWeight(Member member) {
        
        ArrayList<HealthProgress> healthProgresses = member.getHealthProgresses();
        
        //get latest weight from HealthProgress
        if(healthProgresses.size() > 0){
            int lastElement = healthProgresses.size() - 1;
            HealthProgress hp = (HealthProgress)healthProgresses.get(lastElement); //get the last element
            
            return hp.getWeight();             
        } //but if no HealthProgress has been created, get starting weight from member health
        else{
            return member.getMemberHealth().getWeight();
        }            
    }
    
    public static double getLatestHeight(Member member) {
        
        ArrayList<HealthProgress> healthProgresses = member.getHealthProgresses();
        
        //get latest weight from HealthProgress
        if(healthProgresses.size() > 0){
            int lastElement = healthProgresses.size() - 1;
            HealthProgress hp = (HealthProgress)healthProgresses.get(lastElement); //get the last element
            
            return hp.getHeight();
        } //but if no HealthProgress has been created, get starting weight from member health
        else{
            return member.getMemberHealth().getHeight();
        }            
    }    
    
    public static boolean valPassword(String password){
        if(password.length()>=8){
            
        }
        else{
            System.out.println("Password Length is too small");
            return false;
        }
        return false;
    }
    
    public static boolean checkPassword(String password){
        boolean hasNum = false;
        boolean hasCap = false;
        boolean hasLow = false;
        char p;
        
        for(int i = 0; i < password.length(); i++){
            p = password.charAt(i);
            if(Character.isDigit(p)){
                hasNum = true;
            }
            else if(Character.isUpperCase(p)){
                hasCap = true;
            }
            else if(Character.isLowerCase(p)){
                hasLow = true;
            }
            if(hasNum && hasCap && hasLow){
                return true;
            }
        }
        return false;
    }    
    
    public static boolean checkName (String name){
        return Pattern.matches("[a-zA-Z]+", name);
    }    
    
    public static boolean checkEmail(String email){
       boolean emailresult = true;
       try{
           InternetAddress emailAddr = new InternetAddress(email);
           emailAddr.validate();
       }
       catch (AddressException ex){
           emailresult = false;
       }
       return emailresult;
    }
    
    public static boolean checkNumber(String number){
        return Pattern.matches("[0-9]+", number);
    }  
}
