package ia.app.ltia.viewcontroller.meal;

import ia.app.ltia.model.Exercise;
import ia.app.ltia.model.Meal;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

/**
 *
 * @author
 */
public class MealConverter extends ObjectToStringConverter {

    // http://wibawa-programmer.blogspot.com/2011/05/tutorial-autocomplete-dengan-swingx.html
    
    @Override
    public String[] getPossibleStringsForItem(Object item) {
        if (item == null) {
            return null;
        }
        if (!(item instanceof Meal)) {
            return new String[0];
        }
        Meal meal = (Meal) item;
        return new String[]{
                    meal.toString(), meal.getMealName(), Integer.toString(meal.getMealId())
                };
    }

    public String getPreferredStringForItem(Object item) {
        String[] possible = getPossibleStringsForItem(item);
        String preferred = null;
        if (possible != null && possible.length > 0) {
            preferred = possible[0];
        }
        return preferred;
    }        
}
