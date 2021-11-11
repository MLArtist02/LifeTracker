package ia.app.ltia;

import ia.app.ltia.viewcontroller.HomeViewController;

/**
 *
 * @author
 */
public class LifeTrackerMain {
   
    //https://stackoverflow.com/Questions/5217611/the-mvc-pattern-and-swing
    
    public static void main (String[] args){
   
        HomeViewController homeform = new HomeViewController();
        homeform.setVisible(true);
   
    }
    
}
