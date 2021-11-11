package ia.app.ltia.model;

import ia.app.ltia.LifeTrackerConstants;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
public class Member {

    private static final Logger logger = LogManager.getLogger(Member.class);
    
    private MemberHealth memberHealth;
    private ArrayList<HealthProgress> healthProgresses;
    private ArrayList<CalorieRecord> calorieRecords;
    
    private ArrayList<MealEvent> mealEvents;
    private ArrayList<ExerciseEvent> exerciseEvents;    

    private int userId;
    private String username;
    private String password;
    private String email;
    
    private Database db;
    
    public Member() {
        
    }

    public Member(int userId, String username, String password, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;        
    }
    
    public Member(String username, String password, String email) {
        this(username, password, email, false);
    }
    
    public Member(String username, String password, String email, boolean isNewMember) {
        
        this.username = username;
        this.password = password;
        this.email = email;
        
        if (isNewMember) {
            db = new Database(LifeTrackerConstants.DATABASE_NAME);
            this.userId = db.addNewMember(this.username, this.password, this.email);
        }
    }
    
    public ArrayList<HealthProgress> getHealthProgresses() {
        return healthProgresses;
    }
    
    public void setHealthProgress(ArrayList<HealthProgress> healthProgresses) {
        this.healthProgresses = healthProgresses;
    }

    public void addHealthProgress(HealthProgress healthProgress) {
        this.healthProgresses.add(healthProgress);
        
        db = new Database(LifeTrackerConstants.DATABASE_NAME);
        db.updateHealth(this.userId, healthProgress.getWeight(), healthProgress.getHeight(), healthProgress.getDate());
    }
    
    public void addMealEvent(MealEvent mealEvent) {
        this.mealEvents.add(mealEvent);
    }
    
    public MemberHealth getMemberHealth() {
        return memberHealth;
    }

    public void addAndSetMemberHealth(MemberHealth memberHealth){        
        setMemberHealth(memberHealth);
        addMemberHealth(memberHealth);        
    }    
    
    public void setMemberHealth(MemberHealth memberHealth) {
        this.memberHealth = memberHealth;
    }
    
    private void addMemberHealth(MemberHealth memberHealth) {
        
        if(this.memberHealth == null){
            this.memberHealth = memberHealth;
        }            
        
        logger.debug("Add memberHealth to the database:");
        logger.debug(memberHealth.toString());        
        
        db = new Database(LifeTrackerConstants.DATABASE_NAME);
        db.addNewMemberHealth(this.userId, this.memberHealth.getAge(), 
                this.memberHealth.getHeight(), this.memberHealth.getGender(), 
                this.memberHealth.getWeight(), this.memberHealth.getStartDate(),
                this.memberHealth.getTargetWeight(), this.memberHealth.getTargetDate());       
        
    }
    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userid) {
        this.userId = userid;
    }
    
    public ArrayList<CalorieRecord> getCalorieRecords() {
        return calorieRecords;
    }

    public void setCalorieRecords(ArrayList<CalorieRecord> calorieRecords) {
        this.calorieRecords = calorieRecords;
    }    

    public ArrayList<MealEvent> getMealEvents() {
        return mealEvents;
    }

    public void setMealEvents(ArrayList<MealEvent> mealEvents) {
        this.mealEvents = mealEvents;
    }

    public ArrayList<ExerciseEvent> getExerciseEvents() {
        return exerciseEvents;
    }

    public void setExerciseEvents(ArrayList<ExerciseEvent> exerciseEvents) {
        this.exerciseEvents = exerciseEvents;
    }
    
    public String toString() {
        
        //TODO: check for nulls
        return "\nId = " + this.userId +
               "\nName = " + this.username +
               (this.memberHealth == null ? "\nMemberHealth not set (null)" : "\nMemberHealth = \n" + this.memberHealth.toString()) + 
               (this.healthProgresses == null ? "\nHealthProgresses not set (null)" : "\nHealthProgress count = " + this.healthProgresses.size());
    }    
}
