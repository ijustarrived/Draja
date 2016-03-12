package retroroots.alphadraja.CanisterEngine.Android.Application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by devilsLap on 3/12/2016.
 */
public class ActivityConfig
{
    /**
     * Creates a new activity and finishes previous one(if desired)
     *
     * @param activityClass
     * @param context could be 'this'
     * @param finishPreviousActivity flag that indicates if the previous Activity should finish
     */
    public static void CreateActivity(Class activityClass, Context context,
                                      boolean finishPreviousActivity)
    {
        Intent intent = new Intent(context, activityClass);

        if (finishPreviousActivity)
        {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }

        context.startActivity(intent);
    }

}
