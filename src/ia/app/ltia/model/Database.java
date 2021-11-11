package ia.app.ltia.model;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.LifeTrackerHelper;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author
 */
public class Database {
    
    private static final Logger logger = LogManager.getLogger(Database.class);
    
    private int userId;
    private String dbName;
    private final String url;
    
    public Database(String dbName) {
        
        this.dbName = dbName;
        url = "jdbc:sqlite:" + dbName;
        
        logger.info("JDBC URL = " + url);
    }
    
    public Connection connect() {
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return conn;
    }
    
    
    public int getUserId(String userName) {
    
        int userId = -1;
        String sql = "SELECT ID FROM USER WHERE NAME = \"" + userName + "\"";

        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                    userId = rs.getInt("ID");
                }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
       
        return userId;
    }
    
    public int addNewMember(String username, String password, String email){
        
        int userId;
        String sql = "insert into USER (NAME, PASSWORD, EMAIL) values (?, ?, ?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("Error when adding member's health data: " + ex.getMessage());
        }
        return getUserId(username);
    }
    
    public void addNewMemberHealth(int userId, int age, int height, String gender, int weight, Date startDate, int targetWeight, Date targetDate){
       
        String startDateString = LifeTrackerConstants.DB_DATE_FORMAT.format(startDate);
        String targetDateString = LifeTrackerConstants.DB_DATE_FORMAT.format(targetDate);
        
        logger.info("inside db addNewMemberHealth");
        logger.info("user id " + userId + " age =" + age + " height=" + height + " gender=" + gender + " weight=" + weight + " startDate=" + startDateString + " targetWeight =" + targetWeight + " targetDate=" + targetDateString);
        
        String sql = "insert into USERHEALTH (USERID, AGE, HEIGHT, GENDER, WEIGHT, STARTDATE, TARGETWEIGHT, TARGETDATE) values (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, age);
            pstmt.setInt(3, height);
            pstmt.setString(4, gender);
            pstmt.setInt(5, weight);
            pstmt.setString(6, startDateString);
            pstmt.setInt(7, targetWeight);
            pstmt.setString(8, targetDateString);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("Error when adding member's health data: " + ex.getMessage());
        }
    }
  
    public void addNewExercise(String exerciseName, double caloriesBurned, double durationInMinutes){
        
        String sql = "INSERT INTO EXERCISE (EXERCISENAME, CALORIES, DURATIONMINUTES) VALUES (?, ?, ?)";
        
        logger.debug("About to add new exercise: " + exerciseName + ", caloriesBurned: " + caloriesBurned + ", duration: " + durationInMinutes);
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, exerciseName);
            pstmt.setDouble(2, caloriesBurned);
            pstmt.setDouble(3, durationInMinutes);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            logger.error("Error when adding new exercise: " + ex.getMessage());
        }
    }    
    
    public boolean addNewExerciseEvent(int memberId, int exerciseId, Date exerciseDate, double caloriesBurned, double durationInMinutes){
        
        boolean status = true;
        
        String sql = "INSERT INTO EXERCISEEVENT (USERID, EXERCISEID, DATE, CALORIESBURNED, DURATIONMINUTES) VALUES (?, ?, ?, ?, ?)";
        
        String exerciseDateAsString = LifeTrackerConstants.DB_DATE_FORMAT.format(exerciseDate);
        
        logger.debug("About to add new exercise event: " + exerciseId + ", date: " + exerciseDate + " caloriesBurned: " + caloriesBurned + ", duration: " + durationInMinutes);
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, exerciseId);
            pstmt.setString(3, exerciseDateAsString);
            pstmt.setDouble(4, caloriesBurned);
            pstmt.setDouble(5, durationInMinutes);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            logger.error("Error when adding new exercise: " + ex.getMessage());
            status = false;
        }
        
        return status;
        
    }    
    
    public ArrayList getAllMembers() {

        ArrayList<Member> memberList = new ArrayList<Member>();//Creating arraylist  
        
        String sql = "select ID, NAME, PASSWORD, EMAIL from USER ORDER BY ID limit " 
                + LifeTrackerConstants.MAX_NUMBER_OF_MEMBERS;
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            int i=0;
            
            while (rs.next()) {
                Member member = new Member();
                
                int id = rs.getInt("ID");
                String name = rs.getString("NAME");
                String password = rs.getString("PASSWORD");
                String email = rs.getString("EMAIL");
                
                member.setUserId(id);
                member.setUsername(name);
                member.setPassword(password);
                member.setEmail(email);
                
                memberList.add(member);
                i++;
                
                logger.info("Loading member " + i + ": " + member.getUserId() + 
                        ", " + member.getUsername());                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return memberList;
    }
    
    public boolean authenticateLogin(String username, String password) {
        boolean checkPassword = false;
        String sql = "SELECT NAME, PASSWORD from USER WHERE NAME = \"" 
                + username + "\"";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String hash = rs.getString("PASSWORD");
                
                if (BCrypt.checkpw(password, hash)) {
                    checkPassword = true;
                }
                else{
                    checkPassword = false;
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return checkPassword;
    }
    
    public void updateHealth(int userId, int weight, int height, Date date){

        String sql = "insert into UPDATEHEALTH (USERID, WEIGHT, HEIGHT, DATE) values (?, ?, ?, ?)";
        
        String dateString = LifeTrackerConstants.DB_DATE_FORMAT.format(date);
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, weight);
            pstmt.setInt(3, height);
            pstmt.setString(4, dateString);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            System.out.println("Error when updating member's health data: " + ex.getMessage());
        }
    }

    public MemberHealth getMemberHealth(int userId) {
        
        logger.debug("Get member health for user id: " + userId);
        
        //TODO: initialise memberHealth
        MemberHealth memberHealth = new MemberHealth(); 
        
        String sql = "select AGE, HEIGHT, GENDER, WEIGHT, STARTDATE, TARGETWEIGHT, TARGETDATE FROM USERHEALTH WHERE USERID = " + userId;
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                
                int age = rs.getInt("AGE");
                memberHealth.setAge(age);
                
                int height = rs.getInt("HEIGHT");
                memberHealth.setHeight(height);               
                
                String gender = rs.getString("GENDER");
                memberHealth.setGender(gender);
                
                int weight = rs.getInt("WEIGHT");
                memberHealth.setWeight(weight);            
                memberHealth.setBmi(LifeTrackerHelper.getBMI(weight, height));
                
                int targetWeight = rs.getInt("TARGETWEIGHT");                
                memberHealth.setTargetWeight(targetWeight);                

                String targetDateString = rs.getString("TARGETDATE");
                logger.debug("Member health target date = " + targetDateString);
                Date targetDate;                
                try {
                    targetDate = LifeTrackerConstants.DB_DATE_FORMAT.parse(targetDateString);
                    memberHealth.setTargetDate(targetDate);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the target date from the database for this Member Health:");
                    logger.error(ex.toString());
                }
                
                String startDateString = rs.getString("STARTDATE");
                logger.debug("Member health start date = " + startDateString);
                Date startDate;                
                try {
                    startDate = LifeTrackerConstants.DB_DATE_FORMAT.parse(startDateString);
                    memberHealth.setStartDate(startDate);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the start date from the database for this Member Health:");
                    logger.error(ex.toString());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        //logger.debug("Returning member health: " + memberHealth.toString());
        
        return memberHealth;
        
    }
    
    public ArrayList<HealthProgress> getHealthProgresses(int userId) {
        
        ArrayList<HealthProgress> healthProgresses = new ArrayList<HealthProgress>();
        
        String sql = "select WEIGHT, HEIGHT, DATE FROM UPDATEHEALTH WHERE USERID = " + userId + " ORDER BY DATE ASC";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                //TODO:
                HealthProgress healthProgress = new HealthProgress();
                
                int weight = rs.getInt("WEIGHT");
                healthProgress.setWeight(weight);
                
                int height = rs.getInt("HEIGHT");
                healthProgress.setHeight(height);
                healthProgress.setBmi(LifeTrackerHelper.getBMI(weight, height));
                
                String dateString = rs.getString("DATE");
                Date date;
                try {
                    date = LifeTrackerConstants.DB_DATE_FORMAT.parse(dateString);
                    healthProgress.setDate(date);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the date from the database for this member's Health Progress:");
                    logger.error(ex.toString());
                }
                
                healthProgresses.add(healthProgress);                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        return healthProgresses;
        
    }    
    
    public ArrayList<CalorieRecord> getCalorieRecords(int userId) {
        
        Map<Date, Double> calorieDateMap = new HashMap<Date, Double>();
        ArrayList<CalorieRecord> calorieRecords = new ArrayList<CalorieRecord>();
        
        //Get calories gained
        
        String sql = "SELECT ME.DATE, SUM(ME.CALORIESGAINED) AS \"CALORIES\" " +
                     "FROM MEALEVENT ME " +
                     "WHERE ME.USERID = " + userId + " " +
                     "GROUP BY ME.DATE " +
                     "ORDER BY ME.DATE ASC";        
        
        logger.debug("SQL to execute: " + sql);
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                double calories = rs.getDouble("Calories");               
                String dateString = rs.getString("DATE");
                Date date;
                try {
                    date = LifeTrackerConstants.DB_DATE_FORMAT.parse(dateString);
                    calorieDateMap.put(date, calories);
                    //calorieRecord.setDate(date);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the date from the database for this member's Calorie Record:");
                    logger.error(ex.toString());
                }
                
                //calorieRecords.add(calorieRecord);                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        //Get calories burned
        
        String sql2 = "SELECT EE.DATE, SUM(EE.CALORIESBURNED) * -1 AS \"CALORIES\" " +
                      "FROM EXERCISEEVENT EE " +
                      "WHERE EE.USERID = " + userId + " " +
                      "GROUP BY EE.DATE " +
                      "ORDER BY EE.DATE ASC";                
        logger.debug("SQL to execute: " + sql);
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql2)) {
            
            while (rs.next()) {
                double calories = rs.getDouble("Calories");
                String dateString = rs.getString("DATE");
                Date date;
                try {
                    date = LifeTrackerConstants.DB_DATE_FORMAT.parse(dateString);
                    if(calorieDateMap.containsKey(date)){
                        calorieDateMap.put(date, calorieDateMap.get(date) + calories);
                    }
                    else{
                        calorieDateMap.put(date, calories);
                    }
                    
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the date from the database for this member's Calorie Record:");
                    logger.error(ex.toString());
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        //At this point, I have all the calories gained and calories burned
        //Now create the calorie record and add to the calorieRecords array list
        
        for(Map.Entry<Date, Double> entry : calorieDateMap.entrySet()){
            Date calorieDate = entry.getKey();
            Double calories = entry.getValue();
            
            CalorieRecord calorieRecord = new CalorieRecord(calorieDate, calories);
            calorieRecords.add(calorieRecord);
        }
        
        return calorieRecords;        
    }       
    
    public ArrayList<ExerciseEvent> getExerciseEvents(int userId) {
        
        ArrayList<ExerciseEvent> exerciseEvents = new ArrayList<ExerciseEvent>();
        
        String sql = "SELECT EXERCISEID, DATE, CALORIESBURNED, DURATIONMINUTES FROM EXERCISEEVENT WHERE USERID = " + userId + " ORDER BY DATE DESC";
        
        logger.debug("SQL to execute: " + sql);
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                //TODO:
                ExerciseEvent ee = new ExerciseEvent();
                
                int exerciseId = rs.getInt("EXERCISEID");
                ee.setExerciseId(exerciseId);
                
                String dateString = rs.getString("DATE");
                Date date;
                try {
                    date = LifeTrackerConstants.DB_DATE_FORMAT.parse(dateString);
                    ee.setExerciseDate(date);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the date from the database for this member's Exercise Events:");
                    logger.error(ex.toString());
                }
                
                double caloriesBurned = rs.getDouble("CALORIESBURNED");
                ee.setCaloriesBurned(caloriesBurned);
                
                double durationInMinutes = rs.getDouble("DURATIONMINUTES");
                ee.setDurationMinutes(durationInMinutes);                
                
                exerciseEvents.add(ee);                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        return exerciseEvents;        
    }           
    
    public ArrayList<MealEvent> getMealEvents(int userId) {
        
        ArrayList<MealEvent> mealEvents = new ArrayList<MealEvent>();
        
        String sql = "SELECT MEALID, DATE, CALORIESGAINED, SERVINGSIZE FROM MEALEVENT WHERE USERID = " + userId + " ORDER BY DATE DESC";
        
        logger.debug("SQL to execute: " + sql);
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                //TODO:
                MealEvent me = new MealEvent();
                
                int mealId = rs.getInt("MEALID");
                me.setMeal(getMeal(mealId));
                
                String dateString = rs.getString("DATE");
                Date date;
                try {
                    date = LifeTrackerConstants.DB_DATE_FORMAT.parse(dateString);
                    me.setMealDate(date);
                } catch (ParseException ex) {
                    logger.error("An error occurred when getting the date from the database for this member's Exercise Events:");
                    logger.error(ex.toString());
                }
                
                double caloriesGained = rs.getDouble("CALORIESGAINED");
                me.setCaloriesGained(caloriesGained);
                
                double servingSize = rs.getDouble("SERVINGSIZE");
                me.setServingSize(servingSize);                
                
                mealEvents.add(me);                
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        return mealEvents;        
    }         
    
    
    public ArrayList<Exercise> getAllExercises() {
        
        ArrayList<Exercise> allExercises = new ArrayList<Exercise>();
        
        String sql = "select ID, EXERCISENAME, CALORIES, DURATIONMINUTES FROM EXERCISE ORDER BY ID ASC";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            logger.debug("Loading exercises from database ...");
            int i = 0;
            
            while (rs.next()) {
                
                i++;
                logger.debug("Loading exercise: " + i);
                
                int exerciseId = rs.getInt("ID");
                String exerciseName = rs.getString("EXERCISENAME");
                double caloriesBurned = rs.getDouble("CALORIES");
                double durationInMinutes = rs.getDouble("DURATIONMINUTES");

                Exercise exercise = new Exercise(exerciseId, exerciseName, durationInMinutes, caloriesBurned);
                allExercises.add(exercise);                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        logger.debug("Loading exercises from database and found " + allExercises.size() + " exercises");
        
        return allExercises;
        
    }       
    
    public Meal getMeal(int mealId) {
        
        Meal meal = new Meal();
        
        String sql = "select ID, LONG_NAME, PROTEIN, CARB, FIBER, FAT, ENERGY, SERVINGSIZE, SERVINGSIZEUNIT FROM FOOD WHERE ID = " + mealId + " ORDER BY LONG_NAME ASC";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            //logger.debug("Getting meal for meal id " + mealId + " from database ...");
            
            while (rs.next()) {

                String mealName = rs.getString("LONG_NAME");
                double protein = rs.getDouble("PROTEIN");
                double carb = rs.getDouble("CARB");
                double fiber = rs.getDouble("FIBER");
                double fat = rs.getDouble("FAT");
                double energy = rs.getDouble("ENERGY");
                double servingSize = rs.getDouble("SERVINGSIZE");
                String servingSizeUnit = rs.getString("SERVINGSIZEUNIT");                              

                meal = new Meal(mealId, mealName, protein, carb, fiber, fat, energy, servingSize, servingSizeUnit);
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        //logger.debug("Loaded meal for meal id " + mealId + " from database");
        
        return meal;        
    }    
    
    public ArrayList<Meal> getAllMeals() {
        
        ArrayList<Meal> allMeals = new ArrayList<Meal>();
        
        String sql = "select ID, LONG_NAME, PROTEIN, CARB, FIBER, FAT, ENERGY, " + 
                "SERVINGSIZE, SERVINGSIZEUNIT FROM FOOD ORDER BY LONG_NAME ASC";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            
            logger.debug("Loading meals from database ...");
            //int i = 0;
            
            while (rs.next()) {
                
                //i++;
                //logger.debug("Loading meal: " + i);
                
                int mealId = rs.getInt("ID");
                String mealName = rs.getString("LONG_NAME");
                double protein = rs.getDouble("PROTEIN");
                double carb = rs.getDouble("CARB");
                double fiber = rs.getDouble("FIBER");
                double fat = rs.getDouble("FAT");
                double energy = rs.getDouble("ENERGY");
                double servingSize = rs.getDouble("SERVINGSIZE");
                String servingSizeUnit = rs.getString("SERVINGSIZEUNIT");                              

                Meal meal = new Meal(mealId, mealName, protein, carb, fiber, 
                                     fat, energy, servingSize, servingSizeUnit);
                allMeals.add(meal);                
            }
            
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }        
        
        logger.debug("Loading meals from database and found " + allMeals.size() + " meals");
        
        return allMeals;
        
    }

    public boolean addNewMeal(String mealName, double protein, double carbs, double fiber, 
            double fat, double energy, double servingSize, String servingSizeUnit) {
        
        boolean status = true;
        
        String sql = "INSERT INTO FOOD (Long_Name, PROTEIN, CARB, FIBER, FAT, " + 
                "ENERGY, SERVINGSIZE, SERVINGSIZEUNIT) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        logger.debug("About to add new meal: " + mealName);
        
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, mealName);
            pstmt.setDouble(2, protein);
            pstmt.setDouble(3, carbs);
            pstmt.setDouble(4, fiber);
            pstmt.setDouble(5, fat);
            pstmt.setDouble(6, energy);
            pstmt.setDouble(7, servingSize);
            pstmt.setString(8, servingSizeUnit);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            logger.error("Error when adding new meal: " + ex.getMessage());
            status = false;
        }
        
        return status;
    }    
    
    
    public boolean addNewMealEvent(int memberId, int mealId, Date mealDate, double caloriesGained, double servingSize){
        
        boolean status = true;
        
        String sql = "INSERT INTO MEALEVENT (USERID, MEALID, DATE, CALORIESGAINED, SERVINGSIZE) VALUES (?, ?, ?, ?, ?)";
        
        String mealDateAsString = LifeTrackerConstants.DB_DATE_FORMAT.format(mealDate);
        
        logger.debug("About to add new meal event: " + mealId + ", date: " + mealDate + " caloriesGained: " + caloriesGained + ", serving size: " + servingSize);
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, mealId);
            pstmt.setString(3, mealDateAsString);
            pstmt.setDouble(4, caloriesGained);
            pstmt.setDouble(5, servingSize);
            pstmt.executeUpdate();
        }
        catch (SQLException ex) {
            logger.error("Error when adding new meal: " + ex.getMessage());
            status = false;
        }
        
        return status;
        
    }    
    
    public boolean deleteMember(String memberUserName){
        
        boolean success = true;
        
        //get the user id as we need this for deleting from tables other than user
        int userId = -1;
        String sqlGetUserID = "select ID FROM USER WHERE NAME = \"" + memberUserName + "\"";
        
        try (Connection conn = this.connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sqlGetUserID)) {
            
            while (rs.next()) {        
                userId = rs.getInt("ID");
                logger.debug("User ID to delete: " + userId);
            }
        } catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }              
        
        String sqlDeleteUser = "delete from user where id = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteUser)) {
            pstmt.setInt(1, userId);
            int row = pstmt.executeUpdate();
            logger.debug("Deleted " + row + " row(s) from user");
        }
        catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }
        
        String sqlDeleteUserHealth = "delete from userhealth where userid = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteUserHealth)) {
            pstmt.setInt(1, userId);
            int row = pstmt.executeUpdate();
            logger.debug("Deleted " + row + " row(s) from userhealth");
        }
        catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }        
        
        String sqlDeleteUpdateHealth = "delete from updatehealth where userid = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteUpdateHealth)) {
            pstmt.setInt(1, userId);
            int row = pstmt.executeUpdate();
            logger.debug("Deleted " + row + " row(s) from updatehealth");
        }
        catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }                
        
        String sqlDeleteMealEvent = "delete from mealevent where userid = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteMealEvent)) {
            pstmt.setInt(1, userId);
            int row = pstmt.executeUpdate();
            logger.debug("Deleted " + row + " row(s) from mealevent");
        }
        catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }                        

        String sqlDeleteExerciseEvent = "delete from exerciseevent where userid = ?";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sqlDeleteExerciseEvent)) {
            pstmt.setInt(1, userId);
            int row = pstmt.executeUpdate();
            logger.debug("Deleted " + row + " row(s) from exerciseevent");
        }
        catch (SQLException ex) {
            logger.error("Error when deleting member: " + ex.getMessage());
            return false;
        }                                
        
        return success;
    }
}