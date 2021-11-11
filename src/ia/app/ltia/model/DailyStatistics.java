package ia.app.ltia.model;

import ia.app.ltia.LifeTrackerConstants;
import java.util.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.table.DefaultTableModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
public class DailyStatistics {
    
    private static final Logger logger = LogManager.getLogger(DailyStatistics.class);
    
    public static DefaultTableModel getDailyStatisticsTableModelForMember(Member member) {

        logger.debug("Getting daily statistics for member:\n" + member.getUsername());
        
        DefaultTableModel statisticsTableModel = new DefaultTableModel();
        
        //This method manipulates the member data to give us its statistics in a table model format
        // We need 5 columns ...
        // Date | Calories In | Calories Out | Weight | Height
        
        String[] columnNames = {"Date", "Calories In", "Calories Out", LifeTrackerConstants.WEIGHT_LABEL, LifeTrackerConstants.HEIGHT_LABEL};
        
        statisticsTableModel.addColumn(columnNames[0]);
        statisticsTableModel.addColumn(columnNames[1]);
        statisticsTableModel.addColumn(columnNames[2]);
        statisticsTableModel.addColumn(columnNames[3]);
        statisticsTableModel.addColumn(columnNames[4]);        
        
        // Get all the dates from member's MemberHealth, HealthProgress, ExerciseEvent and MealEvent
        // into our statisticsDates list and then get unique dates        
        ArrayList<Date> statisticsDates = new ArrayList<Date>();
        
        //MemberHealth
        statisticsDates.add(member.getMemberHealth().getStartDate());        
        
        // HealthProgress
        ArrayList<HealthProgress> hpList = member.getHealthProgresses();
        Iterator i = hpList.iterator();
        
        while(i.hasNext()) {
            HealthProgress hp = (HealthProgress)i.next(); //i.next() returns Object() but i need HealthProgress so convert or "cast" Object() to HealthProgress
            statisticsDates.add(hp.getDate());            
        }
        
        // ExerciseEvent
        ArrayList<ExerciseEvent> eeList = member.getExerciseEvents();
        Iterator j = eeList.iterator();
        
        while(j.hasNext()) {
            ExerciseEvent ee = (ExerciseEvent)j.next();
            statisticsDates.add(ee.getExerciseDate());            
        }        

        // MealEvent
        ArrayList<MealEvent> meList = member.getMealEvents();
        Iterator k = meList.iterator();
        
        while(k.hasNext()) {
            MealEvent me = (MealEvent)k.next();
            statisticsDates.add(me.getMealDate());            
        }        
        
        //statisticsDates now has all the dates ... sort it and then get the unique dates
        // https://stackoverflow.com/questions/28028102/how-to-keep-arraylist-of-date-stay-sorted-at-all-times
        
        //1st sort by date in descending order
        Collections.sort(statisticsDates, Collections.reverseOrder());
        
        //2nd get unique dates into a new collection which here is Set, we use our sorted list as the source of data
        Set<Date> uniqueDates = new TreeSet<Date>(statisticsDates);        
        
        logger.debug("Daily Statistics unique dates found: " + uniqueDates.size());
        
        // We need 5 columns in the table
        // Date | Calories In | Calories Out | Weight | Height
        
        String[] statisticsRowValues = new String[5]; 
        
        //Now loop over each date and get the statistics we need
        
        int weight = 0;
        int height = 0;
        
        Iterator x = uniqueDates.iterator();
        while(x.hasNext()) {
            
            //column 1 is the date
            Date activityDate = (Date)x.next();                       
            statisticsRowValues[0] = LifeTrackerConstants.GUI_DATE_FORMAT.format(activityDate);
            
            //logger.debug("Daily statistics, getting data for date: " + activityDate);
                
            //column 2 is the calories in (meal events)
            double caloriesIn = 0;
            ArrayList<MealEvent> meList2 = member.getMealEvents();
            Iterator mealIterator = meList2.iterator();
        
            while(mealIterator.hasNext()) {
                MealEvent me = (MealEvent)mealIterator.next();
                
                //logger.debug("Checking activity date: " + activityDate + " versus ME: " + me.toString());
                
                if(me.getMealDate().equals(activityDate)){
                    caloriesIn = caloriesIn + me.getCaloriesGained();
                }
            }        
            
            DecimalFormat df = new DecimalFormat("###,###.##");
            
            statisticsRowValues[1] = df.format(caloriesIn);
            
            //column 3 is the calories out (exercise events)
            double caloriesOut = 0;
            ArrayList<ExerciseEvent> eeList2 = member.getExerciseEvents();
            Iterator exerciseIterator = eeList2.iterator();
        
            while(exerciseIterator.hasNext()) {
                ExerciseEvent ee = (ExerciseEvent)exerciseIterator.next();
                
                //logger.debug("Checking activity date: " + activityDate + " versus EE: " + ee.toString());
                
                if(ee.getExerciseDate().equals(activityDate)){
                    caloriesOut = caloriesOut + ee.getCaloriesBurned();
                }
            }        
            
            statisticsRowValues[2] = df.format(caloriesOut);
            
            //column 4 is the weight, column 5 is the height
            //both come from MemberHealth or HealthProgress
            MemberHealth mh = member.getMemberHealth();
            if(mh.getStartDate() == activityDate){
                weight = mh.getWeight();
                height = mh.getHeight();
            }
            
            ArrayList<HealthProgress> hpList2 = member.getHealthProgresses();
            Iterator hpIterator2 = hpList2.iterator();
        
            while(hpIterator2.hasNext()) {
                HealthProgress hp = (HealthProgress)hpIterator2.next();
                
                if(hp.getDate() == activityDate){
                    weight = hp.getWeight();
                    height = hp.getHeight();
                    
                }
            }        
            
            statisticsRowValues[3] = Integer.toString(weight);
            statisticsRowValues[4] = Integer.toString(height);
            
            String[] statisticsSingleRow = { statisticsRowValues[0], statisticsRowValues[1], statisticsRowValues[2], statisticsRowValues[3], statisticsRowValues[4] };
            
            statisticsTableModel.addRow(statisticsSingleRow);
            
        }        
        
        logger.debug("Daily Statistics table model column count: " + statisticsTableModel.getColumnCount() );
        logger.debug("Daily Statistics table model row count: " + statisticsTableModel.getRowCount() );
        
        return statisticsTableModel;
    }    
}
