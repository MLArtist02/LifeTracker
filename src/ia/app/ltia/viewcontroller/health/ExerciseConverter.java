package ia.app.ltia.viewcontroller.health;

import ia.app.ltia.model.Exercise;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

/**
 *
 * @author
 */
public class ExerciseConverter extends ObjectToStringConverter {

    // http://wibawa-programmer.blogspot.com/2011/05/tutorial-autocomplete-dengan-swingx.html
    
    @Override
    public String[] getPossibleStringsForItem(Object item) {
        if (item == null) {
            return null;
        }
        if (!(item instanceof Exercise)) {
            return new String[0];
        }
        Exercise exercise = (Exercise) item;
        return new String[]{
                    exercise.toString(), exercise.getExerciseName(), Integer.toString(exercise.getExerciseId())
                };
    }

    @Override
    public String getPreferredStringForItem(Object item) {
        String[] possible = getPossibleStringsForItem(item);
        String preferred = null;
        if (possible != null && possible.length > 0) {
            preferred = possible[0];
        }
        return preferred;
    }        
}
