package ia.app.ltia.model.algo;

import java.lang.Math;

/**
 *
 * @author
 */
public class BMICalculator {

    private double weightInKgs;
    private double heightInCentimetres;    
    private double bmi;
    
    public double getWeight() {
        return weightInKgs;
    }

    public void setWeight(double weightInKgs) {
        this.weightInKgs = weightInKgs;
    }

    public double getHeight() {
        return this.heightInCentimetres;
    }
    
    public void setHeight(double heightInCentimetres) {
        this.heightInCentimetres = heightInCentimetres;
    }

    public double getBMI() {
        double heightInMetres = heightInCentimetres/100;
        bmi = weightInKgs / Math.pow(heightInMetres, 2.0);
        return bmi;
    }
    
    public String getBMIJudgement() {
        if (bmi < 18.5 ) {
            return "Underweight";
        }
        else if (bmi >= 18.5 && bmi < 25) {
            return("Healthy");
        }
        else if (bmi >= 25 && bmi < 30) {
            return("Overweight");
        }
        else if (bmi >= 30) {
            return("Obese");
        }
        return null;
    }
}
