package ia.app.ltia.model.algo;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.LifeTrackerHelper;
import ia.app.ltia.model.Member;
import ia.app.ltia.model.MemberHealth;
import java.text.DecimalFormat;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author
 */
public class WeightChallenge {
    
    private static final Logger logger = LogManager.getLogger(WeightChallenge.class);

    private Date startDate;
    private Date targetDate;
    private Date today;
    
    private double startWeight;
    private double targetWeight;
    private double todayWeight;
    
    private double totalNumberOfDaysInChallenge;
    private double totalNumberOfDaysPassedInChallenge;    
    
    private double averageTargetWeightChangePerDay;
    private double averageActualWeightChangePerDay;
    private Member member;
    
    private enum ChallengeProgress {        
        CANNOT_CALCULATE_ON_TRACK,
        COMPLETED, 
        VERY_ON_TRACK, 
        ON_TRACK,
        NOT_ON_TRACK, 
        VERY_NOT_ON_TRACK
    }
    
    private ChallengeProgress challengeProgress;
    
    public WeightChallenge(Member member) {
        
        this.member = member;
        MemberHealth mh = member.getMemberHealth();
        startDate = mh.getStartDate();
        targetDate = mh.getTargetDate();
        today = LifeTrackerHelper.getToday();
        startWeight = mh.getWeight();
        targetWeight = mh.getTargetWeight();
        todayWeight = LifeTrackerHelper.getLatestWeight(this.member);
        
        totalNumberOfDaysPassedInChallenge = 
                LifeTrackerHelper.getDateDifferenceInDays(startDate, today);
        totalNumberOfDaysInChallenge = 
                LifeTrackerHelper.getDateDifferenceInDays(startDate, targetDate);
        
        averageTargetWeightChangePerDay = 
            (targetWeight - startWeight) / getTotalNumberOfDaysInChallenge();
        
        averageActualWeightChangePerDay = 
            (todayWeight - startWeight) / getTotalNumberOfDaysPassedInChallenge();

        calculateChallengeProgress();        
    }
    
    private void calculateChallengeProgress() {        
        
        logger.info("Calculating weight challenge progress");
        logger.info("averageTargetWeightChangePerDay:" + averageTargetWeightChangePerDay);
        logger.info("averageActualWeightChangePerDay:" + averageActualWeightChangePerDay);
        
        challengeProgress = ChallengeProgress.CANNOT_CALCULATE_ON_TRACK;
        
        if (todayWeight == targetWeight) {
            challengeProgress = ChallengeProgress.COMPLETED;
        }
        else {
            double challengeProgressScore = 
                    (((averageActualWeightChangePerDay - averageTargetWeightChangePerDay)/averageTargetWeightChangePerDay)*100);

            if ( challengeProgressScore >= -1 && challengeProgressScore <= 10 ) {
                //the difference between target and actual change is -1 to 10%
                challengeProgress = ChallengeProgress.ON_TRACK;
            }
            else if ( challengeProgressScore > 10 ){
                challengeProgress = ChallengeProgress.VERY_ON_TRACK;
            }
            else if ( challengeProgressScore < -1 && challengeProgressScore >= -10 ){
                challengeProgress = ChallengeProgress.NOT_ON_TRACK;
            }
            else if ( challengeProgressScore < -10){
                challengeProgress = ChallengeProgress.VERY_NOT_ON_TRACK;
            }      

            logger.info("-> challenge progress: " + challengeProgress.toString());                
        }
    }
    
    public String getWeightLoseOrGainOrMaintain() {
        
        String loseOrGainOrMaintain = "";
        double weightChange = this.startWeight - this.targetWeight;
        
        if(weightChange > 0){
            loseOrGainOrMaintain = LifeTrackerConstants.WEIGHT_GOAL_LOSE;
        }            
        else if(weightChange < 0){
            loseOrGainOrMaintain = LifeTrackerConstants.WEIGHT_GOAL_GAIN;
        }
        else if(weightChange == 0){
            loseOrGainOrMaintain = LifeTrackerConstants.WEIGHT_GOAL_MAINTAIN;
        }            
        
        return loseOrGainOrMaintain;
    }
    
    private double getTargetWeightChange() {
        return targetWeight - startWeight;
    }

    public double getTotalNumberOfDaysInChallenge() {
        return totalNumberOfDaysInChallenge;
    }

    public double getTotalNumberOfDaysPassedInChallenge() {
        return totalNumberOfDaysPassedInChallenge;
    }
    
    public String getDayOfChallengeStatement(){
        
        DecimalFormat df = new DecimalFormat("###");
        String s = "You are on day " + df.format(totalNumberOfDaysPassedInChallenge) + " of the " + df.format(totalNumberOfDaysInChallenge) + " day challenge"; 

        return s;
    }
    
    public String getDayOfChallengeAndCurrentWeightStatement() {        
        String part1 = getDayOfChallengeStatement();
        String part2 = " and currently weigh " + todayWeight + " kg";    
        
        return part1 + part2;
    }
    
    public String getChallengeProgressStatement() {
        String part1 = "Challenge ";
        String part2 = "";
        
        switch(challengeProgress) {
            case CANNOT_CALCULATE_ON_TRACK:
                part2 = "status cannot be calculated. Insufficient or bad data.";
                break;
                
            case COMPLETED:
                part2 = "is COMPLETED!\nCongratulations for meeting your target!";
                break;                
                
            case VERY_ON_TRACK:
                part2 = "progress is VERY GOOD!\nAt this rate, you are expected to achieve your goal much earlier than the target date.";
                break;
                
            case ON_TRACK:
                part2 = "progress is GOOD!\nYou are expected to achieve your goal by the target date.";
                break;
            
            case NOT_ON_TRACK:
                part2 = "is not on track.\nAt this rate, you will miss your goal by the target date. But with more focus, you can still recover.";
                break;
                
            case VERY_NOT_ON_TRACK:
                part2 = "progress is NOT GOOD.\nYou are going to miss your goal by the target date. " + getVeryNotOnTrackExtraStatement();
                break;           
        }
        
        return part1 + part2;        
    }
    
    private String getVeryNotOnTrackExtraStatement(){
        
        String s = "";
        
        if(this.targetWeight < this.startWeight) {
            //means need to lose weight
            
            //get how many days left in the challenge
            double daysLeft = this.totalNumberOfDaysInChallenge - this.totalNumberOfDaysPassedInChallenge;
            
            //get how much weight left to lose
            double weightLossNeeded = this.targetWeight - this.todayWeight;
            
            //now get the average weight loss per week needed to meet the target
            double weeklyWeightLossNeeded = weightLossNeeded / (daysLeft/7);
            
            if(weeklyWeightLossNeeded <= LifeTrackerConstants.WEIGHT_LOSS_KG_SAFE_WEEKLY_LIMIT){
                s = "But there is still time to safely lose weight if you focus on losing up to " + 
                        LifeTrackerConstants.WEIGHT_LOSS_CALORIES_SAFE_WEEKLY_LIMIT + " calories a week.";
            }
            else {
                s = "Unfortunately there is not enough time to safely lose weight.";
            }
        }
        else if(this.targetWeight > this.startWeight) {
            //means need to gain weight
            
            //for now apply the same logic as weight loss but this needs to be reviewed 
            //get how many days left in the challenge
            double daysLeft = this.totalNumberOfDaysInChallenge - this.totalNumberOfDaysPassedInChallenge;
            
            //get how much weight left to gain
            double weightGainNeeded = this.targetWeight - this.todayWeight;
            
            //now get the average weight loss per week needed to meet the target
            double weeklyWeightGainNeeded = weightGainNeeded / (daysLeft/7);
            
            if(weeklyWeightGainNeeded <= LifeTrackerConstants.WEIGHT_LOSS_KG_SAFE_WEEKLY_LIMIT){
                s = "But there is still time to safely gain weight if you focus on gaining up to " + 
                        LifeTrackerConstants.WEIGHT_LOSS_CALORIES_SAFE_WEEKLY_LIMIT + " calories a week.";
            }
            else {
                s = "Unfortunately there is not enough time to safely gain weight.";
            }
            
        }
        
        return s;        
    }

    public String getChallengeCompleteStatement() {

        String part1 = getChallengeBriefStatement() + " over " + this.totalNumberOfDaysInChallenge + " days starting on " + 
                LifeTrackerConstants.GUI_DATE_FORMAT.format(this.startDate) + 
                " and ending on " + LifeTrackerConstants.GUI_DATE_FORMAT.format(this.targetDate);   
        
        String part2 = getDayOfChallengeAndCurrentWeightStatement();
        String part3 = getChallengeProgressStatement();
        
        return part1 + "\n" + part2 + "\n" + part3;
    }
    
    public String getChallengeBriefStatement() {
        String s = "Goal is to " + this.getWeightLoseOrGainOrMaintain() + " " + Math.abs(this.getTargetWeightChange()) +  " kg of weight, " + 
                "from " + this.startWeight + " kg to " + this.targetWeight + " kg";
        
        return s;
    }
    
    public String getCurrentWeightStatement(){
        return "You currently weigh " + this.todayWeight + " kg";  
    }
}
