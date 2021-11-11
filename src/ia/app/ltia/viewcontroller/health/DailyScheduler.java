package ia.app.ltia.viewcontroller.health;

import ia.app.ltia.LifeTrackerConstants;
import ia.app.ltia.mail.Mailer;
import ia.app.ltia.model.Member;
import ia.app.ltia.model.algo.WeightChallenge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DailyScheduler implements Runnable {
    
    private static final Logger logger = LogManager.getLogger(DailyScheduler.class);
    private Member member;
    private HealthViewController healthViewController;

    public DailyScheduler(Member member, HealthViewController healthViewController) {
        logger.info("In the MailScheduler for member: " + member.getUsername());
        this.member = member;
        this.healthViewController = healthViewController;
    }    
    
    @Override
    public void run() {
        logger.info("****** Running the Daily Scheduler Thread ******");

        //Instantiate weight challenge with this member
        WeightChallenge weightChallenge = new WeightChallenge(member);
        
        //Prepare the email subject and message parts
        String emailSubject = "LTIA: Important Update on your Goal!";        
        String emailMessagePart1 = "Hi " + member.getUsername() + "\n\n";
        String emailMessagePart2 = weightChallenge.getChallengeCompleteStatement();
        String emailMessagePart3 = "\n\nThanks\nZain";
        
        //Consolidate the email parts into one email message
        String emailMessage = emailMessagePart1 + emailMessagePart2 + emailMessagePart3;
        
        //Send the email!
        Mailer.sendMail(LifeTrackerConstants.MAIL_HOST, 
                LifeTrackerConstants.MAIL_PORT,
                LifeTrackerConstants.MAIL_FROM_EMAIL_ADDRESS,
                LifeTrackerConstants.MAIL_FROM_PASSWORD,
                member.getEmail(), 
                emailSubject, 
                emailMessage);   
        
        //Now refresh the screen by setting the member again. This will re-calculate all the screen statements
        healthViewController.setMember(this.member);        
    }    
}
