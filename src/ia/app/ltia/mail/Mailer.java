package ia.app.ltia.mail;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mailer {    
    private static final Logger logger = LogManager.getLogger(Mailer.class);
    
    public static void sendMail(String host, String port, String fromEmailAddress, String password, String toEmailAddress, String emailSubject, String emailMessage) {        
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true"); //TLS        
        
        Session session = Session.getDefaultInstance(properties, 
                new Authenticator() {  
                    protected PasswordAuthentication getPasswordAuthentication() {  
                        return new PasswordAuthentication(fromEmailAddress, password); 
                    }  
                });    
        
        try {  
                logger.info("*** Attempting to send email ...");  
            
                MimeMessage message = new MimeMessage(session);  
                message.setFrom(new InternetAddress(fromEmailAddress));  
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmailAddress));  
                message.setSubject(emailSubject);  
                message.setText(emailMessage);  
       
                //send the message  
                 Transport.send(message);  
  
                 logger.info("*** Email message sent successfully!");  
            } 
        catch (MessagingException e) {
            logger.error(e.getMessage());
        }  
    }   
}