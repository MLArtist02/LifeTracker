package ia.app.ltia;

import java.awt.Color;
import java.text.SimpleDateFormat;

/**
 *
 * @author
 */
public class LifeTrackerConstants {
    
    private LifeTrackerConstants() {}
    
    //Colours
    public static final Color GUI_BACKGROUND_COLOR_1 = new Color(255, 240, 199);
    
    public static final Color GUI_DELETE_BUTTON_COLOR = new Color(255, 102, 102);
    
    public static final Color GUI_BACKGROUND_COLOR_2 = new Color(255,255,255);
    public static final Color GUI_FOREGROUND_COLOR_2 = new Color(247,247,247);
    public static final Color GUI_COLOR_3 = new Color(27,105,214);
    public static final Color GUI_FIELD_BACKGROUND_COLOR_2 = new Color(192, 223, 239);
    
    public static final Color GUI_BUTTON_BACKGROUND_COLOR_1 = new Color(27,105,214);
    public static final Color GUI_FIELD_ERROR_TEXT_COLOR = Color.red;
    
    public static final Color GUI_BUTTON_COLOR = new Color(153, 204, 255);
    public static final Color GUI_BUTTON_COLOR_2 = Color.BLACK;
    public static final Color GUI_BUTTON_TEXT_COLOR_2 = Color.WHITE;    

    //Limits    
    //https://www.caloriesecrets.net/what-is-a-healthy-amount-of-weight-to-lose-per-week/    
    
    public static final double WEIGHT_LOSS_KG_SAFE_WEEKLY_LIMIT = 0.675; // APPROX 1-2 pounds is safe to lose per week so go with 1.5 pounds = 0.68 kg
    public static final double WEIGHT_LOSS_CALORIES_SAFE_WEEKLY_LIMIT = 5250; // 1 pound = 3500 calories so this is 1.5 * 3500 = 
    
    //https://www.healthline.com/nutrition/best-macronutrient-ratio#diet-quality
    
    public static final double WEIGHT_LOSS_RECOMMENDED_CARB_MIN_LIMIT_PERCENT = 45;
    public static final double WEIGHT_LOSS_RECOMMENDED_CARB_MAX_LIMIT_PERCENT = 65;
    
    public static final double WEIGHT_LOSS_RECOMMENDED_FAT_MIN_LIMIT_PERCENT = 20;
    public static final double WEIGHT_LOSS_RECOMMENDED_FAT_MAX_LIMIT_PERCENT = 35;

    public static final double WEIGHT_LOSS_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT = 10;
    public static final double WEIGHT_LOSS_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT = 35;
    
    //https://relentlessgains.com/macronutrient-ratio-for-lean-muscle-gains-bulking-macros/
    
    public static final double WEIGHT_GAIN_RECOMMENDED_CARB_MIN_LIMIT_PERCENT = 40;
    public static final double WEIGHT_GAIN_RECOMMENDED_CARB_MAX_LIMIT_PERCENT = 50;
    
    public static final double WEIGHT_GAIN_RECOMMENDED_FAT_MIN_LIMIT_PERCENT = 15;
    public static final double WEIGHT_GAIN_RECOMMENDED_FAT_MAX_LIMIT_PERCENT = 25;

    public static final double WEIGHT_GAIN_RECOMMENDED_PROTEIN_MIN_LIMIT_PERCENT = 30;
    public static final double WEIGHT_GAIN_RECOMMENDED_PROTEIN_MAX_LIMIT_PERCENT = 40;    
    
    public static final double DAILY_RECOMMENDED_MAINTENANCE_CALORIES = 2238; //for 18 year old
    
    public static final double BMI_UNDERWEIGHT_MIN_LIMIT = 16;
    public static final double BMI_UNDERWEIGHT_MAX_LIMIT = 18.5;

    public static final double BMI_HEALTHY_MIN_LIMIT = 18.5;
    public static final double BMI_HEALTHY_MAX_LIMIT = 25;    
    
    public static final double BMI_OVERWEIGHT_MIN_LIMIT = 25;
    public static final double BMI_OVERWEIGHT_MAX_LIMIT = 30;
    
    public static final double BMI_OBESE_MIN_LIMIT = 30;      
    
    public static final int MAX_NUMBER_OF_MEMBERS = 4;    
    public static final int MIN_NUMBER_OF_EXERCISENAME_LETTERS = 3;        
    
    //Screen titles
    public static final String TITLE_LOGIN = "LifeTracker Login";    
    public static final String TITLE_NEW_MEMBER = "New Member";
    public static final String TITLE_HOME = "Welcome to LifeTracker";
    public static final String TITLE_DELETE_MEMBER = "Delete Member";
    public static final String TITLE_HEALTH = "Health and Exercise";
    public static final String TITLE_MEAL = "Meals";
    public static final String TITLE_STATISTICS = "Calorie Statistics";
    public static final String TITLE_WEIGHT_CHARTS = "Weight Charts";   
    
    //Icons
    public static final String CROSS_ICON = "/ia/app/ltia/images/cross2_red_icon_1.png";
    public static final String TICK_ICON = "/ia/app/ltia/images/tick_black_icon.png";
    public static final String EMPTY_ICON = "";
    
    //Database related
    public static final String DATABASE_NAME = "LifeTracker.db";
    
    //https://stackoverflow.com/questions/2881321/how-to-insert-date-in-sqlite-through-java#2881723
    //https://www.java67.com/2013/01/how-to-format-date-in-java-simpledateformat-example.html
    public static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat GUI_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat GUI_DETAILED_DATE_FORMAT = new SimpleDateFormat("EEEE dd/MM/yyyy");
    
    //Timezone
    public static final String MY_TIME_ZONE = "Singapore";
    
    //Email
    public static final String MAIL_HOST = "smtp.gmail.com";
    public static final String MAIL_PORT = "587";
    public static final String MAIL_FROM_EMAIL_ADDRESS = "ZA.IBProject.CS.IA@gmail.com";
    public static final String MAIL_FROM_PASSWORD = "yGV1yAy3F6l2JuwekapI";
    
    //Email scheduler
    public static final long MAIL_SCHEDULER_FREQUENCY_IN_MINUTES = 1440; //daily = 24 HOURS * 60 MINUTES
    
    //Labels
    public static final String WEIGHT_LABEL = "Weight (kg)";
    public static final String HEIGHT_LABEL = "Height (cm)";    
    
    public static final String EXERCISE_ADD_LABEL = "Exercise";
    public static final String EXERCISE_DURATION_LABEL = "Duration (mins)";
    public static final String EXERCISE_CALORIES_LABEL = "Calories burned";
    
    public static final String MEAL_ADD_LABEL = "Meal";
    public static final String MEAL_PORTION_LABEL = "Portion size (g)";    
    
    public static final String WEIGHT_PROGRESS_CHART_TITLE = "Weight Progress";
    public static final String WEIGHT_TARGET_CHART_TITLE = "Weight Target";
    public static final String WEIGHT_PROGRESS_CHART_TOGGLE_BUTTON = "Show Weight Progress";
    public static final String WEIGHT_TARGET_CHART_TOGGLE_BUTTON = "Show Weight Target";
    public static final String WEIGHT_SHOW_BOTH_BUTTON = "Show Weight Target and Progress";    
    
    public static final String PIECHART_NUTRIENT_BREAKDOWN = "Your Nutrient Breakdown";    
    public static final String PIECHART_NO_DATA_FOUND = "No Data Found";    
    
    public static final String CARBS_LABEL = "Carbs (g)";
    public static final String FAT_LABEL = "Fat (g)";
    public static final String PROTEIN_LABEL = "Protein (g)";
    public static final String FIBRE_LABEL = "Fibre (g)";

    public static final String WEIGHT_GOAL_LOSE = "LOSE";
    public static final String WEIGHT_GOAL_GAIN = "GAIN";
    public static final String WEIGHT_GOAL_MAINTAIN = "MAINTAIN SAME";
    
    //Messages
    public static final String DATABASE_ERROR_MESSAGE = "Database error occurred. Contact Zain.";
}
